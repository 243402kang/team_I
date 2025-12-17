package game.battle;

public enum TurnPhase {

    /**
     * 턴 시작 단계.
     * - 마나 증가
     * - 드로우
     * - 준비 로직
     */
    START,

    /**
     * 메인 단계.
     * - 카드 사용
     * - 유닛 공격
     */
    MAIN,

    /**
     * 턴 종료 단계.
     * - 턴 종료 정리
     */
    END
}
