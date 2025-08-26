package hanium.product_service.domain;

import lombok.Getter;

@Getter
public enum Category {
    ELECTRONICS(0, "IT, 전자제품"),
    FURNITURE(1, "가구, 인테리어"),
    CLOTHES(2, "옷, 잡화, 장신구"),
    BOOK(3, "도서, 학습 용품"),
    BEAUTY(4, "헤어, 뷰티, 화장품"),
    FOOD(5, "음식, 식료품"),
    ETC(6, "기타");

    private final int index;
    private final String label;

    Category(int index, String label) {
        this.index = index;
        this.label = label;
    }
}
