package hanium.apigateway_service.mapper;


import hanium.apigateway_service.dto.product.request.ProductSearchRequestDTO;
import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.common.proto.product.*;

import java.util.List;

public class ProductGrpcMapperForGateway {

    public static RegisterProductRequest toRegisterProductGrpc(Long memberId,
                                                               RegisterProductRequestDTO dto,
                                                               List<String> s3Paths) {
        return RegisterProductRequest.newBuilder()
                .setSellerId(memberId)
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setCategory(dto.getCategory())
                .addAllImageUrls(s3Paths)
                .build();
    }

    public static UpdateProductRequest toUpdateProductGrpc(Long memberId, Long productId,
                                                           UpdateProductRequestDTO dto,
                                                           List<String> s3Paths) {
        return UpdateProductRequest.newBuilder()
                .setMemberId(memberId)
                .setProductId(productId)
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setCategory(dto.getCategory())
                .addAllImageUrls(s3Paths)
                .build();
    }

    public static DeleteImageRequest toDeleteImageGrpc(Long memberId, Long productId,
                                                       List<Long> leftImageIds) {
        return DeleteImageRequest.newBuilder()
                .setMemberId(memberId)
                .setProductId(productId)
                .addAllLeftImageIds(leftImageIds)
                .build();
    }

    public static GetProductRequest toGetProductGrpc(Long productId, Long memberId) {
        return GetProductRequest.newBuilder()
                .setProductId(productId)
                .setMemberId(memberId)
                .build();
    }

    public static ProductSearchRequest toSearchProductGrpc(Long memberId, ProductSearchRequestDTO dto) {
        return ProductSearchRequest.newBuilder()
                .setMemberId(memberId)
                .setKeyword(dto.getKeyword())
                .build();
    }

    public static ProductSearchHistoryRequest toSearchProductHistoryGrpc(Long memberId) {
        return ProductSearchHistoryRequest.newBuilder()
                .setMemberId(memberId)
                .build();
    }

    public static DeleteProductSearchHistoryRequest toDeleteProductSearchHistoryGrpc(Long searchId, Long memberId) {
        return DeleteProductSearchHistoryRequest.newBuilder()
                .setSearchId(searchId)
                .setMemberId(memberId)
                .build();
    }

    public static DeleteAllProductSearchHistoryRequest toDeleteAllProductSearchHistoryGrpc(Long memberId) {
        return DeleteAllProductSearchHistoryRequest.newBuilder()
                .setMemberId(memberId)
                .build();
    }

    public static GetProductByCategoryRequest toGetProductByCategoryGrpc(Long memberId, String category,
                                                                         String sort, int page) {
        return GetProductByCategoryRequest.newBuilder()
                .setMemberId(memberId)
                .setCategory(category)
                .setSort(sort)
                .setPage(page)
                .build();
    }
}
