package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.apigateway_service.dto.product.response.ProductResponseDTO;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductGrpcClient {

    @GrpcClient("product-service") // Eureka에 등록된 서비스 이름 (소문자 하이픈 가능)
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Template s3Template;

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
    public ProductResponseDTO getProduct(Long productId) {
        GetProductRequest grpcRequest = GetProductRequest.newBuilder().setProductId(productId).build();
        try {
            return ProductResponseDTO.from(stub.getProduct(grpcRequest));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 수정
    public ProductResponseDTO updateProduct(Long productId, Long memberId, UpdateProductRequestDTO dto) {
        UpdateProductRequest grpcRequest = ProductGrpcMapperForGateway.toUpdateProductGrpc(productId, memberId, dto);
        try {
            return ProductResponseDTO.from(stub.updateProduct(grpcRequest));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 삭제
    public void deleteProduct(Long productId, Long memberId) {
        DeleteProductRequest grpcRequest = ProductGrpcMapperForGateway.toDeleteProductGrpc(productId, memberId);
        try {
            stub.deleteProduct(grpcRequest);
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
            String fileName = UUID.randomUUID().toString();
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
}
