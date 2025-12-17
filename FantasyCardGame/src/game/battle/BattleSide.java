package game.battle;

/**
 *
 * - PLAYER : 실제 플레이어
 * - ENEMY  : 적(인공지능)
 */
public enum BattleSide {

    PLAYER,
    ENEMY;

    /**
     * 현재 쪽의 상대편을 반환합니다.
     *
     * PLAYER  -> ENEMY
     * ENEMY   -> PLAYER
     */
    public BattleSide getOpponent() {
        return this == PLAYER ? ENEMY : PLAYER;
    }
}
