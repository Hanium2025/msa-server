package hanium.apigateway_service.mapper;


import hanium.apigateway_service.dto.product.RegisterProductRequestDTO;
import hanium.common.proto.product.RegisterProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductGrpcMapperForGateway {

    public RegisterProductRequest toGrpc(RegisterProductRequestDTO dto) {
        return RegisterProductRequest.newBuilder()
                .setTitle(dto.getTitle())
                .setContent(dto.getContent())
                .setPrice(dto.getPrice())
                .setSellerId(Long.parseLong(dto.getSellerId()))
                .setCategory(dto.getCategory()) // enum이면 Enum.valueOf(dto.getCategory())로 수정 필요
                .build();
    }
}
