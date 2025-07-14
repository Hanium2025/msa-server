package hanium.user_service.controller;

import hanium.user_service.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class MemberController {

    @Value("${jwt.secret:NOT_FOUND}")
    private String jwtSecret;

    @Value("${jwt.expiration:-1}")
    private String jwtExpiration;

    @GetMapping("/health-check")
    public String status() {
        return String.format("User Service 설정:"
                + ", jwt secret = %s"
                + ", jwt exp = %s", jwtSecret, jwtExpiration);
    }
}
