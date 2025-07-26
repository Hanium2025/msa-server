package hanium.apigateway_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.apigateway_service.dto.product.response.ProductInfoResponseDTO;
import hanium.apigateway_service.grpc.ProductGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.proto.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@Slf4j
public class ProductController {

    private final ProductGrpcClient productGrpcClient;
    private final ObjectMapper objectMapper;

    // 상품 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<ProductInfoResponseDTO>> registerProduct(
            @RequestParam(value = "json") String json,
            @RequestParam(value = "images") List<MultipartFile> images,
            Authentication authentication
    ) throws JsonProcessingException {

        Long memberId = (Long) authentication.getPrincipal(); // 요청 사용자 id 확인

        ProductResponse grpcResponse = productGrpcClient.registerProduct(
                objectMapper.readValue(json, RegisterProductRequestDTO.class), memberId); // 상품 저장

        List<String> paths = new ArrayList<>();
        if (images.size() > 0) {
            paths = productGrpcClient.getImagePaths(images); // 이미지 저장 (s3 및 DB)
            productGrpcClient.saveImage(grpcResponse.getId(), paths);
        }

        ResponseDTO<ProductInfoResponseDTO> response = new ResponseDTO<>(
                ProductInfoResponseDTO.of(grpcResponse, paths), HttpStatus.OK, "정상적으로 상품이 등록되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 상품 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO<ProductInfoResponseDTO>> getProduct(@PathVariable Long productId) {
        ResponseDTO<ProductInfoResponseDTO> response = new ResponseDTO<>(
                productGrpcClient.getProduct(productId), HttpStatus.OK, "해당하는 상품이 조회되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ResponseDTO<ProductInfoResponseDTO>> updateProduct(@PathVariable Long productId,
                                                                             @RequestBody UpdateProductRequestDTO dto,
                                                                             Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        ProductInfoResponseDTO responseDTO = productGrpcClient.updateProduct(productId, memberId, dto);
        ResponseDTO<ProductInfoResponseDTO> response = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "상품 수정이 완료되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        productGrpcClient.deleteProduct(productId, memberId);
        ResponseDTO<ProductInfoResponseDTO> response = new ResponseDTO<>(
                null, HttpStatus.NO_CONTENT, "상품이 삭제되었습니다."
        );
        return ResponseEntity.ok(response);
    }
}