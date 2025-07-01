package hanium.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;


@Entity @Getter @Setter
@Table(name = "MEMBER")
// 삭제 시, 삭제 일시를 업데이트하는 쿼리 날림
@SQLDelete(sql = "UPDATE MEMBER SET MEMBER.DELETED_AT = CURRENT_TIMESTAMP WHERE MEMBER.ID = ?")
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String phoneNumber;

    @Enumerated(value = EnumType.STRING)
    private Provider provider;
    @Column
    private String providerUserId;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column
    private boolean isAgreeMarketing;
    @Column
    private boolean isAgreeThirdParty;

    @OneToOne
    @JoinColumn(name = "PROFILE_ID")
    private ProfileEntity profile;
}
