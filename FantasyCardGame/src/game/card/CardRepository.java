package game.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 카드 데이터를 관리하는 저장소 클래스입니다.
 *
 * - ALL_CARDS 리스트에 모든 카드 정보를 보관합니다.
 * - findById, findByName 등 조회용 유틸 메서드를 제공합니다.
 * - createShuffledDeck 을 통해 섞인 덱을 만들 수 있습니다.
 *
 * 이 클래스의 코드는 "데이터 + 도우미 메서드" 위주이기 때문에
 * 라인 수를 채우기에 좋고, 팀원들도 쉽게 읽고 수정할 수 있습니다.
 */
public class CardRepository {

    /**
     * 게임에 존재하는 모든 카드 목록입니다.
     */
    private static final List<card> ALL_CARDS = new ArrayList<>();

    // 정적 초기화 블록에서 카드 데이터를 등록합니다.
    static {
        // ---- 기본 카드들 -----------------------------------------------------

        ALL_CARDS.add(card.createNormal(
                "MON_001",
                "돌거북",
                3,
                2,
                3,
                "공격 2, 방어 3의 단단한 돌거북 몬스터입니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_002",
                "불꽃늑대",
                4,
                4,
                2,
                "화염의 기운을 두른 공격적인 늑대 몬스터입니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_003",
                "숲의 수호자",
                5,
                3,
                5,
                "숲을 지키는 수호자 몬스터로, 방어에 강합니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_004",
                "고블린 전사",
                2,
                2,
                1,
                "값싼 공격 요원으로 활용할 수 있는 고블린입니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_005",
                "고블린 방패병",
                2,
                1,
                3,
                "저코스트 수비용 고블린 몬스터입니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_006",
                "얼음 골렘",
                6,
                4,
                7,
                "얼음으로 만들어진 골렘으로, 높은 체력과 방어력을 자랑합니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_007",
                "화염 정령",
                5,
                6,
                1,
                "매우 높은 공격력을 가진 불의 정령입니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_008",
                "바람의 기사",
                4,
                3,
                3,
                "공격과 방어가 균형 잡힌 바람 속성 기사입니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_009",
                "대지의 수호병",
                5,
                2,
                6,
                "대지의 힘으로 방어에 특화된 몬스터입니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_010",
                "빛의 성직자",
                3,
                1,
                4,
                "방어 위주의 빛 속성 몬스터입니다."
        ));

        // ---- 특수 효과를 가진 카드들 ----------------------------------------

        // 도발 탱커
        ALL_CARDS.add(card.createWithEffects(
                "MON_011",
                "도발의 방패병",
                4,
                2,
                6,
                List.of(
                        new cardEffect(EffectType.TAUNT, 0)
                ),
                "도발을 가진 탱커 몬스터로, 상대의 공격을 자신에게 끌어옵니다."
        ));

        // 플레이어를 직접 공격하는 카드
        ALL_CARDS.add(card.createWithEffects(
                "MON_012",
                "심장파괴자",
                6,
                5,
                2,
                List.of(
                        new cardEffect(EffectType.DIRECT_ATTACK_PLAYER, 5)
                ),
                "적 플레이어에게 직접 5의 피해를 줄 수 있는 몬스터입니다."
        ));

        // 도발 + 플레이어 직통 공격을 모두 가진 미니 보스 카드
        ALL_CARDS.add(card.createWithEffects(
                "MON_013",
                "분노한 용병대장",
                7,
                6,
                4,
                List.of(
                        new cardEffect(EffectType.TAUNT, 0),
                        new cardEffect(EffectType.DIRECT_ATTACK_PLAYER, 3)
                ),
                "도발과 플레이어 직통 공격을 모두 가진 강력한 몬스터입니다."
        ));

        // 공격 관련 추가 예시 카드
        ALL_CARDS.add(card.createWithEffects(
                "MON_014",
                "암살단 첨병",
                4,
                4,
                1,
                List.of(
                        new cardEffect(EffectType.DAMAGE_TO_MONSTER, 2)
                ),
                "등장 시 적 몬스터에게 추가 피해를 줄 수 있는 암살형 몬스터입니다."
        ));

        // 방어/지원 관련 예시 카드
        ALL_CARDS.add(card.createWithEffects(
                "MON_015",
                "수호의 기사",
                5,
                3,
                5,
                List.of(
                        new cardEffect(EffectType.BUFF_DEFENSE, 2)
                ),
                "아군 방어를 강화하는 수호 기사입니다."
        ));

        // 조금 더 강한 몬스터들
        ALL_CARDS.add(card.createNormal(
                "MON_016",
                "용의 분노",
                8,
                8,
                4,
                "높은 코스트를 요구하지만 막강한 공격력을 가진 드래곤 몬스터입니다."
        ));

        ALL_CARDS.add(card.createNormal(
                "MON_017",
                "어둠의 군주",
                9,
                9,
                5,
                "공격과 방어 모두 뛰어난 어둠 속성 군주입니다."
        ));

        ALL_CARDS.add(card.createWithEffects(
                "MON_018",
                "불멸의 수호자",
                7,
                3,
                9,
                List.of(
                        new cardEffect(EffectType.BUFF_DEFENSE, 3)
                ),
                "방어력이 매우 높고, 방어 버프를 부여하는 수호자입니다."
        ));

        ALL_CARDS.add(card.createWithEffects(
                "MON_019",
                "광기의 마도사",
                6,
                6,
                2,
                List.of(
                        new cardEffect(EffectType.DIRECT_ATTACK_PLAYER, 4)
                ),
                "적 플레이어를 집요하게 노리는 광기의 마법사 몬스터입니다."
        ));

        ALL_CARDS.add(card.createWithEffects(
                "MON_020",
                "지배자 크라운",
                10,
                10,
                8,
                List.of(
                        new cardEffect(EffectType.TAUNT, 0),
                        new cardEffect(EffectType.DIRECT_ATTACK_PLAYER, 6)
                ),
                "최고 코스트의 보스 몬스터로, 도발과 강력한 직통 공격을 모두 보유합니다."
        ));
    }

