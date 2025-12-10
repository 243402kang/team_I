package game.battle;

/**
 * 카드 사용 또는 전투 시, 대상의 종류를 표현하는 열거형입니다.
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
