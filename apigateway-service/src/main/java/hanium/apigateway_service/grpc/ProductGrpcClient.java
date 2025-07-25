package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.apigateway_service.dto.product.response.ProductInfoResponseDTO;
import hanium.apigateway_service.mapper.ProductGrpcMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.*;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductGrpcClient {

    @GrpcClient("product-service") // Eureka에 등록된 서비스 이름 (소문자 하이픈 가능)
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    // 상품 등록
    public ProductResponse registerProduct(RegisterProductRequestDTO dto, Long memberId) {
        RegisterProductRequest grpcRequest = ProductGrpcMapperForGateway.toRegisterProductGrpc(dto, memberId);
        try {
            return stub.registerProduct(grpcRequest);
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
}
