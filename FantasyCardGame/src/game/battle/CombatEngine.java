package game.battle;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 전투 행동(하수인의 공격)을 담당하는 엔진 클래스입니다.
 *
 * - 하수인 -> 하수인 공격
 * - 하수인 -> 영웅 공격
 * - 도발(TAUNT) 룰 적용
 *
 */
public class CombatEngine {

    private final GameState gameState;
    private final DamageCalculator damageCalculator;

    public CombatEngine(GameState gameState) {
        this(gameState, new DefaultDamageCalculator());
    }

    public CombatEngine(GameState gameState, DamageCalculator calculator) {
        if (gameState == null) {
            throw new IllegalArgumentException("GameState 는 null 일 수 없습니다.");
        }
        if (calculator == null) {
            throw new IllegalArgumentException("DamageCalculator 는 null 일 수 없습니다.");
        }
        this.gameState = gameState;
        this.damageCalculator = calculator;
    }

    public GameState getGameState() {
        return gameState;
    }

    /**
     * 하수인이 상대 하수인을 공격하는 행동을 수행합니다.
     */
    public BattleLog unitAttackUnit(BattleSide attackerSide,
                                    int attackerIndex,
                                    int defenderIndex) {

        BattleLog log = new BattleLog();

        PlayerBattleState atkState = gameState.getPlayerState(attackerSide);
        PlayerBattleState defState = gameState.getPlayerState(attackerSide.getOpponent());

        List<UnitState> atkBoard = atkState.getBoard();
        List<UnitState> defBoard = defState.getBoard();

        if (attackerIndex < 0 || attackerIndex >= atkBoard.size()
                || defenderIndex < 0 || defenderIndex >= defBoard.size()) {
            log.add("[공격 실패] 잘못된 유닛 인덱스입니다.");
            return log;
        }

        UnitState attacker = atkBoard.get(attackerIndex);
        UnitState defender = defBoard.get(defenderIndex);

        // 공격 가능 여부 체크
        if (!canUnitAttack(attacker, log)) {
            return log;
        }

        // 도발 룰 체크
        if (!isValidTauntTarget(defState, defender)) {
            log.add("[공격 실패] 상대가 도발 유닛을 보유 중이므로, 먼저 도발 유닛을 공격해야 합니다.");
            return log;
        }

        int damageToDef = damageCalculator.calculateUnitToUnitDamage(attacker, defender);
        int damageToAtk = damageCalculator.calculateUnitToUnitDamage(defender, attacker);

        log.add("[전투] \"" + attacker.getName() + "\" 이(가) \"" +
                defender.getName() + "\" 을(를) 공격합니다!");

        int defBefore = defender.getCurrentHealth();
        int atkBefore = attacker.getCurrentHealth();

        defender.applyDamage(damageToDef);
        attacker.applyDamage(damageToAtk);

        log.add(" -> " + defender.getName() + " 체력: " + defBefore +
                " -> " + defender.getCurrentHealth());
        log.add(" -> " + attacker.getName() + " 체력: " + atkBefore +
                " -> " + attacker.getCurrentHealth());

        attacker.incrementAttacksThisTurn();

        if (defender.isDead()) {
            log.add("[전투 결과] " + defender.getName() + " 이(가) 파괴되었습니다.");
            defBoard.remove(defenderIndex);
        }
        if (attacker.isDead()) {
            log.add("[전투 결과] " + attacker.getName() + " 이(가) 파괴되었습니다.");
            atkBoard.remove(attackerIndex);
        }

        return log;
    }

    /**
     * 하수인이 상대 영웅을 공격하는 행동을 수행합니다.
     */
    public BattleLog unitAttackHero(BattleSide attackerSide,
                                    int attackerIndex) {

        BattleLog log = new BattleLog();

        PlayerBattleState atkState = gameState.getPlayerState(attackerSide);
        PlayerBattleState defState = gameState.getPlayerState(attackerSide.getOpponent());

        List<UnitState> atkBoard = atkState.getBoard();
        if (attackerIndex < 0 || attackerIndex >= atkBoard.size()) {
            log.add("[공격 실패] 잘못된 유닛 인덱스입니다.");
            return log;
        }

        UnitState attacker = atkBoard.get(attackerIndex);

        // 공격 가능 여부 체크
        if (!canUnitAttack(attacker, log)) {
            return log;
        }

        // 도발이 있는 경우 영웅을 직접 공격할 수 없음
        if (hasTauntUnit(defState)) {
            log.add("[공격 실패] 상대 필드에 도발 유닛이 있어 영웅을 직접 공격할 수 없습니다.");
            return log;
        }

        HeroState hero = defState.getHero();

        int damage = damageCalculator.calculateUnitToHeroDamage(attacker, hero);
        int beforeHp = hero.getCurrentHealth();

        log.add("[전투] \"" + attacker.getName() + "\" 이(가) 적 영웅 \"" +
                hero.getName() + "\" 을(를) 공격합니다! (" + damage + " 피해)");

        hero.applyDamage(damage);

        log.add(" -> 영웅 체력: " + beforeHp + " -> " + hero.getCurrentHealth());

        attacker.incrementAttacksThisTurn();

        if (hero.isDead()) {
            log.add("[전투 결과] 영웅 " + hero.getName() + " 이(가) 쓰러졌습니다!");
        }

        return log;
    }

    /**
     * 하수인이 공격 가능한 상태인지 확인합니다.
     */
    private boolean canUnitAttack(UnitState attacker, BattleLog log) {
        if (attacker == null) {
            log.add("[공격 실패] 존재하지 않는 유닛입니다.");
            return false;
        }

        if (attacker.isDead()) {
            log.add("[공격 실패] 이미 파괴된 유닛입니다.");
            return false;
        }

        if (attacker.getAttacksThisTurn() >= 1) {
            log.add("[공격 실패] " + attacker.getName() + " 은(는) 이번 턴에 더 이상 공격할 수 없습니다.");
            return false;
        }

        if (attacker.isSummonedThisTurn() && !attacker.isCharge()) {
            log.add("[공격 실패] " + attacker.getName() + " 은(는) 소환된 턴에는 공격할 수 없습니다.");
            return false;
        }

        if (attacker.getAttack() <= 0) {
            log.add("[공격 실패] " + attacker.getName() + " 은(는) 공격력이 0 입니다.");
            return false;
        }

        return true;
    }

    private boolean hasTauntUnit(PlayerBattleState state) {
        return state.getBoard().stream().anyMatch(UnitState::isTaunt);
    }

    /**
     * 상대 필드에 도발 유닛이 있다면,
     * 해당 도발 유닛만 공격 가능한 타겟인지 체크합니다.
     */
    private boolean isValidTauntTarget(PlayerBattleState defenderState, UnitState target) {
        List<UnitState> taunts = defenderState.getBoard().stream()
                .filter(UnitState::isTaunt)
                .collect(Collectors.toList());

        if (taunts.isEmpty()) {
            return true;
        }

        return target != null && target.isTaunt();
    }
}
