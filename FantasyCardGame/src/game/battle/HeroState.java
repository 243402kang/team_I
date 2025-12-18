package game.battle;

/**
 * 한 플레이어(또는 AI)의 "영웅" 전투 상태를 나타내는 클래스입니다.
 *
 * - 체력만 존재 (기본 20)
 */
public class HeroState {

    public static final int DEFAULT_MAX_HEALTH = 20;

    private final String name;

    private final int maxHealth;
    private int currentHealth;

    public HeroState(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("영웅 이름은 비어 있을 수 없습니다.");
        }
        this.name = name;
        this.maxHealth = DEFAULT_MAX_HEALTH;
        this.currentHealth = DEFAULT_MAX_HEALTH;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.max(0, Math.min(currentHealth, maxHealth));
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    /**
     * 영웅에게 피해를 적용합니다.
     */
    public void applyDamage(int damage) {
        if (damage <= 0) return;
        setCurrentHealth(currentHealth - damage);
    }

    @Override
    public String toString() {
        return "HeroState{" +
                "name='" + name + '\'' +
                ", currentHealth=" + currentHealth +
                "/" + maxHealth +
                '}';
    }
    //난이도 테스트
    public HeroState(String name, int maxHealth) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("영웅 이름은 비어 있을 수 없습니다.");
        }
        this.name = name;
        this.maxHealth = Math.max(1, maxHealth);
        this.currentHealth = this.maxHealth;
    }
}
