package hanium.user_service.controller;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.dto.response.MemberResponseDto;
import hanium.user_service.mapper.MemberMapper;
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
        Member saved = memberService.signup(dto);
        return ResponseEntity.ok(MemberMapper.toMemberResponseDto(saved));
    }

    // 회원 조회 요청
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(@PathVariable Long memberId) {
        Member member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(MemberMapper.toMemberResponseDto(member));
    }
}
