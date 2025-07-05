package hanium.user_service.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class GrpcAuthenticationToken extends UsernamePasswordAuthenticationToken {
    
    public GrpcAuthenticationToken(Object principal,
                                   Object credentials,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        // 수동 호출 방지
        throw new IllegalCallerException("setAuthenticated()는 생성자를 통해 설정하세요, 호출 금지");
    }
}
