package game.card;

/**
 * 카드의 특수 효과 종류를 정의하는 열거형입니다.
 *
 * 도발, 플레이어 직통 공격 뿐 아니라
 * 향후 확장을 고려하여 여러 가지 타입을 정의해 둡니다.
 *
 * 실제 게임 구현에서는 필요한 타입만 사용해도 됩니다.
 */
public enum EffectType {

    /**
     * 도발 효과.
     * 이 효과를 가진 몬스터가 전장에 있는 동안
     * 상대는 이 몬스터를 우선적으로 공격해야 한다는 규칙을
     * 전투 엔진에서 구현할 수 있습니다.
     */
    TAUNT,

    /**
     * 적 플레이어를 직접 공격하는 효과.
     * 보통 카드에 적혀 있는 value 값만큼 체력을 감소시킵니다.
     */
    DIRECT_ATTACK_PLAYER,

    /**
     * 적 몬스터(또는 유닛)에게 피해를 주는 효과.
     * value 만큼 피해를 줍니다.
     */
    DAMAGE_TO_MONSTER,

    /**
     * 아군 플레이어의 체력을 회복시키는 효과.
     */
    HEAL_PLAYER,

    /**
     * 아군 몬스터의 공격력을 증가시키는 버프 효과.
     */
    BUFF_ATTACK,

    /**
     * 아군 몬스터의 방어력을 증가시키는 버프 효과.
     */
    BUFF_DEFENSE,

    /**
     * 적 몬스터의 공격력을 감소시키는 디버프 효과.
     */
    DEBUFF_ATTACK,

    /**
     * 적 몬스터의 방어력을 감소시키는 디버프 효과.
     */
    DEBUFF_DEFENSE;

    /**
     * 이 효과 타입이 공격 계열인지 간단히 판별합니다.
     */
    public boolean isAttackRelated() {
        switch (this) {
            case DIRECT_ATTACK_PLAYER:
            case DAMAGE_TO_MONSTER:
            case BUFF_ATTACK:
            case DEBUFF_DEFENSE:
                return true;
            default:
                return false;
        }
    }

    /**
     * 이 효과 타입이 방어/보조 계열인지 판별합니다.
     */
    public boolean isSupportRelated() {
        switch (this) {
            case HEAL_PLAYER:
            case BUFF_DEFENSE:
            case DEBUFF_ATTACK:
                return true;
            default:
                return false;
        }
    }
}
