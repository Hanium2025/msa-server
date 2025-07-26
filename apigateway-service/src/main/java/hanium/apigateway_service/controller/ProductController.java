package hanium.apigateway_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO2;
import hanium.apigateway_service.dto.product.response.ProductResponseDTO;
import hanium.apigateway_service.grpc.ProductGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<ProductResponseDTO>> registerProduct(
            @RequestParam(value = "json") String json,
            @RequestParam(value = "images") List<MultipartFile> images,
            Authentication authentication) throws JsonProcessingException {

        Long memberId = (Long) authentication.getPrincipal(); // 요청 사용자 id 확인

        // s3 이미지 업로드 로직 호출
        List<String> s3Paths = new ArrayList<>();
        if (!images.getFirst().isEmpty()) {
            s3Paths = productGrpcClient.getImagePaths(images);
        }
        // 상품 저장 로직 호출
        ProductResponseDTO result = productGrpcClient.registerProduct(
                memberId, objectMapper.readValue(json, RegisterProductRequestDTO.class), s3Paths);

        ResponseDTO<ProductResponseDTO> response = new ResponseDTO<>(
                result, HttpStatus.OK, "정상적으로 상품이 등록되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 상품 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO<ProductResponseDTO>> getProduct(@PathVariable Long productId) {
        ResponseDTO<ProductResponseDTO> response = new ResponseDTO<>(
                productGrpcClient.getProduct(productId), HttpStatus.OK, "해당하는 상품이 조회되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 상품 수정 TODO: 이미지까지 처리
    @PutMapping("/{productId}")
    public ResponseEntity<ResponseDTO<ProductResponseDTO>> updateProduct(@PathVariable Long productId,
                                                                         @RequestBody UpdateProductRequestDTO dto,
                                                                         Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        ProductResponseDTO result = productGrpcClient.updateProduct(productId, memberId, dto);
        ResponseDTO<ProductResponseDTO> response = new ResponseDTO<>(
                result, HttpStatus.OK, "상품 수정이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 상품 수정 (이미지까지 처리 버전)
    @PutMapping(value = "/{productId}/v2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<ProductResponseDTO>> updateProductV2(
            @PathVariable Long productId,
            @RequestParam(value = "json") String json,
            @RequestParam(value = "images") List<MultipartFile> images,
            Authentication authentication) throws JsonProcessingException {

        Long memberId = (Long) authentication.getPrincipal();
        UpdateProductRequestDTO2 dto = objectMapper.readValue(json, UpdateProductRequestDTO2.class);
        ProductResponseDTO result = productGrpcClient.updateProductV2(memberId, productId, dto, images);
        ResponseDTO<ProductResponseDTO> response = new ResponseDTO<>(
                result, HttpStatus.OK, "상품 수정이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        productGrpcClient.deleteProduct(productId, memberId);
        ResponseDTO<ProductResponseDTO> response = new ResponseDTO<>(
                null, HttpStatus.OK, "상품이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}