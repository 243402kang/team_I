package game.card;

import java.util.Objects;

/**
 * 카드의 특수 효과 한 개를 표현하는 클래스입니다.
 *
 * 예를 들어,
 * - 도발(TAUNT)
 * - 플레이어 직통 공격(DIRECT_ATTACK_PLAYER)
 * - 몬스터에게 피해(DAMAGE_TO_MONSTER)
 * 등의 정보를 이 객체 하나에 담을 수 있습니다.
 *
 * value 값의 의미는 EffectType 에 따라 달라질 수 있습니다.
 * 예:
 *   DIRECT_ATTACK_PLAYER 인 경우 -> 플레이어에게 줄 피해량
 *   DAMAGE_TO_MONSTER     인 경우 -> 몬스터에게 줄 피해량
 */
public class cardEffect {

    private final EffectType type;
    private final int value;
    private final StatusType statusType; // 필요 시 상태를 함께 표현
    private final int duration;          // 상태가 지속되는 턴 수
    private final String description;    // 효과 설명

    /**
     * 가장 단순한 생성자.
     * 상태, 지속 턴, 설명 없이 type 과 value 만 설정합니다.
     */
    public cardEffect(EffectType type, int value) {
        this(type, value, StatusType.NONE, 0, "");
    }

    /**
     * 모든 필드를 설정하는 생성자.
     */
    public cardEffect(
            EffectType type,
            int value,
            StatusType statusType,
            int duration,
            String description
    ) {
        if (type == null) {
            throw new IllegalArgumentException("EffectType 은 null 일 수 없습니다.");
        }
        if (statusType == null) {
            statusType = StatusType.NONE;
        }
        if (duration < 0) {
            throw new IllegalArgumentException("duration 은 음수일 수 없습니다. 입력값=" + duration);
        }

        this.type = type;
        this.value = value;
        this.statusType = statusType;
        this.duration = duration;
        this.description = description == null ? "" : description;
    }

    public EffectType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public int getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 이 효과가 "공격 관련"인지 단순 판정하는 편의 메서드입니다.
     */
    public boolean isAttackEffect() {
        return type.isAttackRelated();
    }

    /**
     * 이 효과가 "지원(버프/힐) 관련"인지 단순 판정하는 편의 메서드입니다.
     */
    public boolean isSupportEffect() {
        return type.isSupportRelated();
    }

    @Override
    public String toString() {
        return "cardEffect{" +
                "type=" + type +
                ", value=" + value +
                ", statusType=" + statusType +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof cardEffect)) return false;
        cardEffect that = (cardEffect) o;
        return value == that.value &&
                duration == that.duration &&
                type == that.type &&
                statusType == that.statusType &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, statusType, duration, description);
    }

    // ---- 빌더 패턴 ----------------------------------------------------------

    /**
     * cardEffect 를 편하게 생성하기 위한 빌더입니다.
     *
     * 예시:
     * cardEffect e = cardEffect.builder()
     *      .type(EffectType.DIRECT_ATTACK_PLAYER)
     *      .value(5)
     *      .description("플레이어에게 5 피해")
     *      .build();
     */
    public static class Builder {
        private EffectType type = EffectType.DIRECT_ATTACK_PLAYER;
        private int value = 0;
        private StatusType statusType = StatusType.NONE;
        private int duration = 0;
        private String description = "";

        public Builder type(EffectType type) {
            this.type = type;
            return this;
        }

        public Builder value(int value) {
            this.value = value;
            return this;
        }

        public Builder status(StatusType statusType) {
            this.statusType = statusType;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public cardEffect build() {
            return new cardEffect(type, value, statusType, duration, description);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
