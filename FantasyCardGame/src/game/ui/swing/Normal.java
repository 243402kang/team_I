package game.ui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Normal extends BaseStage {
    
    private BufferedImage NormalBack;
    private BufferedImage NormalBoss;
    private BufferedImage Player;
    
    public Normal(Screen screen) {
        super(screen);
        loadImage();
        Characters();
    }
    
    private void loadImage() {
        try {
            this.NormalBack = ImageIO.read(new File("res/normalBack.jpg"));
            this.NormalBoss = ImageIO.read(new File("res/normalBoss.png"));
            this.Player = ImageIO.read(new File("res/player.png"));
            
            this.NormalBack = resizeImage(NormalBack, 1280);
            this.NormalBoss = resizeImage(NormalBoss, 200);
            this.Player = resizeImage(Player, 100);
            
            Color color = new Color(63, 96, 57);
            
            this.NormalBoss = TransformColorToTransparency(NormalBoss, color);
            this.Player = TransformColorToTransparency(Player, color);
            
        } catch (IOException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void Characters() {
    	int bossX = (1280 - NormalBoss.getWidth()) / 2;
    	int bossY = 10;
    	this.boss = new CardDragge(bossX, bossY, NormalBoss);
    	
    	int playerX = (1280 - Player.getWidth()) / 2;
    	int playerY = 650;
    	this.player = new CardDragge(playerX, playerY, Player);
    }
    
    public void draw(Graphics g, Screen screen) {
        g.drawImage(NormalBack, 0, 0, screen);
        drawField(g);
        drawCharacter(g);
        
        if (hand != null) hand.draw(g);
        drawResult(g);
    }
}