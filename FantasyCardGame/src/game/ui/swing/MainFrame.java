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
		bgplay("res/bgmusic.mp3");
	}
	public static void bgplay(String filename) {
		Player jlPlayer = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(filename);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			jlPlayer = new Player(bufferedInputStream);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		final Player player = jlPlayer;
		new Thread() {
			public void run() {
				try {
					player.play();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}.start();
	}
}
