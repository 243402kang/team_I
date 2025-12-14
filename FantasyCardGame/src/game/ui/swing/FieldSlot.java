package game.ui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class FieldSlot {
    
    private int x, y;
    private int width;
    private int height;
    private BufferedImage backgroundImage; 
    private CardUI equippedCard = null;

    public FieldSlot(int x, int y, int width, int height, BufferedImage bg) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundImage = bg;
        
    }

    public void draw(Graphics g) {
            g.drawImage(backgroundImage, x, y, width, height, null);

        g.setColor(new Color(255, 255, 255, 150)); 
        g.drawRect(x, y, width, height);
        
        if (equippedCard != null) {
        	equippedCard.draw(g);
        }
    }
    
    public boolean contains(int mx, int my) {
        return new Rectangle(x, y, width, height).contains(mx, my);
    }
    
    public void setCard(CardUI card) {
    	this.equippedCard = card;
    	if (card != null) {
    		card.setPosition(this.x, this.y);
    		card.setOnField(true);
    		card.stopDrag();
    	}
    }
    
    public CardUI getCard() {
    	return equippedCard;
    }
    
    public boolean isEmpty() {
    	return equippedCard == null;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}