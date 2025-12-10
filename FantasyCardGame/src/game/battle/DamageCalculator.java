package game.battle;

/**
 * 피해량 계산
 *
 * 기본 구현(DefaultDamageCalculator)은 단순히 공격력 그대로 적용하지만,
 * 나중에 난이도/스테이지에 따라 다른 계산 방식을 쓰고 싶다면 이 인터페이스를 구현한 클래스를 새로 만들어 CombatEngine 에 주입할 수 있습니다.
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
