package game.ui.swing;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class StageTitle {
	
	private BufferedImage logo;	
	private BufferedImage start_back;
	private BufferedImage[] start_card = new BufferedImage[4]; 
	private int sky_y = -900;	 

	
	public StageTitle() {	
		loadImage();
	}
	private void loadImage() {	
		try {
			this.start_card[0] = ImageIO.read(new File("res/start_card1.png"));
			this.start_card[1] = ImageIO.read(new File("res/start_card2.png"));
			this.start_card[2] = ImageIO.read(new File("res/start_card3.png"));
			this.start_card[3] = ImageIO.read(new File("res/start_card4.png"));
			this.start_back = ImageIO.read(new File("res/startBack.jpg"));
			this.logo = ImageIO.read(new File("res/logo.png"));
			
			this.start_back = resizeImage(start_back, 1400);
			this.logo = resizeImage(logo, 600);
			
			Color color = new Color(63, 96, 57);
			this.logo = TransformColorToTransparency(logo, color);
			for(int i=0; i < 4; i++) {
				this.start_card[i] = TransformColorToTransparency(this.start_card[i], color);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BufferedImage resizeImage(BufferedImage image, int newWidth) {	
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
	
	public static BufferedImage TransformColorToTransparency(BufferedImage image, Color c1) {	
		final int r1 = c1.getRed();
		final int g1 = c1.getGreen();
		final int b1 = c1.getBlue();
		
		ImageFilter filter = new RGBImageFilter() {	
			public int filterRGB(int x, int y, int rgb) {
				int alpha = (rgb >> 24) & 0xFF;
				int r = ( rgb & 0xFF0000 ) >> 16;
				int g = ( rgb & 0xFF00 ) >> 8;
				int b = ( rgb & 0xFF );
				
				if(alpha == 0) {	
				return rgb;	 
			}
				
				if(r == r1 && g == g1 && b == b1) {	
					return rgb & 0x00FFFFFF;
				}
				return rgb;
			}
		};
		ImageProducer ip = new FilteredImageSource( image.getSource(), filter );	
		Image img = Toolkit.getDefaultToolkit().createImage(ip);
		BufferedImage dest = new BufferedImage(img.getWidth(null),	
		img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = dest.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return dest;
	}
	
	private int fight_y = 80;	
	private int time_step = 800;	
	private int start_index = 0;	
	private boolean blink = false;	
	private int blink_time = 200;	
	
	public void draw(Graphics g, Screen screen) {	
		g.drawImage(this.start_back, 0, sky_y, screen);
		
		if(sky_y >= -10) {
		if(screen.getCount() % blink_time == 0) {	
			blink = !blink;	 
			if(blink)	
				blink_time = 1000;	
			else
				blink_time = 200;	
		}
	}
		if(blink) {	
			g.drawImage(this.logo, 335, 100, screen);
		}
		
		if(start_index == 0) {	
			g.drawImage(this.start_card[0], 100, fight_y, screen);
		}
		else if(start_index == 1) {
			g.drawImage(this.start_card[1], 100, fight_y, screen);
		}
		else if(start_index == 2) {
			g.drawImage(this.start_card[2], 100, fight_y, screen);
		}
		else {
			g.drawImage(this.start_card[3], 100, fight_y, screen);
		}
		
		
		if(screen.getCount() % time_step == 0) {	
			start_index++;	
			if(start_index >= 3) {	
				start_index = 3;	
				time_step = 20;	
				if(sky_y <= -10) {
					sky_y += 10;
					fight_y += 10;	
					}
				}
			}
		}
	
	public boolean getStageEnd() {	
		return sky_y >= -10;
	}
}
