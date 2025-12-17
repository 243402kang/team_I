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

    public BattleLog playEnemyMainPhase() {
        BattleLog log = new BattleLog();

        while (true) {
            PlayerBattleState enemy = gameState.getPlayerState(BattleSide.ENEMY);

            if (enemy.getHand().isEmpty()) break;
            if (!enemy.canSummonMoreUnits()) break;

            int bestIdx = pickBestPlayable(enemy);
            if (bestIdx < 0) break;

            card c = enemy.getHand().get(bestIdx);

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

            // AI도 "빈 자리"로 소환(그냥 끝에)
            BattleLog one = cardExecutor.playCard(
                    BattleSide.ENEMY, c,
                    BattleSide.PLAYER,
                    targetType, targetIndex,
                    Integer.MAX_VALUE
            );

            merge(log, one);
            if (gameState.isGameOver()) return log;
        }

        merge(log, attackWithTrade());
        return log;
    }

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

            int tauntIdx = firstTauntIndex(player.getBoard());
            if (tauntIdx >= 0) {
                merge(log, combatEngine.unitAttackUnit(BattleSide.ENEMY, i, tauntIdx));
            } else {
                Integer tradeIdx = findBestTradeTargetIndex(attacker, player.getBoard());
                if (tradeIdx != null) {
                    merge(log, combatEngine.unitAttackUnit(BattleSide.ENEMY, i, tradeIdx));
                } else {
                    merge(log, combatEngine.unitAttackHero(BattleSide.ENEMY, i));
                }
            }

            if (i < enemy.getBoard().size() && enemy.getBoard().get(i) == attacker) {
                i++;
            }
        }
        return log;
    }

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
        return u.getAttack() * 2 + u.getCurrentHealth();
    }

    private void merge(BattleLog total, BattleLog child) {
        if (total == null || child == null) return;
        for (BattleLogEntry e : child.getEntries()) {
            total.add(e.getMessage());
        }
    }
}
