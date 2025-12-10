package game.card;

/**
 * 카드의 상위 타입을 정의하는 열거형입니다.
 *
 * 현재 과제에서는 몬스터 카드만 사용하지만,
 * 코드 구조를 확장 가능하게 만들기 위해 몇 가지 유형을 준비해 둡니다.
 *
 * - MONSTER : 일반 몬스터 카드
 * - ELITE   : 조금 더 강한 정예 몬스터 카드
 * - BOSS    : 보스 몬스터 카드
 *
 * 실제 게임 로직에서 타입에 따라
 * 추가 점수, 난이도, 연출 등을 다르게 줄 수 있습니다.
 */
public enum cardType {

    /**
     * 기본 몬스터 카드입니다.
     * 대부분의 카드가 이 타입을 사용합니다.
     */
    MONSTER,

    /**
     * 엘리트 몬스터 타입입니다.
     * 일반 몬스터보다 능력치가 좋거나
     * 특수 효과를 한 개 이상 가지고 있을 가능성이 높습니다.
     */
    ELITE,

    /**
     * 보스 몬스터 타입입니다.
     * 체력, 공격력, 특수 스킬이 매우 강력한 카드에 사용합니다.
     */
    BOSS;

    /**
     * 문자열 이름으로부터 cardType 을 안전하게 파싱합니다.
     * 대소문자를 구분하지 않고, 알 수 없는 값이 들어오면 null 을 반환합니다.
     */
    public static cardType fromString(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        for (cardType t : cardType.values()) {
            if (t.name().equals(normalized)) {
                return t;
            }
        }
        return null;
    }
}
