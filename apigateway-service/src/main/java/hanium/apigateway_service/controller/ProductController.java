package hanium.apigateway_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.dto.product.request.ProductSearchRequestDTO;
import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.ReportProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.apigateway_service.dto.product.response.ProductMainDTO;
import hanium.apigateway_service.dto.product.response.ProductResponseDTO;
import hanium.apigateway_service.dto.product.response.ProductSearchResponseDTO;
import hanium.apigateway_service.dto.product.response.SimpleProductDTO;
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

    // 메인 페이지
    @GetMapping
    public ResponseEntity<ResponseDTO<ProductMainDTO>> getProductMain(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        ProductMainDTO result = productGrpcClient.getProductMain(memberId);
        ResponseDTO<ProductMainDTO> response = new ResponseDTO<>(
                result, HttpStatus.OK, "메인페이지가 조회되었습니다 - 회원 ID: " + memberId);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<ResponseDTO<List<SimpleProductDTO>>> getProductByCategory(
            Authentication authentication,
            @PathVariable String category,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "0") int page
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        ResponseDTO<List<SimpleProductDTO>> result = new ResponseDTO<>(
                productGrpcClient.getProductByCategory(memberId, category.toUpperCase(), sort, page),
                HttpStatus.OK,
                "카테고리 [" + category + "]가 [" + sort + "]순으로 조회되었습니다."
        );
        return ResponseEntity.ok(result);
    }

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
    public ResponseEntity<ResponseDTO<ProductResponseDTO>> getProduct(@PathVariable Long productId,
                                                                      Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        ResponseDTO<ProductResponseDTO> response = new ResponseDTO<>(
                productGrpcClient.getProduct(memberId, productId), HttpStatus.OK, "해당하는 상품이 조회되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 상품 수정
    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<ProductResponseDTO>> updateProductV2(
            @PathVariable Long productId,
            @RequestParam(value = "json") String json,
            @RequestParam(value = "images") List<MultipartFile> images,
            Authentication authentication) throws JsonProcessingException {

        Long memberId = (Long) authentication.getPrincipal();
        UpdateProductRequestDTO dto = objectMapper.readValue(json, UpdateProductRequestDTO.class);
        ProductResponseDTO result = productGrpcClient.updateProduct(memberId, productId, dto, images);
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

    // 상품 찜/찜 취소
    @PostMapping("/like/{productId}")
    public ResponseEntity<?> likeProduct(@PathVariable Long productId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        String message = productGrpcClient.likeProduct(memberId, productId);
        ResponseDTO<?> response = new ResponseDTO<>(null, HttpStatus.OK, message);
        return ResponseEntity.ok(response);
    }

    // 상품 찜 목록 조회
    @GetMapping("/like")
    public ResponseEntity<ResponseDTO<List<SimpleProductDTO>>> getLikeProducts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        List<SimpleProductDTO> result = productGrpcClient.getLikeProducts(memberId, page);
        ResponseDTO<List<SimpleProductDTO>> response = new ResponseDTO<>(
                result, HttpStatus.OK, "상품 찜 목록이 20개씩 조회되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO<ProductSearchResponseDTO>> searchProduct(
            @RequestParam("keyword") String keyword,
            Authentication authentication) {

        Long memberId = (Long) authentication.getPrincipal();

        ProductSearchRequestDTO requestDTO = ProductSearchRequestDTO.builder()
                .keyword(keyword)
                .build();

        ProductSearchResponseDTO result = productGrpcClient.searchProduct(memberId, requestDTO);

        ResponseDTO<ProductSearchResponseDTO> response = new ResponseDTO<>(
                result, HttpStatus.OK, "상품 검색 결과입니다.");

        return ResponseEntity.ok(response);
    }

    // 상품 신고
    @PostMapping("/report/{productId}")
    public ResponseEntity<ResponseDTO<?>> reportProduct(@PathVariable Long productId,
                                                        @RequestBody ReportProductRequestDTO dto,
                                                        Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        productGrpcClient.reportProduct(memberId, productId, dto);
        ResponseDTO<?> response = new ResponseDTO<>(null, HttpStatus.OK, "상품 신고가 접수되었습니다.");
        return ResponseEntity.ok(response);
    }
}