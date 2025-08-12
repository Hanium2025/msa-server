package hanium.product_service.domain;

import hanium.product_service.dto.request.ChatMessageRequestDTO;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private Long chatroomId;

    @Column
    private Long senderId;

    @Column
    private Long receiverId;

    public static Message from(ChatMessageRequestDTO dto){
        return Message.builder()
                .chatroomId(dto.getChatroomId())
                .content(dto.getContent())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .build();

}
}
