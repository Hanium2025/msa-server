package hanium.product_service.dto.response;

import hanium.common.proto.product.*;
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
    private List<String> imageUrls; //0~3개의 이미지 URL

    //응답 받은 grpc 응답 값을 dto로 변환
    public static List<ChatMessageResponseDTO> from(GetAllMessagesByChatroomResponse response) {
        return response.getChatResponseMessageList().stream()
                .map(i -> ChatMessageResponseDTO.builder()
                        .messageId(i.getMessageId())
                        .chatroomId(i.getChatroomId())
                        .senderId(i.getSenderId())
                        .receiverId(i.getReceiverId())
                        .content(i.getContent())
                        .timestamp(i.getTimestamp())
                        .type(i.getType().name())
                        .imageUrls(i.getImageUrlsList())
                        .build())
                .toList();
    }

}



