package game.battle;

import game.card.EffectType;
import game.card.card;
import game.card.cardEffect;

import java.util.List;

public class CardExecutor {

    private final GameState gameState;

    public CardExecutor(GameState gameState) {
        if (gameState == null) throw new IllegalArgumentException("GameState 는 null 일 수 없습니다.");
        this.gameState = gameState;
    }

    public GameState getGameState() { return gameState; }

    /**UI 연결용: summonPosition(0~4)을 받아서 그 위치로 소환 */
    public BattleLog playCard(BattleSide side,
                              card card,
                              BattleSide targetSide,
                              TargetType targetType,
                              int targetIndex,
                              int summonPosition) {

        BattleLog log = new BattleLog();

        if (card == null) {
            log.add("[카드 사용 실패] 카드가 null 입니다.");
            return log;
        }

        PlayerBattleState casterState = gameState.getPlayerState(side);

        if (casterState.getCurrentMana() < card.getCost()) {
            log.add("[카드 사용 실패] 마나가 부족하여 \"" + card.getName() +
                    "\" 카드를 사용할 수 없습니다. (필요 코스트=" + card.getCost() + ")");
            return log;
        }

        if (!casterState.canSummonMoreUnits()) {
            log.add("[카드 사용 실패] 필드에는 최대 " +
                    PlayerBattleState.MAX_BOARD_SIZE + " 마리까지만 소환할 수 있습니다.");
            return log;
        }

        casterState.setCurrentMana(casterState.getCurrentMana() - card.getCost());

        //손패에 있으면 자동 제거 (UI/AI 둘 다 안전)
        if (casterState.getHand().contains(card)) {
            casterState.removeCardFromHand(card);
        }

        log.add("[카드 사용] " + side + " 이(가) \"" + card.getName() +
                "\" (코스트 " + card.getCost() + ") 카드를 사용했습니다.");

        UnitState summoned = new UnitState(card);
        summoned.applyCardEffectsOnSummon(card.getEffects());

        boolean summonedOk = casterState.summonUnitAt(summoned, summonPosition);
        if (!summonedOk) {
            log.add("[소환 실패] 필드가 가득 차 있어 \"" + summoned.getName() +
                    "\" 을(를) 더 이상 소환할 수 없습니다.");
            return log;
        }

        log.add("[소환] \"" + summoned.getName() + "\" 이(가) 전장에 소환되었습니다. " +
                "(공격력=" + summoned.getAttack() +
                ", 체력=" + summoned.getCurrentHealth() + ", 위치=" + summonPosition + ")");

        List<cardEffect> effects = card.getEffects();
        if (!effects.isEmpty()) {
            if (targetSide == null) targetSide = side.getOpponent();
            handleEffects(effects, targetSide, targetType, targetIndex, summoned, log);
        }

        return log;
    }

    /** 기존 호출 호환용(끝에 소환) */
    public BattleLog playCard(BattleSide side,
                              card card,
                              BattleSide targetSide,
                              TargetType targetType,
                              int targetIndex) {
        return playCard(side, card, targetSide, targetType, targetIndex, Integer.MAX_VALUE);
    }

    private void handleEffects(List<cardEffect> effects,
                               BattleSide targetSide,
                               TargetType targetType,
                               int targetIndex,
                               UnitState self,
                               BattleLog log) {

        PlayerBattleState targetOwner = gameState.getPlayerState(targetSide);

        for (cardEffect effect : effects) {
            EffectType type = effect.getType();

            switch (type) {
                case TAUNT:
                    self.setTaunt(true);
                    log.add("[효과] " + self.getName() + " 은(는) 도발(TAUNT)을 보유합니다.");
                    break;

                case DIRECT_ATTACK_PLAYER:
                    handleDirectAttackPlayer(effect, targetOwner, log);
                    break;

                case DAMAGE_TO_MONSTER:
                    handleDamageToMonster(effect, targetOwner, targetType, targetIndex, log);
                    break;

                // 요청대로 전부 무시
                case HEAL_PLAYER:
                case BUFF_ATTACK:
                case BUFF_DEFENSE:
                case DEBUFF_ATTACK:
                case DEBUFF_DEFENSE:
                    log.add("[효과 무시] " + type + " 효과는 현재 게임 규칙에서 사용하지 않습니다.");
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

        log.add("[효과] 영웅 \"" + hero.getName() + "\" 에게 " + damage + " 의 직통 피해");
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

        log.add("[효과] \"" + target.getName() + "\" 에게 " + damage +
                " 피해 (" + beforeHp + " -> " + target.getCurrentHealth() + ")");

        if (target.isDead()) {
            board.remove(targetIndex);
            log.add("[효과 결과] \"" + target.getName() + "\" 이(가) 파괴되었습니다.");
        }
    }
}
