package game.ui.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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

import game.battle.BattleLog;
import game.battle.BattleLogEntry;
import game.battle.BattleSide;
import game.battle.CardExecutor;
import game.battle.CombatEngine;
import game.battle.EnemyAI;
import game.battle.GameState;
import game.battle.PlayerBattleState;
import game.battle.TurnManager;
import game.battle.TargetType;
import game.battle.UnitState;

public abstract class BaseStage {

    private GameState gameState;
    private TurnManager turnManager;
    private CardExecutor cardExecutor;
    private CombatEngine combatEngine;
    private EnemyAI enemyAI;

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

    protected JButton endTurnBtn;

    // UI 표시용 (GameState 남은시간에서 계산)
    protected int turnTimeSec = 60;
    
    private BufferedImage winImage;
    private BufferedImage loseImage;
    private boolean gameLose = false;
    private boolean gameWin = false;
    private int endTimer = 0;

    public BaseStage(Screen screen) {
        this.screen = screen;
        loadFieldImage();
        loadCardBack();
        loadResultImage();
        FieldSlots();

        this.hand = new Hand();
        turnBtn();

        initBattle();
        syncAllToUI();
    }

    public BaseStage() {}

    /**Screen에서 매 프레임(혹은 타이머) 호출하도록 만들 tick() */
    public void tick() {
        if (turnManager == null) return;
        
        if (gameWin || gameLose) {
        	endTimer++;
        	if (endTimer > 180) {
        		reset();
        		screen.returnSelect();
        	}
        	return;
        }
        
        // 남은 시간 표시 업데이트
        long remainMs = turnManager.getRemainingTurnTimeMillis();
        turnTimeSec = (int) Math.ceil(remainMs / 1000.0);

        // 시간 초과 자동 턴 종료
        BattleLog forced = turnManager.forceEndTurnIfTimeOver();
        if (forced != null && !forced.isEmpty()) {
            printLog(forced);

            // 시간 초과로 턴이 넘어갔다면:
            // PLAYER → ENEMY 자동 진행 / ENEMY → PLAYER 자동 진행을 "버튼 누른 것처럼" 처리
            autoProgressIfNeeded();
            syncAllToUI();
            screen.repaint();
        }
        checkResult();
    }

    /**시간이 강제 종료되었을 때도 버튼 클릭과 동일한 턴 진행을 수행 */
    private void autoProgressIfNeeded() {
        // 지금 턴 주체가 ENEMY라면, AI를 돌리고 PLAYER 턴까지 가져온다.
        if (gameState.getCurrentTurnSide() == BattleSide.ENEMY) {
            // ENEMY 턴 시작
            printLog(turnManager.startTurn());
            // AI 행동
            printLog(enemyAI.playEnemyMainPhase());
            // ENEMY 턴 종료
            printLog(turnManager.endTurn());
            // PLAYER 턴 시작
            printLog(turnManager.startTurn());
        }
    }

    protected BufferedImage resizeImage(BufferedImage image, int newWidth) {
        if (image == null) return null;

        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double ratio = (double) newWidth / (double) imageWidth;
        int w = (int) (imageWidth * ratio);
        int h = (int) (imageHeight * ratio);

        Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(resizeImage, 0, 0, null);
        g.dispose();

        return newImage;
    }

