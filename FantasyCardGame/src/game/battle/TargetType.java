package game.battle;

/**
 *
 * - NONE  : 대상이 필요 없는 카드 (자기 자신 버프 등)
 * - UNIT  : 몬스터/유닛을 대상으로 하는 경우
 * - HERO  : 플레이어/적 영웅을 대상으로 하는 경우
 */
public enum TargetType {
    NONE,
    UNIT,
    HERO
}
