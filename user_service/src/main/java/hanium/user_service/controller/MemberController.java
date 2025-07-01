package hanium.user_service.controller;

import hanium.user_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/members/")
public class MemberController {

    private final MemberService memberService;
}
