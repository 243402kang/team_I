package game.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class EscMenu {
	
	private JButton selectBtn;
	private JButton exitBtn;
	private Screen screen;
	private boolean isVisible = false;
	
	public EscMenu(Screen screen) {
		this.screen = screen;
		
		int width = 200;
		int height = 50;
		int x = (1280 - width) / 2;
		int y =  350;
		
		selectBtn = new JButton("스테이지 선택");
		selectBtn.setBounds(x, y, width, height);
		selectBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleMenu();
				screen.returnSelect();
			}
		});
		
		exitBtn = new JButton("게임 종료");
		exitBtn.setBounds(x, y + 60, width, height);
		exitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleMenu();
				System.exit(0);
			}
		});
		
		screen.add(selectBtn);
		screen.add(exitBtn);
		hideEscBtn();
	}
	
	public void toggleMenu() {
		isVisible = !isVisible;
		if (isVisible) showEscBtn();
		else hideEscBtn();
	}

	public void hideEscBtn() {
		selectBtn.setVisible(false);
		exitBtn.setVisible(false);
	}
	
	public void showEscBtn() {
		selectBtn.setVisible(true);
		exitBtn.setVisible(true);
	}
	
	public boolean isVisible() {
		return isVisible;
	}
}
