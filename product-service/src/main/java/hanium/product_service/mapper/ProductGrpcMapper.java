package hanium.product_service.mapper;

import hanium.common.proto.product.*;
import hanium.product_service.dto.response.ProductImageDTO;
import hanium.product_service.dto.response.ProductMainDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.dto.response.ProductSearchResponseDTO;

import java.util.stream.Collectors;

public class ProductGrpcMapper {

    public static ProductResponse toProductResponseGrpc(ProductResponseDTO dto) {
        return ProductResponse.newBuilder()
                .setProductId(dto.getProductId())
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setSellerId(dto.getSellerId())
                .setSellerNickname(dto.getSellerNickname())
                .setCategory(dto.getCategory())
                .setStatus(dto.getStatus())
                .setIsSeller(dto.isSeller())
                .addAllImages(dto.getImages().stream().map(
                        ProductGrpcMapper::toProductImageGrpc).collect(Collectors.toList()))
                .build();
    }

    private static ProductImageResponse toProductImageGrpc(ProductImageDTO dto) {
        return ProductImageResponse.newBuilder()
                .setProductImageId(dto.getProductImageId())
                .setImageUrl(dto.getImageUrl())
                .build();
    }

    public static ProductMainResponse toProductMainResponseGrpc(ProductMainDTO dto) {
        return ProductMainResponse.newBuilder()
                .addAllProducts(dto
                        .getProducts()
                        .stream()
                        .map(ProductGrpcMapper::toProductMainGrpc)
                        .collect(Collectors.toList()))
                .addAllCategories(dto
                        .getCategories()
                        .stream()
                        .map(ProductGrpcMapper::toCategoryMainGrpc)
                        .collect(Collectors.toList()))
                .build();
    }

    private static ProductMain toProductMainGrpc(ProductMainDTO.MainProductsDTO dto) {
        return ProductMain.newBuilder()
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

    public static ProductSearchResponse toProductSearchResponseGrpc(ProductSearchResponseDTO dto) {
        return ProductSearchResponse.newBuilder()
                .addAllProductList(
                        dto.getProductList().stream()
                                .map(ProductGrpcMapper::toProductResponseGrpc)
                                .collect(Collectors.toList())
                )
                .build();
    }
}