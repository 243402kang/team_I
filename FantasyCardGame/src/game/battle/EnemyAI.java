package game.battle;

import game.card.EffectType;
import game.card.card;

import java.util.List;

public class EnemyAI {

    private final GameState gameState;
    private final CardExecutor cardExecutor;
    private final CombatEngine combatEngine;

    public EnemyAI(GameState gameState) {
        this.gameState = gameState;
        this.cardExecutor = new CardExecutor(gameState);
        this.combatEngine = new CombatEngine(gameState);
    }

    /** ENEMY 메인 페이즈에서 할 행동: 카드 내기 -> 공격 */
    public BattleLog playEnemyMainPhase() {
        BattleLog log = new BattleLog();

        // 1) 카드 최대한 사용
        while (true) {
            PlayerBattleState enemy = gameState.getPlayerState(BattleSide.ENEMY);

            if (enemy.getHand().isEmpty()) break;
            if (!enemy.canSummonMoreUnits()) break;

            int bestIdx = pickBestPlayable(enemy);
            if (bestIdx < 0) break;

            card c = enemy.getHand().get(bestIdx);

            // 타겟형(DAMAGE_TO_MONSTER)이면 상대 유닛 중 체력 낮은 것 선택
            TargetType targetType = TargetType.NONE;
            int targetIndex = -1;

            boolean needUnitTarget = c.getEffects().stream()
                    .anyMatch(e -> e.getType() == EffectType.DAMAGE_TO_MONSTER);

            if (needUnitTarget) {
                List<UnitState> targets = gameState.getPlayerState(BattleSide.PLAYER).getBoard();
                if (!targets.isEmpty()) {
                    targetType = TargetType.UNIT;
                    targetIndex = indexOfLowestHp(targets);
                }
            }

            BattleLog one = cardExecutor.playCard(
                    BattleSide.ENEMY, c,
                    BattleSide.PLAYER,
                    targetType, targetIndex
            );

            //  CardExecutor는 손패 제거를 안 하니까, 여기서 제거
            enemy.removeCardFromHand(c);

            merge(log, one);
            if (gameState.isGameOver()) return log;
        }

        // 2) 공격 (도발 강제 + 유리교환 판단)
        merge(log, attackWithTrade());

        return log;
    }

    /** 도발 우선 + (도발 없으면) 유리교환 있으면 유닛 공격, 없으면 영웅 공격 */
    private BattleLog attackWithTrade() {
        BattleLog log = new BattleLog();

        PlayerBattleState enemy = gameState.getPlayerState(BattleSide.ENEMY);

        int i = 0;
        while (i < enemy.getBoard().size()) {
            if (gameState.isGameOver()) break;

            UnitState attacker = enemy.getBoard().get(i);
            if (!attacker.canAttack()) {
                i++;
                continue;
            }

            PlayerBattleState player = gameState.getPlayerState(BattleSide.PLAYER);

            // 1) 도발 강제
            int tauntIdx = firstTauntIndex(player.getBoard());
            if (tauntIdx >= 0) {
                merge(log, combatEngine.unitAttackUnit(BattleSide.ENEMY, i, tauntIdx));
            }
            // 2) 도발 없으면 유리 교환 판단
            else {
                Integer tradeIdx = findBestTradeTargetIndex(attacker, player.getBoard());
                if (tradeIdx != null) {
                    merge(log, combatEngine.unitAttackUnit(BattleSide.ENEMY, i, tradeIdx));
                } else {
                    merge(log, combatEngine.unitAttackHero(BattleSide.ENEMY, i));
                }
            }

            // 공격 후 죽어서 리스트 당겨질 수 있으니 안전 처리
            if (i < enemy.getBoard().size() && enemy.getBoard().get(i) == attacker) {
                i++;
            }
        }

        return log;
    }

    // 카드 선택(점수화)

    private int pickBestPlayable(PlayerBattleState enemy) {
        int bestIdx = -1;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < enemy.getHand().size(); i++) {
            card c = enemy.getHand().get(i);
            if (c.getCost() > enemy.getCurrentMana()) continue;

            int score = (c.getAttack() + c.getDefense()) - c.getCost() * 2;
            if (c.hasTauntEffect()) score += 3;

            if (score > bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    // 타겟 선택 보조

    private int firstTauntIndex(List<UnitState> board) {
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).isTaunt()) return i;
        }
        return -1;
    }

    private int indexOfLowestHp(List<UnitState> board) {
        int best = 0;
        int hp = Integer.MAX_VALUE;
        for (int i = 0; i < board.size(); i++) {
            int cur = board.get(i).getCurrentHealth();
            if (cur < hp) {
                hp = cur;
                best = i;
            }
        }
        return best;
    }

    /**
     * 공격자(attacker)가 특정 defender를 공격할 때의 "교환 점수"를 보고
     * score > 0 인 가장 좋은 타겟의 index를 반환합니다.
     *
     * score = (상대가 죽으면 상대 유닛 가치) - (내가 죽으면 내 유닛 가치)
     * - score > 0: 손해 없는(유리한) 교환
     */
    private Integer findBestTradeTargetIndex(UnitState attacker, List<UnitState> enemyBoard) {
        Integer bestIndex = null;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < enemyBoard.size(); i++) {
            UnitState defender = enemyBoard.get(i);

            int attackerHpAfter = attacker.getCurrentHealth() - defender.getAttack();
            int defenderHpAfter = defender.getCurrentHealth() - attacker.getAttack();

            int myLoss = (attackerHpAfter <= 0) ? unitValue(attacker) : 0;
            int enemyLoss = (defenderHpAfter <= 0) ? unitValue(defender) : 0;

            int score = enemyLoss - myLoss;

            if (score > 0 && score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    private int unitValue(UnitState u) {
        // 밸류 계산 공격력*2 + 현재체력
        return u.getAttack() * 2 + u.getCurrentHealth();
    }
    // 로그 합치기
 
    private void merge(BattleLog total, BattleLog child) {
        if (total == null || child == null) return;
        for (BattleLogEntry e : child.getEntries()) {
            total.add(e.getMessage());
        }
    }
}