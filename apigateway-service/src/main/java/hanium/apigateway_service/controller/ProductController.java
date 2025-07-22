package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.response.ProductInfoResponseDTO;
import hanium.apigateway_service.grpc.ProductGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductGrpcClient productGrpcClient;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<ProductInfoResponseDTO>> registerProduct(@RequestBody RegisterProductRequestDTO requestDTO) {
        ProductInfoResponseDTO responseDTO = ProductInfoResponseDTO.from(productGrpcClient.registerProduct(requestDTO));
        ResponseDTO<ProductInfoResponseDTO> response = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "정상적으로 상품이 등록되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}