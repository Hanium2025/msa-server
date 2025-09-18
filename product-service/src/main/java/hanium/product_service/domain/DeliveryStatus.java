package hanium.product_service.domain;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    PREPARING(1, "배송준비중"),
    PICKUP_COMPLETE(2, "집화완료"),
    IN_TRANSIT(3, "배송중"),
    ARRIVED_AT_HUB(4, "지점도착"),
    OUT_FOR_DELIVERY(5, "배송출발"),
    DELIVERED(6, "배송완료");

    private final int level;
    private final String description;

    DeliveryStatus(int level, String description) {
        this.level = level;
        this.description = description;
    }

}
