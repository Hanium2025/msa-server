package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.product.RegisterProductRequestDTO;
import hanium.apigateway_service.mapper.ProductGrpcMapperForGateway;
import hanium.common.proto.product.ProductResponse;
import hanium.common.proto.product.ProductServiceGrpc;
import hanium.common.proto.product.RegisterProductRequest;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductGrpcClient {

    @GrpcClient("product-service") // Eureka에 등록된 서비스 이름 (소문자 하이픈 가능)
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    private final ProductGrpcMapperForGateway mapper;

    public ProductResponse registerProduct(RegisterProductRequestDTO dto) {
        RegisterProductRequest grpcRequest = mapper.toGrpc(dto);
        return stub.registerProduct(grpcRequest);
    }
}
