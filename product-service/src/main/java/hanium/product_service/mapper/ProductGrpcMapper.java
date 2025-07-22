package hanium.product_service.mapper;

import hanium.common.proto.product.ProductResponse;
import hanium.product_service.dto.response.ProductInfoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductGrpcMapper {

    // DTO → gRPC
    public ProductResponse toProductResponseGrpc(ProductInfoResponseDTO dto) {
        return ProductResponse.newBuilder()
                .setId(dto.getId())
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setSellerId(dto.getSellerId())
                .setCategory(dto.getCategory().name())
                .setStatus(dto.getStatus().name())
                .build();
    }
}
