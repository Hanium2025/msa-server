package hanium.apigateway_service.dto.chat.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatroomRequestDTO {
    @NotNull(message = "productId는 필수입니다.")
    private Long productId;
    @NotNull(message = "receiverId는 필수입니다.")
    private Long receiverId;
}
