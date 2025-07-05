package hanium.user_service.controller;

import hanium.user_service.dto.MemberSignupRequestDTO;
import hanium.user_service.dto.ResponseMemberDTO;
import hanium.user_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ResponseMemberDTO> createMember(@RequestBody MemberSignupRequestDTO dto) {
        ResponseMemberDTO saved = memberService.createMember(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ResponseMemberDTO> getMemberById(@PathVariable Long memberId) {
        ResponseMemberDTO member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }
}
