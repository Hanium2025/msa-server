package hanium.apigateway_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.apigateway_service.security.filter.ExceptionHandlerFilter;
import hanium.apigateway_service.security.filter.JwtAuthenticationFilter;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // csrf 사용하지 않음
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/auth/signup", "/user/auth/login", "/user/health-check").permitAll() // 로그인, 회원가입 인증 없이 허용
                        .anyRequest().authenticated())
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(((request, response, authException) -> {
                            // 예외 처리
                            CustomException exception = new CustomException(ErrorCode.NOT_AUTHORIZED);
                            ResponseDTO<Exception> responseDTO = new ResponseDTO<>(exception, HttpStatus.UNAUTHORIZED);
                            response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
                        })))
                .build();
    }
}
