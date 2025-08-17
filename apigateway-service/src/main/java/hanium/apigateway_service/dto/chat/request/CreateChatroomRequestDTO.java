package hanium.apigateway_service.dto.chat.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatroomRequestDTO {
    private Long productId;
    private Long senderId;
    private Long receiverId;
}
