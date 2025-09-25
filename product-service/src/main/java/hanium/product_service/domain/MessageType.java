package hanium.product_service.domain;

public enum MessageType {
    TEXT,IMAGE,
    //거래 관련 메시지
    DIRECT_REQUEST, //직거래 요청
    DIRECT_ACCEPT,  //직거래 수락
    PARCEL_REQUEST, //택배거래 요청
    PARCEL_ACCEPT, //택배거래 수락
    PAYMENT_REQUEST, //결제 요청
    PAYMENT_DONE, //결제 완료
    ADDRESS_REGISTER, //배송지 등록
    ADDRESS_REGISTER_DONE,
    TRADE_COMPLETE,
}
