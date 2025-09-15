package hanium.product_service.domain;

public enum PaymentStatus {
    ABORTED,
    CANCELED,
    DONE,
    EXPIRED,
    IN_PROGRESS,
    PARTIAL_CANCELED,
    READY,
    WAITING_FOR_DEPOSIT
}