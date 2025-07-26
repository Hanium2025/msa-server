package hanium.apigateway_service.mapper;


import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.common.proto.product.DeleteProductRequest;
import hanium.common.proto.product.RegisterProductRequest;
import hanium.common.proto.product.SaveImageRequest;
import hanium.common.proto.product.UpdateProductRequest;

import java.util.List;

public class ProductGrpcMapperForGateway {

    public static RegisterProductRequest toRegisterProductGrpc(RegisterProductRequestDTO dto, Long memberId) {
        return RegisterProductRequest.newBuilder()
                .setSellerId(memberId)
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setCategory(dto.getCategory())
                .build();
    }

    public static SaveImageRequest toSaveImageGrpc(Long productId, List<String> images) {
        return SaveImageRequest.newBuilder()
                .setProductId(productId)
                .addAllImagePath(images)
                .build();
    }

    public static UpdateProductRequest toUpdateProductGrpc(Long productId, Long memberId, UpdateProductRequestDTO dto) {
        return UpdateProductRequest.newBuilder()
                .setMemberId(memberId)
                .setProductId(productId)
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setCategory(dto.getCategory())
                .build();
    }

    public static DeleteProductRequest toDeleteProductGrpc(Long productId, Long memberId) {
        return DeleteProductRequest.newBuilder()
                .setProductId(productId)
                .setMemberId(memberId)
                .build();
    }
}
