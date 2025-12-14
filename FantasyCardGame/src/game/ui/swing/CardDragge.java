package game.ui.swing;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class CardDragge {
    private int x, y;
    private int width, height;
    private BufferedImage img;
    
    private boolean isDragging = false;
    private int dragX, dragY;
    private int originX, originY;

    public CardDragge(int x, int y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.originX = x;
        this.originY = y;
        this.img = img;
        this.width = img.getWidth();
        this.height = img.getHeight();
    }

    public void draw(Graphics g) {
        if (img != null) {
            g.drawImage(img, x, y, width, height, null);
        }
    }

    public boolean contains(int mx, int my) {
        return (mx >= x && mx <= x + width && my >= y && my <= y + height);
    }

    public void startDrag(int mx, int my) {
        this.isDragging = true;
        this.dragX = mx - x;
        this.dragY = my - y;
    }

    public void dragTo(int mx, int my) {
        if (isDragging) {
            this.x = mx - dragX;
            this.y = my - dragY;
        }
    }

    public void stopDrag() {
        this.isDragging = false;
    }
    
    public boolean isDragging() {
    	return isDragging;
    }
    
    public void resetPosition() {
    	this.x = originX;
    	this.y = originY;
    }
    
    public Rectangle getRect() {
    	return new Rectangle(x, y, width, height);
    }
    
    public int getX() {
    	return x;
    }
    
    public int getY() {
    	return y;
    }
}