package hanium.product_service.mapper;

import hanium.common.proto.product.*;
import hanium.product_service.dto.response.*;

import java.util.List;
import java.util.stream.Collectors;

public class ProductGrpcMapper {

    public static ProductResponse toProductResponseGrpc(ProductResponseDTO dto) {
        return ProductResponse.newBuilder()
                .setProductId(dto.getProductId())
                .setSellerId(dto.getSellerId())
                .setSellerNickname(dto.getSellerNickname())
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setCategory(dto.getCategory())
                .setStatus(dto.getStatus())
                .setSeller(dto.isSeller())
                .setLiked(dto.isLiked())
                .setLikeCount(dto.getLikeCount())
                .addAllImages(dto.getImages()
                        .stream()
                        .map(ProductGrpcMapper::toProductImageGrpc)
                        .collect(Collectors.toList()))
                .build();
    }

    private static ProductImageResponse toProductImageGrpc(ProductImageDTO dto) {
        return ProductImageResponse.newBuilder()
                .setProductImageId(dto.getId())
                .setImageUrl(dto.getImageUrl())
                .build();
    }

    public static ProductMainResponse toProductMainResponseGrpc(ProductMainDTO dto) {
        return ProductMainResponse.newBuilder()
                .addAllProducts(dto
                        .getProducts()
                        .stream()
                        .map(ProductGrpcMapper::toSimpleProduct)
                        .collect(Collectors.toList()))
                .addAllCategories(dto
                        .getCategories()
                        .stream()
                        .map(ProductGrpcMapper::toCategoryMainGrpc)
                        .collect(Collectors.toList()))
                .build();
    }

    private static SimpleProductResponse toSimpleProduct(SimpleProductDTO dto) {
        return SimpleProductResponse.newBuilder()
                .setProductId(dto.getProductId())
                .setTitle(dto.getTitle())
                .setPrice(dto.getPrice())
                .setImageUrl(dto.getImageUrl())
                .build();
    }

    private static CategoryMain toCategoryMainGrpc(ProductMainDTO.MainCategoriesDTO dto) {
        return CategoryMain.newBuilder()
                .setName(dto.getName())
                .setImageUrl(dto.getImageUrl())
                .build();
    }

    public static LikedProductsResponse toLikedProductsResponse(List<SimpleProductDTO> dto) {
        return LikedProductsResponse.newBuilder()
                .addAllLikedProducts(dto.stream()
                        .map(ProductGrpcMapper::toSimpleProduct)
                        .collect(Collectors.toList()))
                .build();
    }

    public static ProductSearchResponse toProductSearchResponseGrpc(ProductSearchResponseDTO dto) {
        return ProductSearchResponse.newBuilder()
                .addAllProductList(
                        dto.getProductList().stream()
                                .map(ProductGrpcMapper::toSimpleProduct)
                                .collect(Collectors.toList())
                )
                .build();
    }
}