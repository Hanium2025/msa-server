package hanium.product_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageImage extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="message_id")
    @Setter // 편의 메서드에서 세팅
    private Message message;

    // 실제 이미지 URL (S3 등 외부 저장소 경로를 저장하는 용도)
    @Column(length = 1000)
    private String imageUrl;

    public static MessageImage of(Message message, String imageUrl){
        return MessageImage.builder()
                .message(message)
                .imageUrl(imageUrl)
                .build();

    }
}
