package hanium.apigateway_service.security;

import hanium.apigateway_service.security.filter.ExceptionHandlerFilter;
import hanium.apigateway_service.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // csrf 사용하지 않음
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트는 항상 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/error",
                                "/user/auth/signup",
                                "/user/auth/login",
                                "/health/**",  //나머지 서비스 헬스 체크
                                "/health-check", //apigateway 헬스체크
                                "/actuator/health",   // ALB 헬스체크 경로 추가
                                "/user/auth/refresh",
                                "/user/sms/send",
                                "/user/sms/verify",
                                "/health/**",       // 나머지 서비스 헬스 체크
                                "/health-check"     // apigateway 헬스체크
                        ).permitAll() // 로그인, 회원가입 인증 없이 허용
                        .anyRequest().authenticated())
                .addFilterBefore(exceptionHandlerFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
