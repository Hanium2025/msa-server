package hanium.product_service.domain;

import lombok.Getter;

@Getter
public enum Category {
    ELECTRONICS(0),
    FURNITURE(1),
    CLOTHES(2),
    BOOK(3),
    BEAUTY(4),
    FOOD(5),
    ETC(6);

    private final int index;

    Category(int index) {
        this.index = index;
    }
}
