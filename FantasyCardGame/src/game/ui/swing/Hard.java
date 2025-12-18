package game.ui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Hard extends BaseStage {
    
    private BufferedImage HardBack;
    private BufferedImage HardBoss;
    private BufferedImage Player;
    
    public Hard(Screen screen) {
        super(screen);
        loadImage();
        Characters();
    }
    
    private void loadImage() {
        try {
            this.HardBack = ImageIO.read(getClass().getResource("/res/hardBack.jpg"));
            this.HardBoss = ImageIO.read(getClass().getResource("/res/hardBoss.png"));
            this.Player = ImageIO.read(getClass().getResource("/res/player.png"));
            
            this.HardBack = resizeImage(HardBack, 1280);
            this.HardBoss = resizeImage(HardBoss, 200);
            this.Player = resizeImage(Player, 100);
            
            Color color = new Color(63, 96, 57);
            
            this.HardBoss = TransformColorToTransparency(HardBoss, color);
            this.Player = TransformColorToTransparency(Player, color);
            
        } catch (IOException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void Characters() {
    	int bossX = (1280 - HardBoss.getWidth()) / 2;
    	int bossY = 10;
    	this.boss = new CardDragge(bossX, bossY, HardBoss);
    	
    	int playerX = (1280 - Player.getWidth()) / 2;
    	int playerY = 650;
    	this.player = new CardDragge(playerX, playerY, Player);
    }
    
    public void draw(Graphics g, Screen screen) {
        g.drawImage(HardBack, 0, 0, screen);
        drawField(g);
        drawCharacter(g);
        
        if (hand != null) hand.draw(g);
        drawResult(g);
    }
}