package hanium.product_service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompleteTradeInfoDTO {
    private Long tradeId; //거래 아이디
    private Long productId; //상품 아이디
    private Long opponentId; //상대방 아이디

}
