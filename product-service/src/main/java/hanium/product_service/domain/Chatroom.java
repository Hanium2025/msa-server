package hanium.product_service.domain;

import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name="chatroom",
        uniqueConstraints = @UniqueConstraint(
                name="ux_chatroom_tuple",
                columnNames = {"product_id","sender_id","receiver_id"}
        )
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chatroom extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long productId;
    @Column
    private Long senderId;
    @Column
    private Long receiverId;

    @Column(length = 1000)
    private String latestContent;

    @Column
    private LocalDateTime latestContentTime;

    public static Chatroom from(CreateChatroomRequestDTO dto) {
        return Chatroom.builder()
                .productId(dto.getProductId())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .latestContentTime(LocalDateTime.now())
                .build();
    }

    public void updateLatest(String content, LocalDateTime time) {
        this.latestContent = content;
        this.latestContentTime = time;
    }
}
