package hanium.product_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    
    @GetMapping("/health-check")
    public String status() {
        return "상품 서비스 정상 작동";
    }
}
