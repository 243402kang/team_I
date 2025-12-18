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
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class CardImageManager {
    
    private List<BufferedImage> cardImages = new ArrayList<>();
    
    private static CardImageManager instance;

    private CardImageManager() {
        loadAndSliceImages();
    }

    public static CardImageManager getInstance() {
        if (instance == null) {
            instance = new CardImageManager();
        }
        return instance;
    }

    private void loadAndSliceImages() {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResource("/res/units.jpg"));

            int rows = 3;
            int cols = 8;
            
            int cardWidth = sheet.getWidth() / cols;
            int cardHeight = sheet.getHeight() / rows;

            int totalCards = 20;
            int count = 0;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if (count >= totalCards) break; 

                    int x = col * cardWidth;
                    int y = row * cardHeight;

                    BufferedImage subImage = sheet.getSubimage(x, y, cardWidth, cardHeight);
                    cardImages.add(subImage);
                    
                    count++;
                }
            }
            System.out.println("카드 이미지 " + cardImages.size() + "장 로딩 완료!");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("units.jpg 이미지를 찾을 수 없습니다!");
        }
    }
    
    public BufferedImage getCardBackImage() {
       try {
            BufferedImage original = ImageIO.read(getClass().getResource("/res/cardBack.png"));
            
            BufferedImage resized = resizeImage(original, 300, 140);
            
            Color colorToRemove = new Color(63, 96, 57);
            BufferedImage finalImg = TransformColorToTransparency(resized, colorToRemove);
            
            return finalImg;
            
       } catch (IOException e) {
            e.printStackTrace();
            return null;
       }
    }

    public BufferedImage getCardImage(int index) {
        if (index >= 0 && index < cardImages.size()) {
            return cardImages.get(index);
        }
        return null;
    }
    
    protected BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight) {
        if (image == null) return null; 
        
        Image tmp = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        
        BufferedImage dimg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        
        return dimg;
    }
    
    // 특정 색 투명하게
    protected BufferedImage TransformColorToTransparency(BufferedImage image, Color c1) {
        if (image == null) return null;

        final int r1 = c1.getRed();
        final int g1 = c1.getGreen();
        final int b1 = c1.getBlue();
        
        ImageFilter filter = new RGBImageFilter() {
            public int filterRGB(int x, int y, int rgb) {
                int alpha = (rgb >> 24) & 0xFF;
                int r = ( rgb & 0xFF0000 ) >> 16;
                int g = ( rgb & 0xFF00 ) >> 8;
                int b = ( rgb & 0xFF );
                
                if(alpha == 0) return rgb;
                
                if(r == r1 && g == g1 && b == b1) {
                    return rgb & 0x00FFFFFF;
                }
                return rgb;
            }
        };
        
        ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
        Image img = Toolkit.getDefaultToolkit().createImage(ip);
        
        BufferedImage dest = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dest.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        
        return dest;
    }
}