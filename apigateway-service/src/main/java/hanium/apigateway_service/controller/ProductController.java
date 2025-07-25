package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.response.ProductInfoResponseDTO;
import hanium.apigateway_service.grpc.ProductGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductGrpcClient productGrpcClient;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<ProductInfoResponseDTO>> registerProduct(@RequestBody RegisterProductRequestDTO requestDTO,
                                                                               Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal(); // 요청 사용자 id 확인
        ProductInfoResponseDTO responseDTO = ProductInfoResponseDTO.from(
                productGrpcClient.registerProduct(requestDTO, memberId));
        ResponseDTO<ProductInfoResponseDTO> response = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "정상적으로 상품이 등록되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO<ProductInfoResponseDTO>> getProduct(@PathVariable Long productId) {
        ResponseDTO<ProductInfoResponseDTO> response = new ResponseDTO<>(
                productGrpcClient.getProduct(productId), HttpStatus.OK, "해당하는 상품이 조회되었습니다."
        );
        return ResponseEntity.ok(response);
    }
}