package hanium.product_service.mapper;

import hanium.common.proto.product.RegisterProductRequest;
import hanium.common.proto.product.ProductResponse;
import hanium.product_service.domain.Product;
import hanium.product_service.dto.request.RegisterProductRequestDTO;
import hanium.product_service.dto.response.ProductInfoResponseDTO;
import hanium.product_service.enums.Category;
import hanium.product_service.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductMapper {

    public Product toEntity(RegisterProductRequestDTO dto) {
        return Product.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .sellerId(dto.getSellerId())
                .category(dto.getCategory())
                .status(Status.SELLING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ProductInfoResponseDTO toDto(Product product) {
        return ProductInfoResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .content(product.getContent())
                .price(product.getPrice())
                .sellerId(product.getSellerId())
                .category(product.getCategory())
                .status(product.getStatus())
                .build();
    }

    // ✅ gRPC → DTO
    public RegisterProductRequestDTO toDto(RegisterProductRequest request) {
        return RegisterProductRequestDTO.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .sellerId(request.getSellerId())
                .category(Category.valueOf(request.getCategory()))
                .build();
    }

    // ✅ DTO → gRPC
    public ProductResponse toGrpc(ProductInfoResponseDTO dto) {
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
