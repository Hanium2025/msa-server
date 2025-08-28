package hanium.product_service.domain;

import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chatroom extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String roomName;
    @Column
    private Long productId;
    @Column
    private Long senderId;
    @Column
    private Long receiverId;

    @Column(length = 50)
    private String latestContent;

    @Column
    private LocalDateTime latestContentTime;

    @Column
    private String opponentProfileUrl;
    @Column
    private String opponentNickname;

    public static Chatroom from(CreateChatroomRequestDTO dto, String roomName,String opponentProfileUrl, String opponentNickname) {
        return Chatroom.builder()
                .productId(dto.getProductId())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .latestContentTime(LocalDateTime.now())
                .roomName(roomName)
                .opponentProfileUrl(opponentProfileUrl)
                .opponentNickname(opponentNickname)
                .build();
    }

    public void updateLatest(String content, LocalDateTime time) {
        this.latestContent = content;
        this.latestContentTime = time;
    }
}
