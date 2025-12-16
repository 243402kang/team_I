package game.ui.swing;

import java.awt.Color; 
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;


// 필드를 구성하는데 있어서 필요한 메서드들을 모아 놓은 부모(베이스) 클래스
public abstract class BaseStage {
	
	private game.battle.GameState gameState;
	private game.battle.TurnManager turnManager;
	private game.battle.CardExecutor cardExecutor;
	private game.battle.CombatEngine combatEngine;
	private game.battle.EnemyAI enemyAI;
	
    protected Screen screen;
    protected int x = 1280 / 2;
    protected CardDragge boss;
    protected CardDragge player;
    protected BufferedImage fieldImage;
    protected int boardX, boardY;
    protected int boardWidth, boardHeight;
    protected FieldSlot[] playerSlots = new FieldSlot[5];
    protected FieldSlot[] bossSlots = new FieldSlot[5];
    protected Hand hand;
    protected CardUI activeFieldCard = null;
    private BufferedImage cardBack;
    private int playerMana = 10;
    private int bossMana = 5;
    private int playerHp = 20;
    private int bossHp = 20;
    protected JButton endTurnBtn;
    protected int turnTime = 60;


    public BaseStage(Screen screen) {
        this.screen = screen;
        loadFieldImage();
        loadCardBack();
        FieldSlots();
        
        this.hand = new Hand();
        turnBtn();
        
        initBattle();
        
    }
    
    public BaseStage() {}
    
