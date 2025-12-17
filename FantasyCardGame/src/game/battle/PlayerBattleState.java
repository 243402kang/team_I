package game.battle;

import game.card.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerBattleState {

    public static final int MAX_BOARD_SIZE = 5;

    private final HeroState hero;

    private final List<UnitState> board = new ArrayList<>();
    private final List<card> deck = new ArrayList<>();
    private final List<card> hand = new ArrayList<>();

    private int maxMana;
    private int currentMana;

    private int fatigueDamage;
    private boolean heroPowerUsedThisTurn;

    public PlayerBattleState(HeroState hero) {
        if (hero == null) throw new IllegalArgumentException("HeroState 는 null 일 수 없습니다.");
        this.hero = hero;
        this.maxMana = 0;
        this.currentMana = 0;
        this.fatigueDamage = 1;
        this.heroPowerUsedThisTurn = false;
    }

    public HeroState getHero() { return hero; }
    public List<UnitState> getBoard() { return board; }
    public List<card> getDeck() { return deck; }
    public List<card> getHand() { return hand; }

    public int getMaxMana() { return maxMana; }

    public void setMaxMana(int maxMana) {
        this.maxMana = Math.max(0, Math.min(10, maxMana));
        if (currentMana > this.maxMana) currentMana = this.maxMana;
    }

    public int getCurrentMana() { return currentMana; }

    public void setCurrentMana(int currentMana) {
        this.currentMana = Math.max(0, Math.min(maxMana, currentMana));
    }

    public boolean isHeroPowerUsedThisTurn() { return heroPowerUsedThisTurn; }

    public void setHeroPowerUsedThisTurn(boolean heroPowerUsedThisTurn) {
        this.heroPowerUsedThisTurn = heroPowerUsedThisTurn;
    }

    public int getFatigueDamage() { return fatigueDamage; }

    public void setFatigueDamage(int fatigueDamage) {
        this.fatigueDamage = Math.max(1, fatigueDamage);
    }

    public void addCardToDeck(card c) { if (c != null) deck.add(c); }

    public void shuffleDeck() { Collections.shuffle(deck); }

    public void addCardToHand(card c) { if (c != null) hand.add(c); }

    public void removeCardFromHand(card c) { hand.remove(c); }

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

    public boolean canSummonMoreUnits() {
        return board.size() < MAX_BOARD_SIZE;
    }

    /** 기본: 끝에 추가 */
    public boolean summonUnit(UnitState unit) {
        return summonUnitAt(unit, board.size());
    }

    /**UI 슬롯 인덱스(0~4)에 맞춰 소환 */
    public boolean summonUnitAt(UnitState unit, int position) {
        if (unit == null) return false;
        if (!canSummonMoreUnits()) return false;

        int p = position;
        if (p < 0) p = 0;
        if (p > board.size()) p = board.size();

        board.add(p, unit);
        return true;
    }

    public void removeUnit(UnitState unit) {
        board.remove(unit);
    }

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
