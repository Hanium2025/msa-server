package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.apigateway_service.dto.product.response.ProductInfoResponseDTO;
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
    public ProductResponse registerProduct(RegisterProductRequestDTO dto, Long memberId) {
        RegisterProductRequest grpcRequest = ProductGrpcMapperForGateway.toRegisterProductGrpc(dto, memberId);
        try {
            return stub.registerProduct(grpcRequest);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 이미지 등록
    public void saveImage(Long productId, List<String> images) {
        SaveImageRequest grpcRequest = ProductGrpcMapperForGateway.toSaveImageGrpc(productId, images);
        try {
            stub.saveImage(grpcRequest);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 조회
    public ProductInfoResponseDTO getProduct(Long productId) {
        GetProductRequest grpcRequest = GetProductRequest.newBuilder().setProductId(productId).build();
        try {
            return ProductInfoResponseDTO.from(stub.getProduct(grpcRequest));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 수정
    public ProductInfoResponseDTO updateProduct(Long productId, Long memberId, UpdateProductRequestDTO dto) {
        UpdateProductRequest grpcRequest = ProductGrpcMapperForGateway.toUpdateProductGrpc(productId, memberId, dto);
        try {
            return ProductInfoResponseDTO.from(stub.updateProduct(grpcRequest));
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


    public List<String> getImagePaths(List<MultipartFile> images) {
        if (images.size() > 5) {
            throw new CustomException(ErrorCode.IMAGE_EXCEEDED);
        }
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : images) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            paths.add(s3Upload(file, fileName));
        }
        return paths;
    }

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

    private void s3Delete(String fileName) {
        try {
            s3Template.deleteObject(bucketName, fileName);
        } catch (S3Exception e) {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }
}
