package game.ui.swing;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.swing.JFrame;

import javazoom.jl.player.Player;

public class MainFrame extends JFrame {
	public MainFrame() {
		setTitle("짭스스톤");
		setSize(1280, 800);
		add(new Screen());
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bgplay("/res/bgmusic.mp3");
	}
	public static void bgplay(String filename) {
	    Player jlPlayer = null;
	    try {
	        String resourcePath = filename.startsWith("/") ? filename : "/" + filename;
	        
	        java.io.InputStream is = MainFrame.class.getResourceAsStream(resourcePath);
	        
	        if (is != null) {
	            java.io.BufferedInputStream bufferedInputStream = new java.io.BufferedInputStream(is);
	            jlPlayer = new Player(bufferedInputStream);
	        } else {
	            System.err.println("MP3 리소스를 찾을 수 없음: " + resourcePath);
	            return;
	        }
	    } catch (Exception e) {
	        System.err.println("사운드 재생 오류: " + e.getMessage());
	    }
	    
	    final Player player = jlPlayer;
	    new Thread() {
	        public void run() {
	            try { if (player != null) player.play(); } 
	            catch (Exception e) { e.printStackTrace(); }
	        }
	    }.start();
	}
}
