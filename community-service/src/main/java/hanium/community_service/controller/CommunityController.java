package hanium.community_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class CommunityController {
    @GetMapping("/health-check")
    public String status() {
        return "커뮤니티 서비스 정상 작동";
    }

}