    // ---- 조회용 메서드들 ----------------------------------------------------

    /**
     * 등록된 모든 카드를 새로운 리스트로 반환합니다.
     * 반환되는 리스트를 수정해도 내부 데이터에는 영향이 없습니다.
     */
    public static List<card> getAllCards() {
        return new ArrayList<>(ALL_CARDS);
    }

    /**
     * 카드 ID 로 카드를 찾습니다.
     * 없으면 null 을 반환합니다.
     */
    public static card findById(String id) {
        if (id == null) {
            return null;
        }
        for (card c : ALL_CARDS) {
            if (id.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    /**
     * 카드 이름으로 카드를 찾습니다.
     * 이름이 같은 카드가 여러 개라면 첫 번째 결과만 반환합니다.
     */
    public static card findByName(String name) {
        if (name == null) {
            return null;
        }
        for (card c : ALL_CARDS) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    /**
     * 특정 코스트 값을 가진 카드 목록을 반환합니다.
     */
    public static List<card> findByCost(int cost) {
        List<card> result = new ArrayList<>();
        for (card c : ALL_CARDS) {
            if (c.getCost() == cost) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * 코스트가 주어진 값 이하인 카드들을 모두 반환합니다.
     */
    public static List<card> findByCostLessOrEqual(int maxCost) {
        List<card> result = new ArrayList<>();
        for (card c : ALL_CARDS) {
            if (c.getCost() <= maxCost) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * 단순하게 모든 카드를 섞어서 반환합니다.
     */
    public static List<card> createShuffledDeck() {
        List<card> copy = new ArrayList<>(ALL_CARDS);
        Collections.shuffle(copy);
        return copy;
    }

    /**
     * 원하는 크기의 덱을 생성합니다.
     * 전체 카드 수보다 큰 값이 들어오면 전체 카드 수로 조정합니다.
     */
    public static List<card> createShuffledDeck(int deckSize) {
        List<card> copy = new ArrayList<>(ALL_CARDS);
        Collections.shuffle(copy);
        if (deckSize > copy.size()) {
            deckSize = copy.size();
        }
        return new ArrayList<>(copy.subList(0, deckSize));
    }

    /**
     * 디버깅 또는 테스트용으로,
     * 모든 카드의 간단한 정보를 콘솔에 출력합니다.
     */
    public static void printAllCards() {
        System.out.println("===== 등록된 카드 목록 =====");
        for (card c : ALL_CARDS) {
            System.out.println(c.toSimpleString());
        }
        System.out.println("===========================");
    }
}

