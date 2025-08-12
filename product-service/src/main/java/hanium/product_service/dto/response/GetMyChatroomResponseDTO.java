package hanium.product_service.dto.response;

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

    public static GetMyChatroomResponseDTO from(Chatroom.ChatroomSummary summary) {
        return GetMyChatroomResponseDTO.builder()
                .chatroomId(summary.getChatroomId())
                .roomName(summary.getRoomName())
                .latestMessage(summary.getLatestMessage())
                .latestTime(
                        summary.getLatestTime() > 0
                                ? LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(summary.getLatestTime()),
                                ZoneId.systemDefault()
                        )
                                : null
                )
                .productId(summary.getProductId())
                .opponentId(summary.getOpponentId())
                .build();
    }

}


