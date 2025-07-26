package hanium.product_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.*;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
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
    public void registerProduct(RegisterProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductResponseDTO dto = productService.registerProduct(RegisterProductRequestDTO.from(request));
            responseObserver.onNext(ProductGrpcMapper.toProductResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (Exception e) {
            CustomException ce = new CustomException(ErrorCode.ERROR_ADD_PRODUCT);
            responseObserver.onError(GrpcUtil.generateException(ce.getErrorCode()));
        }
    }

    // 상품 조회
    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductResponseDTO dto = productService.getProductById(request.getProductId());
            responseObserver.onNext(ProductGrpcMapper.toProductResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 수정
    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductResponseDTO dto = productService.updateProduct(UpdateProductRequestDTO.from(request));
            responseObserver.onNext(ProductGrpcMapper.toProductResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 삭제
    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<Empty> responseObserver) {
        try {
            productService.deleteProductById(request.getProductId(), request.getMemberId());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }
}
