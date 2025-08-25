package hanium.product_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.*;
import hanium.product_service.dto.request.DeleteImageRequestDTO;
import hanium.product_service.dto.request.ProductSearchRequestDTO;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.request.UpdateProductRequestDTO;
import hanium.product_service.dto.response.ProductMainDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;
import hanium.product_service.mapper.ProductGrpcMapper;
import hanium.product_service.service.ProductLikeService;
import hanium.product_service.service.ProductSearchService;
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
    private final ProductLikeService likeService;
    private final ProductSearchService productSearchService;

    // 메인페이지 조회
    @Override
    public void getProductMain(ProductMainRequest request, StreamObserver<ProductMainResponse> responseObserver) {
        try {
            ProductMainDTO dto = productService.getProductMain(request.getMemberId());
            responseObserver.onNext(ProductGrpcMapper.toProductMainResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

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
            ProductResponseDTO dto = productService.getProductAndViewLog(request.getMemberId(), request.getProductId());
            responseObserver.onNext(ProductGrpcMapper.toProductResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

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

    @Override
    public void deleteImage(DeleteImageRequest request, StreamObserver<DeleteImageResponse> responseObserver) {
        try {
            int leftImageCount = productService.deleteProductImage(DeleteImageRequestDTO.from(request));
            responseObserver.onNext(DeleteImageResponse.newBuilder().setLeftImgCount(leftImageCount).build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 삭제
    @Override
    public void deleteProduct(GetProductRequest request, StreamObserver<Empty> responseObserver) {
        try {
            productService.deleteProductById(request.getProductId(), request.getMemberId());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 찜/찜 취소
    @Override
    public void likeProduct(GetProductRequest request, StreamObserver<LikeProductResponse> responseObserver) {
        try {
            responseObserver.onNext(LikeProductResponse.newBuilder()
                    .setLikeCanceled(
                            likeService.likeProduct(request.getMemberId(), request.getProductId()))
                    .build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 찜 목록 조회
    @Override
    public void getLikeProducts(GetLikedProductsRequest request,
                                StreamObserver<LikedProductsResponse> responseObserver) {
        try {
            responseObserver.onNext(
                    ProductGrpcMapper.toLikedProductsResponse(
                            likeService.getLikedProducts(request.getMemberId(), request.getPage()))
            );
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 검색
    @Override
    public void searchProduct(ProductSearchRequest request, StreamObserver<ProductSearchResponse> responseObserver) {
        try {
            ProductSearchResponseDTO dto = productSearchService.searchProduct(ProductSearchRequestDTO.from(request));
            responseObserver.onNext(ProductGrpcMapper.toProductSearchResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }
}
