package hanium.product_service.domain;

import lombok.Getter;

@Getter
public enum Category {
    TRAVEL(0, "이동·안전장비"),
    FEEDING(1, "식사·수유·위생 가전"),
    SLEEP(2, "수면·안전"),
    PLAY(3, "놀이·교육"),
    LIVING(4, "리빙·가구"),
    APPAREL(5, "의류·잡화"),
    OTHER(6, "기타");

    private final int index;
    private final String label;

    Category(int index, String label) {
        this.index = index;
        this.label = label;
    }
}
