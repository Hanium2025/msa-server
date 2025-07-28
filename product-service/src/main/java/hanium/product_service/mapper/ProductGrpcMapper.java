package hanium.product_service.mapper;

import hanium.common.proto.product.ProductImageResponse;
import hanium.common.proto.product.ProductResponse;
import hanium.product_service.dto.response.ProductImageDTO;
import hanium.product_service.dto.response.ProductResponseDTO;

import java.util.stream.Collectors;

public class ProductGrpcMapper {

    public static ProductResponse toProductResponseGrpc(ProductResponseDTO dto) {
        return ProductResponse.newBuilder()
                .setProductId(dto.getId())
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setSellerId(dto.getSellerId())
                .setCategory(dto.getCategory().name())
                .setStatus(dto.getStatus().name())
                .addAllImages(dto.getImages().stream().map(
                        ProductGrpcMapper::toProductImageGrpc).collect(Collectors.toList()))
                .build();
    }

    public static ProductImageResponse toProductImageGrpc(ProductImageDTO dto) {
        return ProductImageResponse.newBuilder()
                .setProductImageId(dto.getProductImageId())
                .setImageUrl(dto.getImageUrl())
                .build();
    }
}