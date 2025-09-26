package hanium.user_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Table(name = "PROFILE")
// 삭제 시, 삭제 일시를 업데이트하는 쿼리 날림
@SQLDelete(sql = "UPDATE PROFILE SET PROFILE.DELETED_AT = CURRENT_TIMESTAMP WHERE PROFILE.ID = ?")
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column
    private String imageUrl;

    @Column
    private Long score;

    @ElementCollection(fetch = FetchType.LAZY)
    List<String> mainCategory;

    @Column(name = "main_category_ttl")
    LocalDateTime mainCategoryTTL;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    public void update(String nickname, String imageUrl) {
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }

    public void updateMainCategory(List<String> mainCategory, LocalDateTime mainCategoryTTL) {
        this.mainCategory = mainCategory;
        this.mainCategoryTTL = mainCategoryTTL;
    }

    public void addScore(Long score) {
        this.score += score;
        if (this.score < 0) {
            this.score = 0L;
        }
    }
}
