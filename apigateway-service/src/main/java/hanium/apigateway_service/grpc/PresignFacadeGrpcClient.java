package hanium.apigateway_service.grpc;

import chat.Chat;
import chat.ChatServiceGrpc;
import hanium.apigateway_service.dto.chat.response.PresignedUrlDTO;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresignFacadeGrpcClient {

    @GrpcClient("product-service")
    private ChatServiceGrpc.ChatServiceBlockingStub stub;

    public List<PresignedUrlDTO> create(Long chatroomId,int count, String contentType){
        var req = Chat.CreatePresignedUrlsRequest.newBuilder()
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
