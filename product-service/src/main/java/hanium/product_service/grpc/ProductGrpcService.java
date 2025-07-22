package hanium.product_service.grpc;

import hanium.common.proto.product.ProductServiceGrpc;
import hanium.common.proto.product.RegisterProductRequest;
import hanium.common.proto.product.ProductResponse;

import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;
import hanium.product_service.mapper.ProductMapper;
import hanium.product_service.service.ProductService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Override
    public void registerProduct(RegisterProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        // gRPC → DTO
        RegisterProductRequestDTO dto = productMapper.toDto(request);

        // 비즈니스 로직 처리
        ProductInfoResponseDTO result = productService.registerProduct(dto);

        // DTO → gRPC
        ProductResponse response = productMapper.toGrpc(result);

        // 응답 반환
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
