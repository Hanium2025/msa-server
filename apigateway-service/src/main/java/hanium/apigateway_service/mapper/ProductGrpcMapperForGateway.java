package hanium.apigateway_service.mapper;


import hanium.apigateway_service.dto.product.request.RegisterProductRequestDTO;
import hanium.common.proto.product.RegisterProductRequest;

public class ProductGrpcMapperForGateway {

    public static RegisterProductRequest toRegisterProductGrpc(RegisterProductRequestDTO dto, Long memberId) {
        return RegisterProductRequest.newBuilder()
                .setSellerId(memberId)
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setCategory(dto.getCategory()) // enum 이면 Enum.valueOf(dto.getCategory())로 수정 필요
                .build();
    }
}
