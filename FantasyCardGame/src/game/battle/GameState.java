package game.battle;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 전투 화면에 존재하는 모든 상태를 담는 클래스입니다.
 *
 * - 두 플레이어(PLAYER, ENEMY)의 PlayerBattleState
 * - 현재 턴을 진행 중인 쪽(BattleSide)
 * - 현재 턴 번호, 현재 턴 Phase
 * - 턴 시간 제한(예: 60초) 및 턴 시작
 */
public class GameState {

    private final Map<BattleSide, PlayerBattleState> players = new EnumMap<>(BattleSide.class);

    private BattleSide currentTurnSide;
    private int turnNumber;
    private TurnPhase turnPhase;

    /** 현재 턴이 시작된 시각 (millis) */
    private long turnStartTimeMillis;

    /** 턴 시간 제한 (millis) - 기본 60초 */
    private long turnTimeLimitMillis;

    public GameState(PlayerBattleState playerState, PlayerBattleState enemyState) {
        players.put(BattleSide.PLAYER, playerState);
        players.put(BattleSide.ENEMY, enemyState);

        this.currentTurnSide = BattleSide.PLAYER;
        this.turnNumber = 1;
        this.turnPhase = TurnPhase.START;

        this.turnTimeLimitMillis = 60_000L; // 60초
        this.turnStartTimeMillis = System.currentTimeMillis();
    }

    public PlayerBattleState getPlayerState(BattleSide side) {
        return players.get(side);
    }

    public PlayerBattleState getCurrentPlayerState() {
        return players.get(currentTurnSide);
    }

    public PlayerBattleState getOpponentPlayerState() {
        return players.get(currentTurnSide.getOpponent());
    }

    public BattleSide getCurrentTurnSide() {
        return currentTurnSide;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    public void setTurnPhase(TurnPhase phase) {
        this.turnPhase = phase;
    }

    public long getTurnStartTimeMillis() {
        return turnStartTimeMillis;
    }

    public void markTurnStartNow() {
        this.turnStartTimeMillis = System.currentTimeMillis();
    }

    public long getTurnTimeLimitMillis() {
        return turnTimeLimitMillis;
    }

    public void setTurnTimeLimitMillis(long turnTimeLimitMillis) {
        this.turnTimeLimitMillis = Math.max(1_000L, turnTimeLimitMillis); // 최소 1초
    }

    /**
     * 남은 턴 시간(ms)을 반환합니다.
     */
    public long getRemainingTurnTimeMillis() {
        long elapsed = System.currentTimeMillis() - turnStartTimeMillis;
        long remaining = turnTimeLimitMillis - elapsed;
        return Math.max(0, remaining);
    }

    /**
     * 턴 시간 제한을 초과했는지 여부를 반환합니다.
     */
    public boolean isTurnTimeOver() {
        return getRemainingTurnTimeMillis() <= 0;
    }

    /**
     * 현재 게임이 종료(누군가 영웅 사망) 상태인지 여부를 반환합니다.
     */
    public boolean isGameOver() {
        return getPlayerState(BattleSide.PLAYER).getHero().isDead() ||
               getPlayerState(BattleSide.ENEMY).getHero().isDead();
    }

    /**
     * 승리한 쪽을 반환합니다.
     * 둘 다 살아있으면 null.
     */
    public BattleSide getWinnerSide() {
        boolean playerDead = getPlayerState(BattleSide.PLAYER).getHero().isDead();
        boolean enemyDead = getPlayerState(BattleSide.ENEMY).getHero().isDead();

        if (playerDead && !enemyDead) {
            return BattleSide.ENEMY;
        }
        if (enemyDead && !playerDead) {
            return BattleSide.PLAYER;
        }
        return null;
    }

    /**
     * 다음 턴으로 넘어갈 때 호출되는 내부 메서드입니다.
     */
    public void advanceTurn() {
        currentTurnSide = currentTurnSide.getOpponent();
        turnNumber++;
        turnPhase = TurnPhase.START;
        markTurnStartNow();
    }

    /**
     * 디버깅 또는 UI용으로 남길 수 있는 간단한 상태 문자열.
     */
    public List<String> toStatusLines() {
        List<String> lines = new ArrayList<>();
        PlayerBattleState p = players.get(BattleSide.PLAYER);
        PlayerBattleState e = players.get(BattleSide.ENEMY);

        lines.add("=== 턴 " + turnNumber + " (" + currentTurnSide +
                " 턴, phase=" + turnPhase +
                ", 남은 시간(ms)=" + getRemainingTurnTimeMillis() + ") ===");
        lines.add("[플레이어] " + p);
        lines.add("[적] " + e);

        return lines;
    }
}
