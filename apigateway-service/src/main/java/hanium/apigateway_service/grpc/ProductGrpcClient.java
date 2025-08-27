package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.product.request.ProductSearchRequestDTO;
import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.apigateway_service.dto.product.response.ProductMainDTO;
import hanium.apigateway_service.dto.product.response.ProductResponseDTO;
import hanium.apigateway_service.dto.product.response.ProductSearchResponseDTO;
import hanium.apigateway_service.dto.product.response.SimpleProductDTO;
import hanium.apigateway_service.mapper.ProductGrpcMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.*;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductGrpcClient {

    @GrpcClient("product-service") // Eureka에 등록된 서비스 이름 (소문자 하이픈 가능)
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Template s3Template;

    // 메인 페이지
    public ProductMainDTO getProductMain(Long memberId) {
        ProductMainRequest request = ProductMainRequest.newBuilder().setMemberId(memberId).build();
        try {
            return ProductMainDTO.from(stub.getProductMain(request));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 카테고리별 보기
    public List<SimpleProductDTO> getProductByCategory(Long memberId, String category,
                                                       String sort, int page) {
        GetProductByCategoryRequest req =
                ProductGrpcMapperForGateway.toGetProductByCategoryGrpc(memberId, category, sort, page);
        try {
            return stub.getProductByCategory(req).getProductsList()
                    .stream()
                    .map(SimpleProductDTO::from)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 등록
    public ProductResponseDTO registerProduct(Long memberId,
                                              RegisterProductRequestDTO dto,
                                              List<String> s3Paths) {
        RegisterProductRequest grpcRequest =
                ProductGrpcMapperForGateway.toRegisterProductGrpc(memberId, dto, s3Paths);
        try {
            return ProductResponseDTO.from(stub.registerProduct(grpcRequest));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 조회
    public ProductResponseDTO getProduct(Long memberId, Long productId) {
        GetProductRequest grpcRequest = ProductGrpcMapperForGateway.toGetProductGrpc(productId, memberId);
        try {
            return ProductResponseDTO.from(stub.getProduct(grpcRequest));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 수정
    public ProductResponseDTO updateProduct(Long memberId, Long productId,
                                            UpdateProductRequestDTO dto, List<MultipartFile> images) {
        // 삭제된 이미지가 있는지 확인, 비교하고 삭제 처리
        DeleteImageRequest deleteRequest =
                ProductGrpcMapperForGateway.toDeleteImageGrpc(memberId, productId, dto.getLeftImageIds());
        try {
            int leftImageCount = stub.deleteImage(deleteRequest).getLeftImgCount();
            // 새로 추가된 이미지 있는가?
            List<String> s3Paths = new ArrayList<>();
            if (!images.getFirst().isEmpty()) {
                // 기존 이미지와 새로 추가된 이미지 포함 5장 초과면 예외 발생
                if (images.size() + leftImageCount > 5) {
                    throw new CustomException(ErrorCode.IMAGE_EXCEEDED);
                }
                s3Paths = getImagePaths(images);
            }
            // 새로 추가된 이미지와 수정할 상품 dto로 상품 수정
            UpdateProductRequest updateRequest =
                    ProductGrpcMapperForGateway.toUpdateProductGrpc(memberId, productId, dto, s3Paths);
            return ProductResponseDTO.from(stub.updateProduct(updateRequest));

        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 삭제
    public void deleteProduct(Long productId, Long memberId) {
        GetProductRequest grpcRequest = ProductGrpcMapperForGateway.toGetProductGrpc(productId, memberId);
        try {
            stub.deleteProduct(grpcRequest);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 찜/찜 취소
    public String likeProduct(Long memberId, Long productId) {
        GetProductRequest grpcRequest = ProductGrpcMapperForGateway.toGetProductGrpc(productId, memberId);
        try {
            if (stub.likeProduct(grpcRequest).getLikeCanceled()) {
                return "상품 (id=" + productId + ") 찜이 취소되었습니다.";
            } else {
                return "상품 (id=" + productId + ") 찜이 등록되었습니다.";
            }
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 찜 목록 조회
    public List<SimpleProductDTO> getLikeProducts(Long memberId, int page) {
        GetLikedProductsRequest grpcRequest =
                GetLikedProductsRequest.newBuilder().setMemberId(memberId).setPage(page).build();
        try {
            return stub.getLikeProducts(grpcRequest).getLikedProductsList()
                    .stream()
                    .map(SimpleProductDTO::from)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 요청의 images 리스트 파일들을 s3에 저장하고 해당 경로 리스트 반환
    public List<String> getImagePaths(List<MultipartFile> images) {
        if (images.size() > 5) {
            throw new CustomException(ErrorCode.IMAGE_EXCEEDED);
        }
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : images) {
            String fileName = "product_image/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            paths.add(s3Upload(file, fileName));
        }
        return paths;
    }

    // 특정 파일을 s3에 업로드
    private String s3Upload(MultipartFile file, String fileName) {
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.BLANK_IMAGE);
        }
        try (InputStream is = file.getInputStream()) {
            S3Resource upload = s3Template.upload(bucketName, fileName, is);
            return upload.getURL().toString();
        } catch (IOException | S3Exception e) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_ERROR);
        }
    }

    // 특정 파일을 s3에서 삭제
    private void s3Delete(String fileName) {
        try {
            s3Template.deleteObject(bucketName, fileName);
        } catch (S3Exception e) {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }

    // 상품 검색
    public ProductSearchResponseDTO searchProduct(Long memberId, ProductSearchRequestDTO dto) {
        ProductSearchRequest grpcRequest =
                ProductGrpcMapperForGateway.toSearchProductGrpc(memberId, dto);
        try {
            return ProductSearchResponseDTO.from(stub.searchProduct(grpcRequest));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }
}
