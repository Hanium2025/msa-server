package hanium.apigateway_service.mapper;


import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO;
import hanium.apigateway_service.dto.product.request.UpdateProductRequestDTO2;
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

    public static UpdateProductRequest toUpdateProductGrpc(Long productId, Long memberId,
                                                           UpdateProductRequestDTO dto) {
        return UpdateProductRequest.newBuilder()
                .setMemberId(memberId)
                .setProductId(productId)
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setCategory(dto.getCategory())
                .build();
    }

    public static UpdateProductRequest2 toUpdateProduct2Grpc(Long memberId, Long productId,
                                                             UpdateProductRequestDTO2 dto,
                                                             List<String> s3Paths) {
        return UpdateProductRequest2.newBuilder()
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

    public static DeleteProductRequest toDeleteProductGrpc(Long productId, Long memberId) {
        return DeleteProductRequest.newBuilder()
                .setProductId(productId)
                .setMemberId(memberId)
                .build();
    }
}
