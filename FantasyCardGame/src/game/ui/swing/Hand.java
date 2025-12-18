package game.ui.swing;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import game.card.CardRepository;
import game.card.card;

public class Hand {

    private List<CardUI> myHandCards = new ArrayList<>();

    private final int SCREEN_WIDTH = 1280;
    private final int HAND_Y = 600;

    private final int DECK_X = 50;
    private final int DECK_Y = 600;
    private final int DECK_WIDTH = 100;
    private final int DECK_HEIGHT = 140;

    private boolean isExpanded = false;

    public Hand() {
        // 초기에는 비어있어도 됨 (BaseStage에서 battle hand로 채움)
        arrangeCards(false);
    }

    /**battle의 손패를 그대로 UI hand로 동기화 */
    public void setFromBattleHand(List<card> battleHand) {
        myHandCards.clear();
        if (battleHand == null) {
            arrangeCards(false);
            return;
        }

        CardImageManager imgManager = CardImageManager.getInstance();

        for (card data : battleHand) {
            int imageIndex = findImageIndex(data);
            CardUI newCard = new CardUI(data, imgManager.getCardImage(imageIndex), 0, 0);
            myHandCards.add(newCard);
        }

        arrangeCards(isExpanded);
    }

    public void arrangeCards(boolean expand) {
        this.isExpanded = expand;

        if (!expand) {
            for (CardUI c : myHandCards) {
                c.setPosition(DECK_X, DECK_Y);
            }
        } else {
            int count = myHandCards.size();
            if (count == 0) return;

            int cardWidth = 100;
            int defaultGap = 110;
            int maxAvailableWidth = SCREEN_WIDTH - 200;

            int gap = defaultGap;
            if (count * defaultGap > maxAvailableWidth) {
                gap = Math.max(40, maxAvailableWidth / count);
            }

            int totalWidth = (count - 1) * gap + cardWidth;
            int startX = (SCREEN_WIDTH - totalWidth) / 2;

            for (int i = 0; i < count; i++) {
                int x = startX + (i * gap);
                myHandCards.get(i).setPosition(x, HAND_Y);
            }
        }
    }

    private int findImageIndex(card c) {
        List<card> all = CardRepository.getAllCards();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(c.getId())) return i;
        }
        return 0;
    }

    public void draw(Graphics g) {
        for (CardUI card : myHandCards) {
            card.draw(g);
        }
    }

    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (mx >= DECK_X && mx <= DECK_X + DECK_WIDTH &&
                my >= DECK_Y && my <= DECK_Y + DECK_HEIGHT) {

            isExpanded = !isExpanded;
            arrangeCards(isExpanded);
            MainFrame.bgplay("/res/card.mp3");
            return;
        }

        if (isExpanded) {
            for (int i = myHandCards.size() - 1; i >= 0; i--) {
                CardUI card = myHandCards.get(i);
                if (card.contains(mx, my)) {
                    card.startDrag(mx, my);
                    break;
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();
        for (CardUI card : myHandCards) {
            if (card.isDragging()) card.dragTo(mx, my);
        }
    }

    public void mouseReleased(MouseEvent e) {
        for (CardUI card : myHandCards) {
            if (card.isDragging()) card.stopDrag();
        }
    }

    public CardUI getDraggingCard() {
        for (CardUI card : myHandCards) {
            if (card.isDragging()) return card;
        }
        return null;
    }

    public List<CardUI> getCards() {
        return myHandCards;
    }
}
