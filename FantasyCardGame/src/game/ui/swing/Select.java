	package game.ui.swing;
	import java.awt.Graphics;
	import java.awt.Image;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
	import java.awt.image.BufferedImage;
	import java.io.File;
	import java.io.IOException;
	import game.stage.Stage;
	
	import javax.imageio.ImageIO;
	import javax.swing.JButton;
	
	public class Select {
		private JButton Easy = new JButton("Easy");
		private JButton Normal = new JButton("Normal");
		private JButton Hard = new JButton("Hard");
		
		private BufferedImage select;
		
		public Select(Screen screen) {
			loadImage();
			Easy.setText("Easy");
			Normal.setText("Normal");
			Hard.setText("Hard");
			
			int buttonWidth = 200;
			int buttonHeight = 50;
			int center = 530;
			
			Easy.setBounds(center, 250, buttonWidth, buttonHeight);
			Normal.setBounds(center, 350, buttonWidth, buttonHeight);
			Hard.setBounds(center, 450, buttonWidth, buttonHeight);
			
			Easy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					screen.setDifficulty(Stage.Difficulty.EASY);
					screen.startBattle(2);
					hideButtons();
					MainFrame.bgplay("/res/cardShuffule.mp3");
				}
			});
			
			Normal.addActionListener(new ActionListener() {	
				@Override
				public void actionPerformed(ActionEvent e) {
					screen.setDifficulty(Stage.Difficulty.NORMAL);
					screen.startBattle(3);
					hideButtons();
					MainFrame.bgplay("/res/cardShuffule.mp3");
				}
			});
			
			Hard.addActionListener(new ActionListener() {	
				@Override
				public void actionPerformed(ActionEvent e) {
					screen.setDifficulty(Stage.Difficulty.HARD);
					screen.startBattle(4);
					hideButtons();
					MainFrame.bgplay("/res/cardShuffule.mp3");
				}
			});
			
			screen.add(Easy);
			screen.add(Normal);
			screen.add(Hard);
		}
		
		private void loadImage() {
			try {
				this.select = ImageIO.read(getClass().getResource("/res/selectBack.jpg"));
				
				this.select = resizeImage(select, 1280);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		}
		
		private BufferedImage resizeImage(BufferedImage image, int newWidth) {
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);
			double ratio = (double)newWidth/(double)imageWidth;
			int w = (int)(imageWidth * ratio);
			int h = (int)(imageHeight * ratio);
			Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
			BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics g = newImage.getGraphics();
			g.drawImage(resizeImage, 0, 0, null);
			g.dispose(); 
			return newImage;
		}
		
		public void showButtons() {
			Easy.setVisible(true);
			Normal.setVisible(true);
			Hard.setVisible(true);
		}
		
		public void hideButtons() {
			Easy.setVisible(false);
			Normal.setVisible(false);
			Hard.setVisible(false);
		}
		
		public void draw(Graphics bg, Screen screen) {
			bg.drawImage(select, 0, 0, screen);
		}
	}
