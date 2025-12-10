package game.battle;

/**
 * 가장 단순한 피해량 계산 방식:
 * - 유닛의 공격력 그대로 피해로 사용
 */
public class DefaultDamageCalculator implements DamageCalculator {

    @Override
    public int calculateUnitToUnitDamage(UnitState attacker, UnitState defender) {
        if (attacker == null || defender == null) {
            return 0;
        }
        return Math.max(0, attacker.getAttack());
    }

    @Override
    public int calculateUnitToHeroDamage(UnitState attacker, HeroState hero) {
        if (attacker == null || hero == null) {
            return 0;
        }
        return Math.max(0, attacker.getAttack());
    }
}
