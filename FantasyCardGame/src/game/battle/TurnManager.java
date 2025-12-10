package game.battle;

import java.util.List;

/**
 * 하스스톤 스타일의 턴 시스템을 관리하는 클래스입니다.
 *
 * 책임:
 * - 턴 시작/종료 처리
 * - 마나 증가 및 보충
 * - 드로우 요청 (피로 포함)
 * - 유닛 공격 가능 상태 초기화
 * - 턴 시간 제한(예: 60초) 관리
 */
public class TurnManager {

    private final GameState gameState;

    public TurnManager(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("GameState 는 null 일 수 없습니다.");
        }
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    /**
     * 현재 턴의 턴 시작 처리.
     * - 마나 최대치 증가 (최대 10)
     * - 마나 풀 충전
     * - 카드 1장 드로우 (피로 규칙 포함)
     * - 유닛 상태 초기화
     * - 턴 시간 제한 설정 및 시작 시각 기록
     */
    public BattleLog startTurn() {
        BattleLog log = new BattleLog();

        gameState.setTurnPhase(TurnPhase.START);
        BattleSide side = gameState.getCurrentTurnSide();
        PlayerBattleState current = gameState.getCurrentPlayerState();

        // 마나 증가 + 보충
        int newMaxMana = Math.min(10, current.getMaxMana() + 1);
        current.setMaxMana(newMaxMana);
        current.setCurrentMana(newMaxMana);
        log.add("[턴 시작] " + side + " 의 마나가 " + newMaxMana + " 로 설정되었습니다.");

        // 턴 시간 제한 시작 (GameState 에 이미 기본값 60초가 있음)
        gameState.markTurnStartNow();
        long limitSec = gameState.getTurnTimeLimitMillis() / 1000L;
        log.add("[턴 제한] 이번 턴 제한 시간은 " + limitSec + "초입니다.");

        // 드로우 (피로 규칙 포함)
        List<String> drawLogs = current.drawCardWithFatigue();
        log.addAll(drawLogs);

        // 유닛 상태 초기화
        List<String> stateLogs = current.onTurnStart();
        log.addAll(stateLogs);

        // MAIN 페이즈로 전환
        gameState.setTurnPhase(TurnPhase.MAIN);
        log.add("[턴 정보] 이제 메인 페이즈입니다.");

        return log;
    }

    /**
     * 현재 턴을 종료합니다.
     */
    public BattleLog endTurn() {
        BattleLog log = new BattleLog();

        gameState.setTurnPhase(TurnPhase.END);
        BattleSide side = gameState.getCurrentTurnSide();
        log.add("[턴 종료] " + side + " 의 턴이 종료되었습니다.");

        gameState.advanceTurn();
        log.add("[턴 전환] 다음 턴은 " + gameState.getCurrentTurnSide() +
                " 의 턴입니다. (턴 번호=" + gameState.getTurnNumber() + ")");

        return log;
    }

    /**
     * 남은 턴 시간(ms)를 반환합니다.
     */
    public long getRemainingTurnTimeMillis() {
        return gameState.getRemainingTurnTimeMillis();
    }

    /**
     * 턴 시간이 초과되었는지 여부를 반환합니다.
     */
    public boolean isTurnTimeOver() {
        return gameState.isTurnTimeOver();
    }

    /**
     * 턴 시간이 초과된 경우, 턴을 강제로 종료합니다.
     * 시간이 남아 있다면 아무 일도 하지 않고 빈 BattleLog 를 반환합니다.
     */
    public BattleLog forceEndTurnIfTimeOver() {
        BattleLog log = new BattleLog();
        if (!isTurnTimeOver()) {
            return log;
        }

        log.add("[턴 제한] 제한 시간을 초과하여 턴을 강제로 종료합니다.");
        BattleLog endLog = endTurn();
        for (BattleLogEntry entry : endLog.getEntries()) {
            log.add(entry.getMessage());
        }
        return log;
    }
}
