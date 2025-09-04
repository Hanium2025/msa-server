package hanium.apigateway_service.grpc;

import hanium.apigateway_service.mapper.TradeGrpcMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.ProductServiceGrpc;
import hanium.common.proto.product.TradeRequest;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub stub;
    private final TradeGrpcMapperForGateway tradeGrpcMapperForGateway;

    // 직거래
    public void DirectTrade(Long chatroomId, Long memberId) {
        TradeRequest request = tradeGrpcMapperForGateway.toTradeRequestGrpc(chatroomId, memberId);
        try {
            stub.directTrade(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(ErrorCode.CHATROOM_ID_NOT_FOUND);
        }
    }

    // 택배 거래
    public void ParcelTrade(Long chatroomId, Long memberId) {
        TradeRequest request = tradeGrpcMapperForGateway.toTradeRequestGrpc(chatroomId, memberId);
        try {
            stub.parcelTrade(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(ErrorCode.CHATROOM_ID_NOT_FOUND);
        }
    }

}