    // 이미지 비율, 크기 조절
    protected BufferedImage resizeImage(BufferedImage image, int newWidth) {
        if (image == null) return null; 
        
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
    
    // 필드 배경 이미지
    private void loadFieldImage() {
        try {
            this.fieldImage = ImageIO.read(new File("res/gameField.jpg"));
            
            this.boardWidth = 800;
            this.boardHeight = 450;
            
            this.boardX = (1280 - boardWidth) / 2;
            this.boardY = 200;
            
            this.fieldImage = resizeImage(fieldImage, boardWidth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // 카드 슬롯 5개 중앙 배치 (나, 보스)
    private void FieldSlots() {
        int slotWidth = 100;
        int slotHeight = 140;
        int gap = 50;
        
        int totalWidth = (slotWidth * 5) + (gap * 4);
        
        int startX = boardX + (boardWidth - totalWidth) / 2;
        
        int enemyY = boardY + 40;
        int playerY = boardY + boardHeight - slotHeight - 40;
        
        for (int i  = 0; i < 5; i++) {
            int x = startX + i * (slotWidth + gap);
            bossSlots[i] = new FieldSlot(x, enemyY, slotWidth, slotHeight, null);
            playerSlots[i] = new FieldSlot(x, playerY, slotWidth, slotHeight, null);
        }
    }
    
    // 필드 슬롯 그리기
    protected void drawField(Graphics g) {
        g.drawImage(fieldImage, boardX, boardY, boardWidth, boardHeight, screen);
        for (FieldSlot slot : bossSlots) { if (slot != null) slot.draw(g); }
        for (FieldSlot slot : playerSlots) { if (slot != null) slot.draw(g); }
        
        if (cardBack != null) {
            int deckWidth = 300;
            int deckHeight = 140;

            int myDeckX = -50;
            int myDeckY = 600;
            g.drawImage(cardBack, myDeckX, myDeckY, deckWidth, deckHeight, null);
            
            if (hand != null) {
                drawCenteredText(g, String.valueOf(hand.getCards().size()), myDeckX, myDeckY, deckWidth);
            }

            int bossDeckX = -50; 
            int bossDeckY = 50; 
            g.drawImage(cardBack, bossDeckX, bossDeckY, deckWidth, deckHeight, null);

            int bossHandCount = 10; 
            drawCenteredText(g, String.valueOf(bossHandCount), bossDeckX, bossDeckY, deckWidth);
        }
        
        drawManaBar(g, 930, 50, bossMana);
        drawManaBar(g, 930, 700, playerMana);
        
        drawTurnTime(g);
    }
    
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (hand != null) hand.mousePressed(e);

        if (hand == null || hand.getDraggingCard() == null) {
            for (FieldSlot slot : playerSlots) {
                if (slot != null && !slot.isEmpty() && slot.contains(mx, my)) {
                    activeFieldCard = slot.getCard();
                    activeFieldCard.startDrag(mx, my);
                    break;
                }
            }
        }

        if (player != null && player.contains(mx, my)) {
            player.startDrag(mx, my);
        }
    }
    
    public void mouseDragged(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (hand != null) hand.mouseDragged(e);

        if (activeFieldCard != null) {
            activeFieldCard.dragTo(mx, my);
        }

        if (boss != null) boss.dragTo(mx, my);
        if (player != null) player.dragTo(mx, my);
    }
    
    public void mouseReleased(MouseEvent e) {
        CardUI draggingHandCard = (hand != null) ? hand.getDraggingCard() : null;
        if (draggingHandCard != null) {
            boolean isPlaced = false;
            for (int i = 0; i < 5; i++) {
                FieldSlot slot = playerSlots[i];
                if (slot != null && slot.contains(e.getX(), e.getY()) && slot.isEmpty()) {
                    slot.setCard(draggingHandCard);
                    hand.removeCard(draggingHandCard);
                    isPlaced = true;
                    
                    MainFrame.bgplay("res/cardDrop.mp3");
                    break;
                }
            }
            if (!isPlaced) draggingHandCard.resetPosition();
            if (hand != null) hand.mouseReleased(e);
        }

        if (activeFieldCard != null) {
            if (boss != null && boss.contains(e.getX(), e.getY())) {
                System.out.println("하수인이 보스를 공격했습니다!");
                MainFrame.bgplay("res/attack.mp3");
            }
            activeFieldCard.resetPosition();
            activeFieldCard.stopDrag();
            activeFieldCard = null;
        }

        if (boss != null) {
            boss.stopDrag();
            boss.resetPosition();
        }
        
        if (player != null) {
            if (player.isDragging() && boss != null && boss.contains(e.getX(), e.getY())) {
                System.out.println("플레이어가 보스를 공격했습니다!");
                MainFrame.bgplay("res/attack.mp3");
            }
            player.stopDrag();
            player.resetPosition();
        }
    }
    
    // 캐릭터 그림
    protected void drawCharacter(Graphics g) {
        boss.draw(g);
        drawHp(g, boss.getX() - 15, boss.getY() + 150, bossHp);
        
        player.draw(g);
        drawHp(g, player.getX() - 35, player.getY() + 60, playerHp);
        
    }
    
    // 카드 뒷면 그리기
    private void loadCardBack() {
    	this.cardBack = CardImageManager.getInstance().getCardBackImage();
    }
    
    // 남은 카드 갯수
    private void drawCenteredText(Graphics g, String text, int x, int y, int size) {
    	g.setColor(Color.white);
    	g.setFont(new Font("Arial", Font.BOLD, 20));
    	
    	FontMetrics fm = g.getFontMetrics();
    	int textX = x + (size - fm.stringWidth(text)) / 2;
    	int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
    	
    	g.setColor(Color.black);
    	g.drawString(text, textX + 1, textY + 1);
    	g.setColor(Color.white);
    	g.drawString(text, textX, textY);
    } 
    
    // 마나 바 그리기 
    private void drawManaBar(Graphics g, int x, int y, int mana) {
    	for (int i = 0; i < 10; i++) {
    		g.setColor(i <mana ? Color.blue :Color.lightGray);
    		g.fillRect(x + (i * 30), y, 28, 20);
    		
    		g.setColor(Color.black);
    		g.drawRect(x + (i * 30), y, 28, 20);
    	}
    	
    	g.setColor(Color.blue);
    	g.fillOval(x + 305, y - 2, 24, 24);
    	
    	g.setColor(Color.white);
    	g.drawString(mana + "", x + (mana == 10 ? 306 : 310), y + 15);
    }
    
    // 체력 그리기
    private void drawHp(Graphics g, int x, int y, int hp) {
    	g.setColor(Color.red);
    	g.fillOval(x, y, 30, 30);
    	
    	g.setColor(Color.black);
    	g.drawOval(x, y, 30, 30);
    	
    	g.setColor(Color.yellow);
    	g.setFont(new Font("SansSerif", Font.BOLD, 18));
    	
    	int textX = x + (hp >= 10 ? 5 : 10);
    	int textY = y + 22;
    	
    	g.drawString(String.valueOf(hp), textX, textY);
    }
    
    // 턴 종료 버튼 
    private void turnBtn() {
    	endTurnBtn = new JButton("턴 종료");
    	int width = 100;
    	int height = 40;
    	int btnX = 1150;
    	int btnY = 400;
    	
    	endTurnBtn.setBounds(btnX, btnY, width, height);
    	
    	endTurnBtn.setBackground(Color.green);
    	endTurnBtn.setForeground(Color.black);
    	endTurnBtn.setVisible(false);
    	endTurnBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// 보스 턴으로 넘김
				System.out.println("턴 종료");
			    turnTime = 60;

			    // 1) 플레이어 턴 종료 -> ENEMY 턴으로 전환
			    printLog(turnManager.endTurn());

			    // 2) ENEMY 턴 시작
			    printLog(turnManager.startTurn());

			    // 3) ENEMY 메인 페이즈(=AI) 실행
			    printLog(enemyAI.playEnemyMainPhase());

			    // 4) ENEMY 턴 종료 -> PLAYER 턴으로 전환
			    printLog(turnManager.endTurn());

			    // 5) PLAYER 턴 시작
			    printLog(turnManager.startTurn());

			    // 6) 전투 결과를 UI 슬롯에 반영(아래 sync 메서드 )
			    syncBoardsToSlots();

			    screen.repaint();

			}
		});
    	screen.add(endTurnBtn);
    }
    
