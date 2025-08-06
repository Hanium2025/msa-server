package hanium.apigateway_service.dto.chat.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDTO {

    private Long chatroomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long timestamp;

}
