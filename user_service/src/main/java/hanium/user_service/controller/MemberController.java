package hanium.user_service.controller;

import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.dto.response.MemberResponseDto;
import hanium.user_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 회원가입 요청
    @PostMapping
    public ResponseEntity<MemberResponseDto> createMember(@RequestBody MemberSignupRequestDto dto) {
        MemberResponseDto saved = memberService.signup(dto);
        return ResponseEntity.ok(saved);
    }

    // 회원 조회 요청
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable Long memberId) {
        MemberResponseDto member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }
}
