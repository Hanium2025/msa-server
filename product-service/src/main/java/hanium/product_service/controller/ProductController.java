package hanium.product_service.controller;

import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;
import hanium.product_service.mapper.ProductMapper;
import hanium.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping("/register")
    public ProductInfoResponseDTO registerProduct(@RequestBody RegisterProductRequestDTO requestDto) {
        return productService.registerProduct(requestDto);
    }

    @GetMapping("/health-check")
    public String status() {
        return "상품 서비스 정상 작동";
    }
}
