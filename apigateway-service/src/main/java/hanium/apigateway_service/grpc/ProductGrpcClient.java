package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.mapper.ProductGrpcMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.ProductResponse;
import hanium.common.proto.product.ProductServiceGrpc;
import hanium.common.proto.product.RegisterProductRequest;
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
}
