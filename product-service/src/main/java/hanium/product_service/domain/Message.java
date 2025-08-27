package hanium.product_service.domain;

import hanium.product_service.dto.request.ChatMessageRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="chatroom_id", nullable=false)
    private Chatroom chatroom;

    @Column
    private Long senderId;

    @Column
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column
    private MessageType messageType;


    public static Message of(Chatroom chatroom, ChatMessageRequestDTO dto) {
       return Message.builder()
                .chatroom(chatroom)
                .content(dto.getContent())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .messageType(dto.getMessageType() == null ? MessageType.TEXT : dto.getMessageType())
                .build();

    }


}
