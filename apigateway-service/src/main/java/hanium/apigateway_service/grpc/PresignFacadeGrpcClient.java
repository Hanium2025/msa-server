package hanium.apigateway_service.grpc;

import hanium.common.proto.product.*;
import hanium.apigateway_service.dto.chat.response.PresignedUrlDTO;
import hanium.common.proto.product.ProductServiceGrpc;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresignFacadeGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    public List<PresignedUrlDTO> create(Long chatroomId,int count, String contentType){
        var req = CreatePresignedUrlsRequest.newBuilder()
                .setChatroomId(chatroomId)
                .setCount(count)
                .setContentType(contentType)
                .build();
        var res = stub.createPresignedUrls(req);
        return res.getUrlsList().stream()
                .map(u-> PresignedUrlDTO.builder()
                        .putUrl(u.getPutUrl())
                        .getUrl(u.getGetUrl())
                        .key(u.getKey())
                        .build())
                .toList();
    }
}
