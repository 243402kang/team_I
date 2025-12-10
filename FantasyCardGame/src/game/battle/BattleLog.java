package game.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 전투 중에 발생하는 여러 이벤트 로그를 한 번에 묶어서 전달하기 위한 클래스입니다.
 *
 * 전투 엔진(CombatEngine), 카드 실행(CardExecutor), 턴 매니저(TurnManager)에서
 * 발생한 로그를 모아서 UI 쪽으로 넘겨주면, UI는 이를 출력/이펙트 재생 등에 활용할 수 있습니다.
 */
public class BattleLog {

    private final List<BattleLogEntry> entries = new ArrayList<>();

    public void add(String message) {
        if (message != null && !message.isEmpty()) {
            entries.add(new BattleLogEntry(message));
        }
    }

    public void addAll(List<String> messages) {
        if (messages == null) return;
        for (String msg : messages) {
            add(msg);
        }
    }

    public List<BattleLogEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (BattleLogEntry e : entries) {
            sb.append(e.toString()).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