    private void syncBoardsToSlots() {
        // 1) PLAYER 보드 -> playerSlots 
        // 2) ENEMY 보드 -> bossSlots

        // bossSlots 비우기
        for (int i = 0; i < bossSlots.length; i++) {
            if (bossSlots[i] != null) bossSlots[i].setCard(null);
        }

        List<game.battle.UnitState> enemyBoard = gameState.getPlayerState(game.battle.BattleSide.ENEMY).getBoard();
        CardImageManager imgManager = CardImageManager.getInstance();
        List<game.card.card> all = game.card.CardRepository.getAllCards();

        for (int i = 0; i < enemyBoard.size() && i < bossSlots.length; i++) {
            game.card.card base = enemyBoard.get(i).getBaseCard();

            int imageIndex = 0;
            for (int k = 0; k < all.size(); k++) {
                if (all.get(k).getId().equals(base.getId())) { imageIndex = k; break; }
            }

            CardUI ui = new CardUI(base, imgManager.getCardImage(imageIndex), 0, 0);
            bossSlots[i].setCard(ui);
        }
    }
    
    public void showTurnBtn() {
    	endTurnBtn.setVisible(true);
    }
    
    public void hideTurnBtn() {
    	endTurnBtn.setVisible(false);
    }
    
    // 턴 시간 표시 
    protected void drawTurnTime(Graphics g) {
    	int btnX = 1280 - 100 - 20;
    	int btnY = (800 / 2) - (40 / 2);
    	
    	g.setFont(new Font("SansSerif", Font.BOLD, 20));
    	
    	if (turnTime <= 10) {
    		g.setColor(Color.red);
			MainFrame.bgplay("res/10sec.mp3");
    	}
    	else g.setColor(Color.yellow);
    	
    	String timeText = "Left Time : " + turnTime;
    	g.drawString(timeText, btnX - 50, btnY - 10);
    }
    
    private void printLog(game.battle.BattleLog log) {
        if (log == null) return;
        for (game.battle.BattleLogEntry e : log.getEntries()) {
            System.out.println(e.getMessage());
        }
    }
    
    private void initBattle() {
        // 영웅 생성
        game.battle.HeroState pHero = new game.battle.HeroState("PLAYER");
        game.battle.HeroState eHero = new game.battle.HeroState("ENEMY");

        game.battle.PlayerBattleState p = new game.battle.PlayerBattleState(pHero);
        game.battle.PlayerBattleState e = new game.battle.PlayerBattleState(eHero);

        // 적 손패는 랜덤 10장
        List<game.card.card> enemyHand = game.card.CardRepository.createShuffledDeck(10);
        for (game.card.card c : enemyHand) e.addCardToHand(c);

        this.gameState = new game.battle.GameState(p, e);
        this.turnManager = new game.battle.TurnManager(gameState);
        this.cardExecutor = new game.battle.CardExecutor(gameState);
        this.combatEngine = new game.battle.CombatEngine(gameState);
        this.enemyAI = new game.battle.EnemyAI(gameState);

        // 플레이어 첫 턴 시작
        printLog(turnManager.startTurn());
    }
    
}