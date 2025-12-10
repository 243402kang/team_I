package game.card;

/**
 * 몬스터 또는 플레이어에게 적용될 수 있는
 * 상태(Status)를 정의하는 열거형입니다.
 *
 * 실제 전투 엔진에서는 이 정보를 참조하여
 * 턴마다 지속 데미지를 주거나, 공격 불가 상태로 만들거나 등
 * 다양한 효과를 구현할 수 있습니다.
 */
public enum StatusType {

    /**
     * 아무런 상태가 없는 기본 상태입니다.
     */
    NONE,

    /**
     * 도발 상태.
     * 도발을 보유한 몬스터에게 적용할 수 있으며,
     * 전투 엔진에서 "우선 공격 대상"으로 처리할 수 있습니다.
     */
    TAUNT,

    /**
     * 기절 상태.
     * 기절 상태인 유닛은 일정 턴 동안 행동을 할 수 없도록 만들 수 있습니다.
     */
    STUNNED,

    /**
     * 중독 상태.
     * 턴이 시작될 때마다 고정 피해를 입는 디버프로 사용할 수 있습니다.
     */
    POISONED,

    /**
     * 화상 상태.
     * 중독과 비슷하지만, 불 속성으로 연출할 때 사용할 수 있습니다.
     */
    BURNING;

    /**
     * 이 상태가 디버프(나쁜 상태)인지 여부를 반환합니다.
     */
    public boolean isDebuff() {
        switch (this) {
            case STUNNED:
            case POISONED:
            case BURNING:
                return true;
            default:
                return false;
        }
    }
}
