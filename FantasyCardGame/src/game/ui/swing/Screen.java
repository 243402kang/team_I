package game.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class Screen extends JPanel implements ComponentListener, KeyListener, MouseListener, MouseMotionListener {

    private Graphics bg;
    private Image offScreen;
    private Dimension dim;

    private int countNumber = 0;
    private StageTitle stageTitle = new StageTitle();
    private Select select = new Select(this);
    private Easy easy = new Easy(this);
    private Normal normal = new Normal(this);
    private Hard hard = new Hard(this);
    int stage = 0;
    private EscMenu escMenu;

    public Screen() {
        setLayout(null);
        addKeyListener(this);
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);

        setFocusable(true);
        escMenu = new EscMenu(this);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
                
                if (escMenu.isVisible()) {
                	return;
                }
                counting();
                turnBtn();

                //스테이지 tick 연결(턴시간 표시/자동 종료)
                if (stage == 2) easy.tick();
                else if (stage == 3) normal.tick();
                else if (stage == 4) hard.tick();
            }
        }, 0, 16); //1ms -> 16ms

        select.hideButtons();
    }

    public void returnSelect() {
        this.stage = 1;
        select.showButtons();
    }

    public void counting() { this.countNumber++; }
    public int getCount() { return this.countNumber; }

    private void initBuffer() {
        this.dim = getSize();
        this.offScreen = createImage(dim.width, dim.height);
        this.bg = this.offScreen.getGraphics();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (bg == null || dim == null) initBuffer();

        bg.clearRect(0, 0, dim.width, dim.height);

        if (stage == 0) {
            stageTitle.draw(bg, this);
        } else if (stage == 1) {
            select.draw(bg, this);
        } else if (stage == 2) {
            easy.draw(bg, this);
        } else if (stage == 3) {
            normal.draw(bg, this);
        } else if (stage == 4) {
            hard.draw(bg, this);
        }

        g.drawImage(offScreen, 0, 0, this);
    }

    private void turnBtn() {
    	
    	if (escMenu.isVisible()) {
    		easy.hideTurnBtn();
    		normal.hideTurnBtn();
    		hard.hideTurnBtn();
    		return;
    	}
    	
        if (stage == 2) easy.showTurnBtn();
        else easy.hideTurnBtn();

        if (stage == 3) normal.showTurnBtn();
        else normal.hideTurnBtn();

        if (stage == 4) hard.showTurnBtn();
        else hard.hideTurnBtn();
    }

    @Override
    public void componentResized(ComponentEvent e) { initBuffer(); }
    @Override public void componentMoved(ComponentEvent e) {}
    @Override public void componentShown(ComponentEvent e) {}
    @Override public void componentHidden(ComponentEvent e) {}

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                escMenu.toggleMenu();
                repaint();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (stage == 0) {
            stage = 1;
            select.showButtons();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (stage == 2) easy.mousePressed(e);
        else if (stage == 3) normal.mousePressed(e);
        else if (stage == 4) hard.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (stage == 2) easy.mouseReleased(e);
        else if (stage == 3) normal.mouseReleased(e);
        else if (stage == 4) hard.mouseReleased(e);
    }

    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (stage == 2) easy.mouseDragged(e);
        else if (stage == 3) normal.mouseDragged(e);
        else if (stage == 4) hard.mouseDragged(e);
    }

    @Override public void mouseMoved(MouseEvent e) {}
}
