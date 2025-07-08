package hanium.user_service.controller;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.request.TokenRefreshRequestDTO;
import hanium.user_service.dto.response.LoginResponseDTO;
import hanium.user_service.dto.response.MemberResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.mapper.entity.MemberEntityMapper;
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
    public ResponseEntity<MemberResponseDTO> createMember(@RequestBody SignUpRequestDTO dto) {
        Member saved = authService.signUp(dto);
        return ResponseEntity.ok(MemberEntityMapper.toMemberResponseDto(saved));
    }

    // 로그인 요청
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO loginResponse = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(loginResponse);
    }

    // 토큰 refresh 요청 (새로운 Access 토큰 생성)
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestBody TokenRefreshRequestDTO request) {
        TokenResponseDTO tokenResponseDto = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponseDto);
    }
}
