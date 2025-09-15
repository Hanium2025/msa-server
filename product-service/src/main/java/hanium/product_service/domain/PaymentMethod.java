package hanium.product_service.domain;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    TRANSFER("퀵계좌이체"),
    CARD("신용/체크카드"),
    TOSS("토스페이"),
    PAYCO("페이코"),
    KAKAO("카카오페이"),
    NAVER("네이버페이"),
    MOBILE("휴대폰결제"),
    ;

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }
}
