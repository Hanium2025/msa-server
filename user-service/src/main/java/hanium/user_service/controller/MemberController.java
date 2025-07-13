package hanium.user_service.controller;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.response.MemberResponseDTO;
import hanium.user_service.mapper.entity.MemberEntityMapper;
import hanium.user_service.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final Environment env;
    private final MemberService memberService;
    @Value("${jwt.secret:NOT_FOUND}")
    private String jwtSecret;

    @Value("${jwt.expiration:-1}")
    private String jwtExpiration;
    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in User Service"
                + ", jwt secret = %s"
                + ", jwt exp = %s", jwtSecret, jwtExpiration);
    }
    // 회원 조회 요청
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDTO> getMemberById(@PathVariable Long memberId) {
        Member member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(MemberEntityMapper.toMemberResponseDto(member));
    }
}
