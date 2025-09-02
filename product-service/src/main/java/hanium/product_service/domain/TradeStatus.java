package hanium.product_service.domain;

public enum TradeStatus {
    REQUESTED,   // 구매자가 거래 요청(직거래/택배)
    ACCEPTED,    // 판매자가 수락
    PAID,        // 결제 완료(에스크로 등)
    SHIPPED,     // 발송(택배)
    COMPLETED,   // 거래 완료
    CANCELED;    // 취소

}
