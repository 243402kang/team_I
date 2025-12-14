package game.ui.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints; // 텍스트 깨짐 방지
import java.awt.image.BufferedImage;

// 팀원이 만든 데이터 클래스 import
import game.card.card;

public class CardUI {
    
    private card data;           
    private BufferedImage image; 
    
    private int x, y;
    private int width = 100;     
    private int height = 140;
    
    private boolean isHovered = false;
    private boolean isSelected = false;
    
    private int originX, originY;
    private boolean isDragging = false;

    public CardUI(card data, BufferedImage image, int x, int y) {
        this.data = data;
        this.image = image;
        this.x = x;
        this.y = y;
        this.originX = x;
        this.originY = y;
    }

    public void draw(Graphics g) {
            g.drawImage(image, x, y, width, height, null);

        if (isSelected) { /* ... */ } 
        else if (isHovered) { /* ... */ }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));

        if (data != null) {
            g2d.setColor(Color.RED);
            g2d.drawString(String.valueOf(data.getAttack()), x + 10, y + height - 10);

            g2d.setColor(Color.BLUE);
            g2d.drawString(String.valueOf(data.getDefense()), x + width - 25, y + height - 10);

            g2d.setColor(Color.YELLOW);
            g2d.drawString(String.valueOf(data.getCost()), x + 8, y + 20);
            
        }
    }
    
    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
    
    public boolean contains(int mx, int my) {
        return getRect().contains(mx, my);
    }
    
    public void startDrag(int mx, int my) {
        this.isDragging = true;
    }
    
    public void dragTo(int mx, int my) {
        if (isDragging) {
            this.x = mx - width / 2;
            this.y = my - height / 2;
        }
    }
    
    public void stopDrag() {
        this.isDragging = false;
    }
    
    public void resetPosition() {
        this.x = originX;
        this.y = originY;
    }
    
    public boolean isDragging() { return isDragging; }
    
    public card getData() {
        return data;
    }
    
    public void setPosition(int x, int y) {
    	this.x = x;
    	this.y = y;
    	this.originX = x;
    	this.originY = y;
    }
    
    private boolean isOnField = false;
    public void setOnField(boolean onField) {
    	this.isOnField = onField;
    }
    public boolean isOnField() {
    	return isOnField;
    }
    
}