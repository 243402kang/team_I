package game.battle;

import game.card.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 전투 화면에서 "한 쪽 플레이어"의 상태를 관리하는 클래스입니다.
 *
 * - 영웅 상태 (HeroState)
 * - 필드 위 유닛 목록 (board)
 * - 손패/덱 (hand/deck)
 * - 마나 (maxMana, currentMana)
 * - 피로(Fatigue) 데미지
 *
 */
public class PlayerBattleState {

    /** 필드에 소환할 수 있는 최대 유닛 수 */
    public static final int MAX_BOARD_SIZE = 5;

    private final HeroState hero;

    /** 전장(필드)에 깔려 있는 유닛 목록 */
    private final List<UnitState> board = new ArrayList<>();

    /** 덱(아직 손에 들어오지 않은 카드들) */
    private final List<card> deck = new ArrayList<>();

    /** 손패 */
    private final List<card> hand = new ArrayList<>();

    /** 최대 마나 (0~10) */
    private int maxMana;

    /** 현재 마나 (0~maxMana) */
    private int currentMana;

    /** 현재 피로 데미지 (드로우할 카드가 없을 때 누적) */
    private int fatigueDamage;

    /** 이번 턴에 히어로 파워를 사용했는지 여부 (있다면) */
    private boolean heroPowerUsedThisTurn;

    public PlayerBattleState(HeroState hero) {
        if (hero == null) {
            throw new IllegalArgumentException("HeroState 는 null 일 수 없습니다.");
        }
        this.hero = hero;
        this.maxMana = 0;
        this.currentMana = 0;
        this.fatigueDamage = 1;
        this.heroPowerUsedThisTurn = false;
    }

    public HeroState getHero() {
        return hero;
    }

    public List<UnitState> getBoard() {
        return board;
    }

    public List<card> getDeck() {
        return deck;
    }

    public List<card> getHand() {
        return hand;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(0, Math.min(10, maxMana));
        if (currentMana > this.maxMana) {
            currentMana = this.maxMana;
        }
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(0, Math.min(maxMana, currentMana));
    }

    public boolean isHeroPowerUsedThisTurn() {
        return heroPowerUsedThisTurn;
    }

    public void setHeroPowerUsedThisTurn(boolean heroPowerUsedThisTurn) {
        this.heroPowerUsedThisTurn = heroPowerUsedThisTurn;
    }

    public int getFatigueDamage() {
        return fatigueDamage;
    }

    public void setFatigueDamage(int fatigueDamage) {
        this.fatigueDamage = Math.max(1, fatigueDamage);
    }

    /**
     * 덱에 카드를 추가합니다.
     */
    public void addCardToDeck(card c) {
        if (c != null) {
            deck.add(c);
        }
    }

    /**
     * 덱을 섞습니다.
     */
    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    /**
     * 손패에 카드를 추가합니다.
     */
    public void addCardToHand(card c) {
        if (c != null) {
            hand.add(c);
        }
    }

    /**
     * 손패에서 카드를 제거합니다.
     */
    public void removeCardFromHand(card c) {
        hand.remove(c);
    }

    /**
     * 피로(fatigue) 규칙을 포함한 드로우 처리입니다.
     */
    public List<String> drawCardWithFatigue() {
        List<String> logs = new ArrayList<>();
        if (deck.isEmpty()) {
            int beforeHp = hero.getCurrentHealth();
            hero.applyDamage(fatigueDamage);
            logs.add("[피로] 덱에 카드가 없어 " + fatigueDamage +
                    "의 피로 피해를 받습니다. (" + beforeHp + " -> " + hero.getCurrentHealth() + ")");
            fatigueDamage++;
            return logs;
        }

        card drawn = deck.remove(deck.size() - 1);
        hand.add(drawn);
        logs.add("[드로우] \"" + drawn.getName() + "\" 카드를 뽑았습니다.");
        return logs;
    }

    /**
     * 필드에 더 소환할 수 있는지 여부를 반환합니다.
     */
    public boolean canSummonMoreUnits() {
        return board.size() < MAX_BOARD_SIZE;
    }

    /**
     * 보드(전장)에 유닛을 소환합니다.
     * @return 소환 성공 여부
     */
    public boolean summonUnit(UnitState unit) {
        if (unit == null) {
            return false;
        }
        if (!canSummonMoreUnits()) {
            return false;
        }
        board.add(unit);
        return true;
    }

    /**
     * 유닛을 보드에서 제거합니다.
     */
    public void removeUnit(UnitState unit) {
        board.remove(unit);
    }

    /**
     * 턴 시작 시 호출되는 초기화 로직입니다.
     * - 하수인 공격 횟수 초기화
     * - 소환턴 플래그 조정
     * - 히어로 파워 사용 여부 초기화
     */
    public List<String> onTurnStart() {
        List<String> logs = new ArrayList<>();
        heroPowerUsedThisTurn = false;

        for (UnitState unit : board) {
            unit.resetAttacksThisTurn();
            if (unit.isSummonedThisTurn()) {
                unit.setSummonedThisTurn(false);
                logs.add("[턴 시작] " + unit.getName() + " 이(가) 이제 공격할 수 있습니다.");
            }
        }

        return logs;
    }

    @Override
    public String toString() {
        return "PlayerBattleState{" +
                "hero=" + hero +
                ", boardSize=" + board.size() +
                ", handSize=" + hand.size() +
                ", deckSize=" + deck.size() +
                ", maxMana=" + maxMana +
                ", currentMana=" + currentMana +
                ", fatigueDamage=" + fatigueDamage +
                ", heroPowerUsedThisTurn=" + heroPowerUsedThisTurn +
                '}';
    }
}
