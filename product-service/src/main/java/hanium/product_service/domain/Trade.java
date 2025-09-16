package hanium.product_service.domain;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trade extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "product_id")
    private Product product;  //구매자들이 하나의 상품에 구매 요청을 보낼 수 있음

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Enumerated(EnumType.STRING)
    @Column(name="trade_type" ,nullable = false, length = 20)
    private TradeType type;

    @Enumerated(EnumType.STRING)
    @Column(name="trade_status", nullable = false, length = 30)
    private TradeStatus tradeStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chatroom_id", nullable = false, unique = true) //거래당 채팅방 보장
    private Chatroom chatroom;

    public Long getOtherParty(Long memberId) {
        if (buyerId.equals(memberId)) return sellerId;
        if (sellerId.equals(memberId)) return buyerId;
        throw new CustomException(ErrorCode.FORBIDDEN);
    }

}

