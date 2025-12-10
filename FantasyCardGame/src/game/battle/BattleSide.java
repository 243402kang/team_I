package game.battle;

/**
 * 전투에서 어느 쪽 플레이어(또는 유닛)인지 나타내는 열거형입니다.
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
