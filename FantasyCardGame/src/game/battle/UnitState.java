package game.battle;

import game.card.EffectType;
import game.card.card;
import game.card.cardEffect;

import java.util.List;
import java.util.UUID;

/**
 * 전장(필드)에 소환된 "몬스터(유닛)" 1기를 표현하는 클래스입니다.
 *
 * 관리하는 정보:
 * - 원본 카드 정보(card)
 * - 현재 공격력 / 체력
 * - 도발(TAUNT) 여부
 * - 이번 턴에 공격한 횟수
 * - 소환된 턴인지 여부
 */
public class UnitState {

    /** 전장에서 유닛을 구분하기 위한 유니크 ID */
    private final String instanceId;

    /** 원본 카드 데이터 */
    private final card baseCard;

    /** 표시용 이름(편의상 card.getName() 복사) */
    private final String name;

    /** 현재 공격력 */
    private int attack;

    /** 현재 최대 체력 */
    private int maxHealth;

    /** 현재 체력 */
    private int currentHealth;

    /** 도발 보유 여부 */
    private boolean taunt;

    /** 돌진(소환된 턴에도 공격 가능) 여부 – 필요 시 사용할 수 있음 */
    private boolean charge;

    /** 이번 턴에 몇 번 공격했는지 */
    private int attacksThisTurn;

    /** 이 유닛이 이번 턴에 소환되었는지 여부 */
    private boolean summonedThisTurn;

    public UnitState(card baseCard) {
        if (baseCard == null) {
            throw new IllegalArgumentException("baseCard 는 null 일 수 없습니다.");
        }
        this.instanceId = UUID.randomUUID().toString();
        this.baseCard = baseCard;
        this.name = baseCard.getName();
        this.attack = baseCard.getAttack();
        this.maxHealth = baseCard.getDefense(); // defense 를 체력으로 사용
        this.currentHealth = this.maxHealth;

        // 카드에 도발(TAUNT) 효과가 있으면 true
        this.taunt = baseCard.hasTauntEffect();

        this.charge = false;
        this.attacksThisTurn = 0;
        this.summonedThisTurn = true; // 생성 시점 = 소환 시점이라고 가정
    }

    // ==== 기본 getter/setter ====

    public String getInstanceId() {
        return instanceId;
    }

    public card getBaseCard() {
        return baseCard;
    }

    public String getName() {
        return name;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = Math.max(0, attack);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        if (currentHealth > this.maxHealth) {
            currentHealth = this.maxHealth;
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.max(0, Math.min(currentHealth, maxHealth));
    }

    public boolean isTaunt() {
        return taunt;
    }

    public void setTaunt(boolean taunt) {
        this.taunt = taunt;
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public int getAttacksThisTurn() {
        return attacksThisTurn;
    }

    public void resetAttacksThisTurn() {
        this.attacksThisTurn = 0;
    }

    public void incrementAttacksThisTurn() {
        this.attacksThisTurn++;
    }

    public boolean isSummonedThisTurn() {
        return summonedThisTurn;
    }

    public void setSummonedThisTurn(boolean summonedThisTurn) {
        this.summonedThisTurn = summonedThisTurn;
    }

    // ==== 전투 관련 메서드 ====

    public boolean isDead() {
        return currentHealth <= 0;
    }

    /**
     * 유닛에게 피해를 적용합니다.
     */
    public void applyDamage(int damage) {
        if (damage <= 0) return;
        setCurrentHealth(currentHealth - damage);
    }

    /**
     * 현재 유닛이 공격 가능한 상태인지 여부를 체크합니다.
     *
     * - 이미 죽은 경우 X
     * - 이번 턴에 이미 한 번 공격했다면 X
     * - 소환된 턴이고, 돌진(CHARGE)이 없으면 X
     * - 공격력이 0 이하면 X
     */
    public boolean canAttack() {
        if (isDead()) {
            return false;
        }

        if (attacksThisTurn >= 1) {
            return false;
        }

        if (summonedThisTurn && !charge) {
            return false;
        }

        if (attack <= 0) {
            return false;
        }

        return true;
    }

    /**
     * 소환 시 카드 효과들 중 "도발"만 UnitState 에 반영하는 도우미 메서드입니다.
     */
    public void applyCardEffectsOnSummon(List<cardEffect> effects) {
        if (effects == null || effects.isEmpty()) return;

        for (cardEffect effect : effects) {
            if (effect.getType() == EffectType.TAUNT) {
                setTaunt(true);
            }
        }
    }

    @Override
    public String toString() {
        return "UnitState{" +
                "id='" + instanceId + '\'' +
                ", name='" + name + '\'' +
                ", attack=" + attack +
                ", currentHealth=" + currentHealth +
                "/" + maxHealth +
                ", taunt=" + taunt +
                ", charge=" + charge +
                ", attacksThisTurn=" + attacksThisTurn +
                ", summonedThisTurn=" + summonedThisTurn +
                '}';
    }
}
