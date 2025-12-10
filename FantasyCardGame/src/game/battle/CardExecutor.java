package game.battle;

import game.card.EffectType;
import game.card.card;
import game.card.cardEffect;

import java.util.List;

/**
 * 카드를 실제로 "사용"했을 때 발생하는 전투 관련 로직을 처리하는 클래스입니다.
 *
 * 지원:
 * - 몬스터 카드 소환 → UnitState 생성 후 전장에 배치
 * - 일부 cardEffect 처리:
 *   - TAUNT (소환된 유닛에 도발)
 *   - DIRECT_ATTACK_PLAYER (영웅에게 직접 피해)
 *   - DAMAGE_TO_MONSTER (단일 적 유닛 피해)
 *
 * 무시되는 효과 (로그만 남기고 실제 효과 없음):
 *   - HEAL_PLAYER
 *   - BUFF_ATTACK / BUFF_DEFENSE
 *   - DEBUFF_ATTACK / DEBUFF_DEFENSE
 */
public class CardExecutor {

    private final GameState gameState;

    public CardExecutor(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("GameState 는 null 일 수 없습니다.");
        }
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    /**
     * 카드를 플레이(사용)하는 메인 메서드입니다.
     */
    public BattleLog playCard(BattleSide side,
                              card card,
                              BattleSide targetSide,
                              TargetType targetType,
                              int targetIndex) {

        BattleLog log = new BattleLog();

        if (card == null) {
            log.add("[카드 사용 실패] 카드가 null 입니다.");
            return log;
        }

        PlayerBattleState casterState = gameState.getPlayerState(side);

        // 필드가 꽉 찼는지 먼저 체크 (마나 차감 전에!)
        if (!casterState.canSummonMoreUnits()) {
            log.add("[카드 사용 실패] 필드에는 최대 " +
                    PlayerBattleState.MAX_BOARD_SIZE + " 마리까지만 소환할 수 있습니다.");
            return log;
        }

        // 마나 체크
        if (casterState.getCurrentMana() < card.getCost()) {
            log.add("[카드 사용 실패] 마나가 부족하여 \"" + card.getName() +
                    "\" 카드를 사용할 수 없습니다. (필요 코스트=" + card.getCost() + ")");
            return log;
        }

        // 마나 차감
        casterState.setCurrentMana(casterState.getCurrentMana() - card.getCost());
        log.add("[카드 사용] " + side + " 이(가) \"" + card.getName() +
                "\" (코스트 " + card.getCost() + ") 카드를 사용했습니다.");

        // 몬스터 카드라 가정하고 전장에 소환
        UnitState summoned = new UnitState(card);
        summoned.applyCardEffectsOnSummon(card.getEffects()); // 도발 등만 반영

        boolean summonedOk = casterState.summonUnit(summoned);
        if (!summonedOk) {
            log.add("[소환 실패] 필드가 가득 차 있어 \"" + summoned.getName() +
                    "\" 을(를) 더 이상 소환할 수 없습니다.");
            return log;
        }

        log.add("[소환] \"" + summoned.getName() + "\" 이(가) 전장에 소환되었습니다. " +
                "(공격력=" + summoned.getAttack() +
                ", 체력=" + summoned.getCurrentHealth() + ")");

        // 효과 처리
        List<cardEffect> effects = card.getEffects();
        if (!effects.isEmpty()) {
            if (targetSide == null) {
                targetSide = side.getOpponent();
            }
            handleEffects(effects, side, targetSide, targetType, targetIndex, summoned, log);
        }

        return log;
    }

    private void handleEffects(List<cardEffect> effects,
                               BattleSide casterSide,
                               BattleSide targetSide,
                               TargetType targetType,
                               int targetIndex,
                               UnitState self,
                               BattleLog log) {

        PlayerBattleState caster = gameState.getPlayerState(casterSide);
        PlayerBattleState targetOwner = gameState.getPlayerState(targetSide);

        for (cardEffect effect : effects) {
            EffectType type = effect.getType();

            switch (type) {
                case TAUNT:
                    // 소환 시에도 처리되지만, 중복 호출에 대비해 한 번 더 로깅
                    self.setTaunt(true);
                    log.add("[효과] " + self.getName() + " 은(는) 도발(TAUNT)을 보유합니다.");
                    break;

                case DIRECT_ATTACK_PLAYER:
                    handleDirectAttackPlayer(effect, targetOwner, log);
                    break;

                case DAMAGE_TO_MONSTER:
                    handleDamageToMonster(effect, targetOwner, targetType, targetIndex, log);
                    break;

                case HEAL_PLAYER:
                case BUFF_ATTACK:
                case BUFF_DEFENSE:
                case DEBUFF_ATTACK:
                case DEBUFF_DEFENSE:
                    // 요청에 따라 이 효과들은 전부 무시
                    log.add("[효과 무시] " + type +
                            " 효과는 현재 게임 규칙에서 사용하지 않습니다.");
                    break;

                default:
                    log.add("[효과] 아직 처리되지 않은 EffectType: " + type);
            }
        }
    }

    private void handleDirectAttackPlayer(cardEffect effect,
                                          PlayerBattleState targetOwner,
                                          BattleLog log) {

        int damage = effect.getValue();
        if (damage <= 0) {
            log.add("[효과] DIRECT_ATTACK_PLAYER 값이 0 이하입니다. 피해 없음.");
            return;
        }

        HeroState hero = targetOwner.getHero();
        int beforeHp = hero.getCurrentHealth();

        hero.applyDamage(damage);

        log.add("[효과] 영웅 \"" + hero.getName() + "\" 에게 " +
                damage + " 의 직통 피해를 줍니다.");
        log.add(" -> 체력: " + beforeHp + " -> " + hero.getCurrentHealth());

        if (hero.isDead()) {
            log.add("[효과 결과] 영웅 \"" + hero.getName() + "\" 이(가) 쓰러졌습니다!");
        }
    }

    private void handleDamageToMonster(cardEffect effect,
                                       PlayerBattleState targetOwner,
                                       TargetType targetType,
                                       int targetIndex,
                                       BattleLog log) {

        if (targetType != TargetType.UNIT) {
            log.add("[효과 실패] DAMAGE_TO_MONSTER 는 유닛(TargetType.UNIT)을 대상으로 해야 합니다.");
            return;
        }
        List<UnitState> board = targetOwner.getBoard();
        if (targetIndex < 0 || targetIndex >= board.size()) {
            log.add("[효과 실패] DAMAGE_TO_MONSTER 대상 인덱스가 잘못되었습니다.");
            return;
        }

        UnitState target = board.get(targetIndex);
        int damage = effect.getValue();
        int beforeHp = target.getCurrentHealth();
        target.applyDamage(damage);

        log.add("[효과] \"" + target.getName() + "\" 에게 " +
                damage + " 의 피해를 줍니다. (" + beforeHp + " -> " +
                target.getCurrentHealth() + ")");

        if (target.isDead()) {
            board.remove(targetIndex);
            log.add("[효과 결과] \"" + target.getName() + "\" 이(가) 파괴되었습니다.");
        }
    }
}
