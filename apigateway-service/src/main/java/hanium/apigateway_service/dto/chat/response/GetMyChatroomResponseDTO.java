package hanium.apigateway_service.dto.chat.response;

import chatroom.Chatroom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMyChatroomResponseDTO {
    private Long chatroomId;
    private String roomName;
    private LocalDateTime latestTime;
    private String latestMessage;
    private Long productId;
    private Long opponentId; //상대방 아이디

    public static List<GetMyChatroomResponseDTO> from(Chatroom.ListMyChatroomsResponse response) {
        return response.getItemsList().stream()
                .map(i -> GetMyChatroomResponseDTO.builder()
                        .chatroomId(i.getChatroomId())
                        .roomName(i.getRoomName())
                        .latestMessage(i.getLatestMessage())
                        .latestTime( // int64 → LocalDateTime
                                i.getLatestTime() > 0
                                        ? LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(i.getLatestTime()),
                                        ZoneId.systemDefault()
                                )
                                        : null
                        )
                        .productId(i.getProductId())
                        .opponentId(i.getOpponentId())
                        .build())
                .toList();
    }
}
