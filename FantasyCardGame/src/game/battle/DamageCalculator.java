package game.battle;

/**
 * 피해량 계산
 *
 * 기본 구현(DefaultDamageCalculator)은 단순히 공격력 그대로 적용
 */
public interface DamageCalculator {

    /**
     * 유닛 vs 유닛 공격 시 피해량 계산.
     */
    int calculateUnitToUnitDamage(UnitState attacker, UnitState defender);

    /**
     * 유닛 vs 영웅 공격 시 피해량 계산.
     */
    int calculateUnitToHeroDamage(UnitState attacker, HeroState hero);
}
