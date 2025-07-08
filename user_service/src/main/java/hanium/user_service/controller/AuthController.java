package hanium.user_service.controller;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.LoginRequestDto;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.dto.request.TokenRefreshRequestDto;
import hanium.user_service.dto.response.LoginResponseDto;
import hanium.user_service.dto.response.MemberResponseDto;
import hanium.user_service.dto.response.TokenResponseDto;
import hanium.user_service.mapper.MemberMapper;
import hanium.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입 요청
    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> createMember(@RequestBody MemberSignupRequestDto dto) {
        Member saved = authService.signUp(dto);
        return ResponseEntity.ok(MemberMapper.toMemberResponseDto(saved));
    }

    // 로그인 요청
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto loginResponse = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(loginResponse);
    }

    // 토큰 refresh 요청 (새로운 Access 토큰 생성)
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody TokenRefreshRequestDto request) {
        TokenResponseDto tokenResponseDto = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponseDto);
    }
}
