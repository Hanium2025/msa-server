package hanium.user_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column
    private LocalDateTime expiresAt;

    @JoinColumn(name = "MEMBER_ID")
    @ManyToOne(fetch = FetchType.EAGER) //TODO: 변경 가능 (매번 Member 같이 가져오는 로직이라 Eager로 설정)
    private Member member;
}
