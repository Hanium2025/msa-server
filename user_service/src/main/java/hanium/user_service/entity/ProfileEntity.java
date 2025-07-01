package hanium.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Entity @Getter @Setter
@Table(name = "PROFILE")
// 삭제 시, 삭제 일시를 업데이트하는 쿼리 날림
@SQLDelete(sql = "UPDATE PROFILE SET PROFILE.DELETED_AT = CURRENT_TIMESTAMP WHERE PROFILE.ID = ?")
public class ProfileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nickname;

    @Column
    private String imageUrl;
}
