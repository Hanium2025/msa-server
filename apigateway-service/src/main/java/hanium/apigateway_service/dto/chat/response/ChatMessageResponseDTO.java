package hanium.apigateway_service.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChatMessageResponseDTO {

    private Long messageId;      // DB에 저장된 메시지 ID
    private Long chatroomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Long timestamp;     // 서버 기준 createdAt
    private boolean mine;     // 프론트에서 보낸 사용자 입장 기준
    private String type;
    private List<String> imageUrl; //0~3개의 이미지 URL
}
