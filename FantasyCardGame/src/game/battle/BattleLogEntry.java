package game.battle;

import java.time.LocalDateTime;

/**
 * 전투 중 발생한 하나의 이벤트를 표현하는 로그 항목 클래스
 *
 * - timestamp : 발생 시각
 * - message   : 사람이 읽을 수 있는 설명
 */
public class BattleLogEntry {

    private final LocalDateTime timestamp;
    private final String message;

    public BattleLogEntry(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + message;
    }
}
