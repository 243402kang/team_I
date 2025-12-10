package game.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 카드 한 장을 표현하는 클래스입니다.
 * 
 * 이 클래스는 몬스터 카드만을 다룹니다.
 * - id:      내부 식별용 문자열 (예: "MON_001")
 * - name:    화면에 표시되는 카드 이름
 * - type:    카드의 타입 (기본적으로 MONSTER 이지만 확장 가능)
 * - cost:    카드를 사용하는 데 필요한 코스트 (0 ~ MAX_COST)
 * - attack:  카드가 가진 기본 공격력 값
 * - defense: 카드가 가진 기본 방어력 값
 * - effects: 특수 효과 목록 (도발, 플레이어 직통 공격 등)
 * - tags:    카드 분류용 태그들 (예: "BASIC", "FIRE", "BOSS")
 * - description: 카드 설명 텍스트
 *
 * 이 클래스는 불변(immutable)을 지향합니다.
 * 한 번 생성된 card 인스턴스의 필드는 변경되지 않습니다.
 */
public class card {

    /**
     * 카드가 가질 수 있는 최대 코스트 값 입니다.
     * 과제 요구사항에 맞추어 10으로 고정합니다.
     */
    public static final int MAX_COST = 10;

    private final String id;
    private final String name;
    private final cardType type;

    private final int cost;
    private final int attack;
    private final int defense;

    private final List<cardEffect> effects;
    private final List<String> tags;

    private final String description;

