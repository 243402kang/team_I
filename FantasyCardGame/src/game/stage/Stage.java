package game.stage;

public final class Stage {
    private Stage() {}

    public enum Difficulty {
    	//적 체력, 적 시작 마나, 적 시작 드로우, 플레이어 시작 드로우
        EASY  (20, 1, 3, 4),
        NORMAL(30, 1, 4, 3),
        HARD  (40, 2, 7, 2);

        public final int enemyHp;
        public final int enemyStartMana;
        public final int enemyStartHand;
        public final int playerStartHand;

        Difficulty(int enemyHp, int enemyStartMana, int enemyStartHand,int playerStartHand) {
            this.enemyHp = enemyHp;
            this.enemyStartMana = enemyStartMana;
            this.enemyStartHand = enemyStartHand;
            this.playerStartHand = playerStartHand;
        }
    }
}