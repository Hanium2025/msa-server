package hanium.apigateway_service.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@NoArgsConstructor
@AllArgsConstructor
public class CreateChatroomResponseDTO {
    private Long chatroomId;
    private String message; //채팅방 생성 성공 등 응답 메시지
}
