package hanium.product_service.domain;

import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import jakarta.persistence.*;
import lombok.*;

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

    public static Chatroom from(CreateChatroomRequestDTO dto, String roomName) {
        return Chatroom.builder()
                .productId(dto.getProductId())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .roomName(roomName)
                .build();
    }
}
