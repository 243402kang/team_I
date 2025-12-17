package game.ui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Easy extends BaseStage {
    
    private BufferedImage EasyBack;
    private BufferedImage EasyBoss;
    private BufferedImage Player;
    
    public Easy(Screen screen) {
        super(screen);
        loadImage();
        Characters();
    } 
    
    private void loadImage() {
        try {
            this.EasyBack = ImageIO.read(new File("res/easyBack.jpg"));
            this.EasyBoss = ImageIO.read(new File("res/easyBoss.png"));
            this.Player = ImageIO.read(new File("res/player.png"));
            
            this.EasyBack = resizeImage(EasyBack, 1280);
            this.EasyBoss = resizeImage(EasyBoss, 200);
            this.Player = resizeImage(Player, 100);
            
            Color color = new Color(63, 96, 57);
            
            this.EasyBoss = TransformColorToTransparency(EasyBoss, color);
            this.Player = TransformColorToTransparency(Player, color);
            
        } catch (IOException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void Characters() {
    	int bossX = (1280 - EasyBoss.getWidth()) / 2;
    	int bossY = 10;
    	this.boss = new CardDragge(bossX, bossY, EasyBoss);
    	 
    	int playerX = (1280 - Player.getWidth()) / 2;
    	int playerY = 650;
    	this.player = new CardDragge(playerX, playerY, Player);
    }
    
    public void draw(Graphics g, Screen screen) {
        g.drawImage(EasyBack, 0, 0, screen);
        drawField(g);
        drawCharacter(g);
        
        if (hand != null) hand.draw(g);
        drawResult(g);
    }
}