package hanium.product_service.domain;

import lombok.Getter;

@Getter
public enum Status {
    SELLING("판매 중"),
    IN_PROGRESS("거래 중"),
    SOLD_OUT("판매 완료");

    private final String label;

    Status(String label) {
        this.label = label;
    }
}