    protected BufferedImage TransformColorToTransparency(BufferedImage image, Color c1) {
        if (image == null) return null;

        final int r1 = c1.getRed();
        final int g1 = c1.getGreen();
        final int b1 = c1.getBlue();

        ImageFilter filter = new RGBImageFilter() {
            public int filterRGB(int x, int y, int rgb) {
                int alpha = (rgb >> 24) & 0xFF;
                int r = (rgb & 0xFF0000) >> 16;
                int g = (rgb & 0xFF00) >> 8;
                int b = (rgb & 0xFF);

                if (alpha == 0) return rgb;
                if (r == r1 && g == g1 && b == b1) {
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

    private void FieldSlots() {
        int slotWidth = 100;
        int slotHeight = 140;
        int gap = 50;

        int totalWidth = (slotWidth * 5) + (gap * 4);
        int startX = boardX + (boardWidth - totalWidth) / 2;

        int enemyY = boardY + 40;
        int playerY = boardY + boardHeight - slotHeight - 40;

        for (int i = 0; i < 5; i++) {
            int x = startX + i * (slotWidth + gap);
            bossSlots[i] = new FieldSlot(x, enemyY, slotWidth, slotHeight, null);
            playerSlots[i] = new FieldSlot(x, playerY, slotWidth, slotHeight, null);
        }
    }

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

            // battle 손패 개수 표시
            int myCount = 0;
            if (gameState != null) {
                myCount = gameState.getPlayerState(BattleSide.PLAYER).getHand().size();
            }
            drawCenteredText(g, String.valueOf(myCount), myDeckX, myDeckY, deckWidth);

            int bossDeckX = -50;
            int bossDeckY = 50;
            g.drawImage(cardBack, bossDeckX, bossDeckY, deckWidth, deckHeight, null);

            int bossHandCount = 0;
            if (gameState != null) {
                bossHandCount = gameState.getPlayerState(BattleSide.ENEMY).getHand().size();
            }
            drawCenteredText(g, String.valueOf(bossHandCount), bossDeckX, bossDeckY, deckWidth);
        }

        // 마나 표시(실제 battle에서 가져오기)
        int bossMana = (gameState == null) ? 0 : gameState.getPlayerState(BattleSide.ENEMY).getCurrentMana();
        int playerMana = (gameState == null) ? 0 : gameState.getPlayerState(BattleSide.PLAYER).getCurrentMana();

        drawManaBar(g, 930, 50, bossMana);
        drawManaBar(g, 930, 700, playerMana);

        drawTurnTime(g);
        
    }


    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (hand != null) hand.mousePressed(e);

        // 내 필드 카드 드래그 시작
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
        // 1) 손패 카드 드롭 → 실제 battle 소환 연결
        CardUI draggingHandCard = (hand != null) ? hand.getDraggingCard() : null;
        if (draggingHandCard != null) {
            boolean placed = false;

            for (int i = 0; i < 5; i++) {
                FieldSlot slot = playerSlots[i];
                if (slot != null && slot.contains(e.getX(), e.getY()) && slot.isEmpty()) {

                    //battle에 카드 사용(슬롯 i 위치로 소환)
                    BattleLog log = cardExecutor.playCard(
                            BattleSide.PLAYER,
                            draggingHandCard.getData(),
                            BattleSide.ENEMY,
                            TargetType.NONE, -1,
                            i
                    );
                    printLog(log);

                    placed = true;
                    MainFrame.bgplay("res/cardDrop.mp3");
                    break;
                }
            }

            // 못 놓았으면 원위치
            if (!placed) draggingHandCard.resetPosition();

            if (hand != null) hand.mouseReleased(e);

            //반드시 UI 동기화
            syncAllToUI();
            screen.repaint();
            return;
        }

        // 2) 내 필드 카드 드롭 → 공격 처리
        if (activeFieldCard != null) {

            Integer attackerIndex = findPlayerSlotIndexByCard(activeFieldCard);

            if (attackerIndex == null) {
                activeFieldCard.resetPosition();
                activeFieldCard.stopDrag();
                activeFieldCard = null;
                return;
            }

            // (A) 적 영웅(보스) 공격
            if (boss != null && boss.contains(e.getX(), e.getY())) {
                BattleLog log = combatEngine.attack(
                        BattleSide.PLAYER,
                        attackerIndex,
                        TargetType.HERO,
                        -1
                );
                printLog(log);
                MainFrame.bgplay("res/attack.mp3");
            } else {
                // (B) 적 유닛(슬롯) 공격
                Integer defenderIndex = findBossSlotIndexAtPoint(e.getX(), e.getY());
                if (defenderIndex != null) {
                    BattleLog log = combatEngine.attack(
                            BattleSide.PLAYER,
                            attackerIndex,
                            TargetType.UNIT,
                            defenderIndex
                    );
                    printLog(log);
                    MainFrame.bgplay("res/attack.mp3");
                }
            }

            activeFieldCard.resetPosition();
            activeFieldCard.stopDrag();
            activeFieldCard = null;

            //전투 결과 동기화(죽은 유닛 제거, 체력 반영)
            syncAllToUI();
            screen.repaint();
        }

        if (boss != null) {
            boss.stopDrag();
            boss.resetPosition();
        }

        if (player != null) {
            player.stopDrag();
            player.resetPosition();
        }
    }

    /** 내 슬롯에서 activeFieldCard가 몇 번째 슬롯인지 찾기 */
    private Integer findPlayerSlotIndexByCard(CardUI card) {
        for (int i = 0; i < playerSlots.length; i++) {
            FieldSlot s = playerSlots[i];
            if (s != null && !s.isEmpty() && s.getCard() == card) return i;
        }
        return null;
    }

    /** 드롭 좌표가 적 슬롯(0~4) 위인지 찾기 */
    private Integer findBossSlotIndexAtPoint(int x, int y) {
        for (int i = 0; i < bossSlots.length; i++) {
            FieldSlot s = bossSlots[i];
            if (s != null && !s.isEmpty() && s.contains(x, y)) return i;
        }
        return null;
    }

    protected void drawCharacter(Graphics g) {
        boss.draw(g);
        int bossHp = (gameState == null) ? 0 : gameState.getPlayerState(BattleSide.ENEMY).getHero().getCurrentHealth();
        drawHp(g, boss.getX() - 15, boss.getY() + 150, bossHp);

        player.draw(g);
        int playerHp = (gameState == null) ? 0 : gameState.getPlayerState(BattleSide.PLAYER).getHero().getCurrentHealth();
        drawHp(g, player.getX() - 35, player.getY() + 60, playerHp);
    }

    private void loadCardBack() {
        this.cardBack = CardImageManager.getInstance().getCardBackImage();
    }

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

    private void drawManaBar(Graphics g, int x, int y, int mana) {
        for (int i = 0; i < 10; i++) {
            g.setColor(i < mana ? Color.blue : Color.lightGray);
            g.fillRect(x + (i * 30), y, 28, 20);

            g.setColor(Color.black);
            g.drawRect(x + (i * 30), y, 28, 20);
        }

        g.setColor(Color.blue);
        g.fillOval(x + 305, y - 2, 24, 24);

        g.setColor(Color.white);
        g.drawString(mana + "", x + (mana == 10 ? 306 : 310), y + 15);
    }

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

                // PLAYER 턴 종료
                printLog(turnManager.endTurn());

                // ENEMY 턴 시작
                printLog(turnManager.startTurn());

                // AI 행동
                printLog(enemyAI.playEnemyMainPhase());

                // ENEMY 턴 종료
                printLog(turnManager.endTurn());

                // PLAYER 턴 시작
                printLog(turnManager.startTurn());

                // UI 동기화
                syncAllToUI();
                screen.repaint();
            }
        });
        screen.add(endTurnBtn);
    }

    public void showTurnBtn() { 
    	endTurnBtn.setVisible(true); 
    }
    
    public void hideTurnBtn() {
    	endTurnBtn.setVisible(false);
    }

    protected void drawTurnTime(Graphics g) {
        int btnX = 1280 - 100 - 20;
        int btnY = (800 / 2) - (40 / 2);

        g.setFont(new Font("SansSerif", Font.BOLD, 20));

        if (turnTimeSec <= 10) {
            g.setColor(Color.red);
            // 10초 효과음 연타 방지(원하면 플래그로 한번만 재생 가능)
        } else {
            g.setColor(Color.yellow);
        }

        String timeText = "Left Time : " + turnTimeSec;
        g.drawString(timeText, btnX - 50, btnY - 10);
    }

    private void printLog(BattleLog log) {
        if (log == null) return;
        for (BattleLogEntry e : log.getEntries()) {
            System.out.println(e.getMessage());
        }
    }

    private void initBattle() {
        // 영웅(체력 20 고정)
        game.battle.HeroState pHero = new game.battle.HeroState("PLAYER");
        game.battle.HeroState eHero = new game.battle.HeroState("ENEMY");

        PlayerBattleState p = new PlayerBattleState(pHero);
        PlayerBattleState e = new PlayerBattleState(eHero);

        // 플레이어/적 덱: 전체 카드 셔플 20장
        List<game.card.card> pDeck = game.card.CardRepository.createShuffledDeck(20);
        for (game.card.card c : pDeck) p.addCardToDeck(c);

        List<game.card.card> eDeck = game.card.CardRepository.createShuffledDeck(20);
        for (game.card.card c : eDeck) e.addCardToDeck(c);

        // 적 손패 10장 (AI 테스트용)
        List<game.card.card> enemyHand = game.card.CardRepository.createShuffledDeck(0);
        for (game.card.card c : enemyHand) e.addCardToHand(c);

        this.gameState = new GameState(p, e);
        this.turnManager = new TurnManager(gameState);
        this.cardExecutor = new CardExecutor(gameState);
        this.combatEngine = new CombatEngine(gameState);
        this.enemyAI = new EnemyAI(gameState);

        // PLAYER 첫 턴 시작
        printLog(turnManager.startTurn());
    }

    /**battle 상태를 UI(슬롯/손패)로 전부 동기화 */
    private void syncAllToUI() {
        syncBoardsToSlots();
        syncHandFromBattle();
    }

    private void syncHandFromBattle() {
        if (hand == null || gameState == null) return;
        List<game.card.card> battleHand = gameState.getPlayerState(BattleSide.PLAYER).getHand();
        hand.setFromBattleHand(battleHand);
    }

    private void syncBoardsToSlots() {
        if (gameState == null) return;

        // 슬롯 비우기
        for (int i = 0; i < 5; i++) {
            if (playerSlots[i] != null) playerSlots[i].setCard(null);
            if (bossSlots[i] != null) bossSlots[i].setCard(null);
        }

        CardImageManager imgManager = CardImageManager.getInstance();
        List<game.card.card> all = game.card.CardRepository.getAllCards();

        // PLAYER 보드 -> playerSlots
        List<UnitState> pBoard = gameState.getPlayerState(BattleSide.PLAYER).getBoard();
        for (int i = 0; i < pBoard.size() && i < playerSlots.length; i++) {
            UnitState unit = pBoard.get(i);
            game.card.card base = pBoard.get(i).getBaseCard();
            int imageIndex = findImageIndexById(all, base.getId());
            CardUI ui = new CardUI(base, imgManager.getCardImage(imageIndex), 0, 0);
            ui.setHp(unit.getCurrentHealth());
            playerSlots[i].setCard(ui);
        }

        // ENEMY 보드 -> bossSlots
        List<UnitState> eBoard = gameState.getPlayerState(BattleSide.ENEMY).getBoard();
        for (int i = 0; i < eBoard.size() && i < bossSlots.length; i++) {
        	UnitState unit = eBoard.get(i);
            game.card.card base = eBoard.get(i).getBaseCard();
            int imageIndex = findImageIndexById(all, base.getId());
            CardUI ui = new CardUI(base, imgManager.getCardImage(imageIndex), 0, 0);
            ui.setHp(unit.getCurrentHealth());
            bossSlots[i].setCard(ui);
        }
    }

    private int findImageIndexById(List<game.card.card> all, String id) {
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(id)) return i;
        }
        return 0;
    }
    
    private void checkResult() {
    	int playerHp = gameState.getPlayerState(BattleSide.PLAYER).getHero().getCurrentHealth();
    	int bossHp = gameState.getPlayerState(BattleSide.ENEMY).getHero().getCurrentHealth();
    	
    	if (playerHp <= 0) {
    		gameLose = true;
    		gameWin = false;
    		hideTurnBtn();
    		MainFrame.bgplay("res/gameLose.mp3");
    	} else if (bossHp <= 0) {
    		gameWin = true;
    		gameLose = false;
    		hideTurnBtn();
    		MainFrame.bgplay("res/gameWin.mp3");
    	}
    }
    
    private void loadResultImage() {
        try {
            File w = new File("res/gameWin.jpg"); 
            
            if (w.exists()) {
                this.winImage = ImageIO.read(w);
                System.out.println("승리 이미지 로딩 성공!");
            } else {
                System.out.println("승리 이미지 파일을 찾을 수 없음: " + w.getAbsolutePath());
            }
            
            File l = new File("res/gameLose.jpg");
            if (l.exists()) {
                this.loseImage = ImageIO.read(l);
                System.out.println("패배 이미지 로딩 성공!");
            } else {
                System.out.println("패배 이미지 파일을 찾을 수 없음: " + l.getAbsolutePath());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void endTurnBtn() {
    	if (gameWin || gameLose) {
    		endTurnBtn.setVisible(false);
    		return;
    	}
    	endTurnBtn.setVisible(true);
    }
    
    public void reset() {
    	gameWin = false;
    	gameLose = false;
    	endTimer = 0;
    	
    	initBattle();
    	syncAllToUI();
    }
    
    public void drawResult(Graphics g){{
    if (gameWin || gameLose) {

        BufferedImage imgToDraw = null;
        if (gameWin) imgToDraw = winImage;
        else if (gameLose) imgToDraw = loseImage;

        if (imgToDraw != null) {
            int x = (1280 - imgToDraw.getWidth()) / 2;
            int y = (800 - imgToDraw.getHeight()) / 2;
            g.drawImage(imgToDraw, x, y, screen);
        		} 
    		}
    	}
    }
}

