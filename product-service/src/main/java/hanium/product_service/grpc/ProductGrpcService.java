package hanium.product_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.GetProductRequest;
import hanium.common.proto.product.ProductResponse;
import hanium.common.proto.product.ProductServiceGrpc;
import hanium.common.proto.product.RegisterProductRequest;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;
import hanium.product_service.mapper.ProductGrpcMapper;
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

    // 상품 등록
    @Override
    public void registerProduct(RegisterProductRequest request,
                                StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductInfoResponseDTO responseDTO = productService
                    .registerProduct(RegisterProductRequestDTO.from(request));
            ProductResponse response = ProductGrpcMapper.toProductResponseGrpc(responseDTO);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            CustomException ce = new CustomException(ErrorCode.ERROR_ADD_PRODUCT);
            responseObserver.onError(GrpcUtil.generateException(ce.getErrorCode()));
        }
    }

    // 상품 조회
    @Override
    public void getProduct(GetProductRequest request,
                           StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductInfoResponseDTO dto = productService.getProductById(request.getProductId());
            responseObserver.onNext(ProductGrpcMapper.toProductResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }
}