    /**
     * 기본 생성자는 private 으로 막습니다.
     * card 는 정적 팩토리 메서드를 통해서만 생성됩니다.
     */
    private card(
            String id,
            String name,
            cardType type,
            int cost,
            int attack,
            int defense,
            List<cardEffect> effects,
            List<String> tags,
            String description
    ) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id 는 비어 있을 수 없습니다.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name 은 비어 있을 수 없습니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type 은 null 일 수 없습니다.");
        }
        if (cost < 0 || cost > MAX_COST) {
            throw new IllegalArgumentException("cost 는 0 이상 " + MAX_COST + " 이하이어야 합니다. 입력값=" + cost);
        }
        if (attack < 0) {
            throw new IllegalArgumentException("attack 은 음수일 수 없습니다. 입력값=" + attack);
        }
        if (defense < 0) {
            throw new IllegalArgumentException("defense 는 음수일 수 없습니다. 입력값=" + defense);
        }

        this.id = id;
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.attack = attack;
        this.defense = defense;

        // 방어적 복사(defensive copy)로 불변성 유지
        if (effects == null) {
            this.effects = Collections.emptyList();
        } else {
            this.effects = Collections.unmodifiableList(new ArrayList<>(effects));
        }

        if (tags == null) {
            this.tags = Collections.emptyList();
        } else {
            this.tags = Collections.unmodifiableList(new ArrayList<>(tags));
        }

        this.description = description == null ? "" : description;
    }

    // ---- 정적 팩토리 메서드들 -------------------------------------------------

    /**
     * 특수 효과가 없는 기본 몬스터 카드를 생성합니다.
     */
    public static card createNormal(
            String id,
            String name,
            int cost,
            int attack,
            int defense,
            String description
    ) {
        return new card(id, name, cardType.MONSTER, cost, attack, defense,
                Collections.emptyList(), Collections.emptyList(), description);
    }

    /**
     * 특수 효과가 있는 몬스터 카드를 생성합니다.
     */
    public static card createWithEffects(
            String id,
            String name,
            int cost,
            int attack,
            int defense,
            List<cardEffect> effects,
            String description
    ) {
        return new card(id, name, cardType.MONSTER, cost, attack, defense,
                effects, Collections.emptyList(), description);
    }

    /**
     * 태그가 포함된 몬스터 카드를 생성합니다.
     */
    public static card createWithTags(
            String id,
            String name,
            int cost,
            int attack,
            int defense,
            List<String> tags,
            String description
    ) {
        return new card(id, name, cardType.MONSTER, cost, attack, defense,
                Collections.emptyList(), tags, description);
    }

    /**
     * 효과와 태그가 모두 있는 몬스터 카드를 생성합니다.
     */
    public static card createFull(
            String id,
            String name,
            int cost,
            int attack,
            int defense,
            List<cardEffect> effects,
            List<String> tags,
            String description
    ) {
        return new card(id, name, cardType.MONSTER, cost, attack, defense,
                effects, tags, description);
    }

    // ---- getter 메서드들 ----------------------------------------------------

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public cardType getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    /**
     * 이 카드가 가진 특수 효과 목록을 반환합니다.
     * 반환되는 리스트는 수정할 수 없습니다.
     */
    public List<cardEffect> getEffects() {
        return effects;
    }

    /**
     * 태그 목록을 반환합니다.
     * 반환되는 리스트는 수정할 수 없습니다.
     */
    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    // ---- 유틸리티 메서드들 --------------------------------------------------

    /**
     * 이 카드가 특정 태그를 포함하고 있는지 확인합니다.
     */
    public boolean hasTag(String tag) {
        if (tag == null) {
            return false;
        }
        return tags.contains(tag);
    }

    /**
     * 이 카드가 특수 효과를 하나라도 가지고 있는지 확인합니다.
     */
    public boolean hasAnyEffect() {
        return !effects.isEmpty();
    }

    /**
     * 카드가 도발(TAUNT) 효과를 가지는지 여부를 확인합니다.
     * (편의 메서드)
     */
    public boolean hasTauntEffect() {
        for (cardEffect effect : effects) {
            if (effect.getType() == EffectType.TAUNT) {
                return true;
            }
        }
        return false;
    }

    /**
     * 카드가 플레이어 직통 공격 효과를 가지는지 여부를 확인합니다.
     */
    public boolean hasDirectAttackToPlayer() {
        for (cardEffect effect : effects) {
            if (effect.getType() == EffectType.DIRECT_ATTACK_PLAYER) {
                return true;
            }
        }
        return false;
    }

    /**
     * 특정 EffectType 을 가진 효과들을 모아서 반환합니다.
     */
    public List<cardEffect> findEffectsByType(EffectType type) {
        List<cardEffect> result = new ArrayList<>();
        for (cardEffect e : effects) {
            if (e.getType() == type) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 카드의 기본 정보를 보기 좋게 문자열로 반환합니다.
     */
    public String toSimpleString() {
        return String.format("[%s] 이름=%s, 코스트=%d, 공격=%d, 방어=%d",
                id, name, cost, attack, defense);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("card{")
          .append("id='").append(id).append('\'')
          .append(", name='").append(name).append('\'')
          .append(", type=").append(type)
          .append(", cost=").append(cost)
          .append(", attack=").append(attack)
          .append(", defense=").append(defense)
          .append(", tags=").append(tags)
          .append(", effectsCount=").append(effects.size())
          .append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof card)) return false;
        card other = (card) o;
        // id 는 유일하다고 가정하고 id 기준으로 비교
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ---- 빌더(Builder) 구현 -------------------------------------------------

    /**
     * 카드 생성시 선택적인 필드가 많기 때문에
     * 가독성을 위해 빌더 패턴을 제공합니다.
     *
     * 예시:
     * card c = card.builder()
     *      .id("MON_999")
     *      .name("예시 카드")
     *      .cost(3)
     *      .attack(2)
     *      .defense(4)
     *      .addTag("EXAMPLE")
     *      .build();
     */
    public static class Builder {
        private String id;
        private String name;
        private cardType type = cardType.MONSTER;
        private int cost = 0;
        private int attack = 0;
        private int defense = 0;
        private final List<cardEffect> effects = new ArrayList<>();
        private final List<String> tags = new ArrayList<>();
        private String description = "";

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(cardType type) {
            this.type = type;
            return this;
        }

        public Builder cost(int cost) {
            this.cost = cost;
            return this;
        }

        public Builder attack(int attack) {
            this.attack = attack;
            return this;
        }

        public Builder defense(int defense) {
            this.defense = defense;
            return this;
        }

        public Builder addEffect(cardEffect effect) {
            if (effect != null) {
                this.effects.add(effect);
            }
            return this;
        }

        public Builder addTag(String tag) {
            if (tag != null && !tag.isEmpty()) {
                this.tags.add(tag);
            }
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public card build() {
            return new card(id, name, type, cost, attack, defense, effects, tags, description);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}

