package hanium.user_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@Table(name = "MEMBER")
// 삭제 시, 삭제 일시를 업데이트하는 쿼리 날림
@SQLDelete(sql = "UPDATE MEMBER SET MEMBER.DELETED_AT = CURRENT_TIMESTAMP WHERE MEMBER.ID = ?")
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity implements UserDetails {
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
    private Profile profile;

    // UserDetails 메서드 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
