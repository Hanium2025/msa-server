package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.product.response.TradeReviewPageDTO;
import hanium.apigateway_service.dto.trade.request.TradeReviewRequestDTO;
import hanium.apigateway_service.mapper.TradeGrpcMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.*;
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

    // 직거래 요청
    public TradeResponse directTrade(Long chatroomId, Long memberId) {
        TradeRequest request = tradeGrpcMapperForGateway.toTradeRequestGrpc(chatroomId, memberId);
        try {
            return stub.directTrade(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(ErrorCode.CHATROOM_ID_NOT_FOUND);
        }
    }

    // 직거래 수락
    public TradeResponse acceptDirectTrade(Long chatroomId, Long memberId) {
        TradeRequest request = tradeGrpcMapperForGateway.toTradeRequestGrpc(chatroomId, memberId);
        try {
            return stub.acceptDirectTrade(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(ErrorCode.CHATROOM_ID_NOT_FOUND);
        }
    }


    // 택배 거래
    public TradeResponse parcelTrade(Long chatroomId, Long memberId) {
        log.info("택배 거래 요청");
        TradeRequest request = tradeGrpcMapperForGateway.toTradeRequestGrpc(chatroomId, memberId);
        try {
            return stub.parcelTrade(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(ErrorCode.CHATROOM_ID_NOT_FOUND);
        }
    }
    // 택배 거래 수락
    public TradeResponse acceptParcelTrade(Long chatroomId, Long memberId) {
        TradeRequest request = tradeGrpcMapperForGateway.toTradeRequestGrpc(chatroomId, memberId);
        try {
            return stub.acceptParcelTrade(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(ErrorCode.CHATROOM_ID_NOT_FOUND);
        }
    }

    // 거래 리뷰 페이지
    public TradeReviewPageDTO getTradeReviewPageInfo(Long tradeId, Long memberId) {
        GetTradeReviewPageRequest request = tradeGrpcMapperForGateway.toGetTradeReviewPageRequestGrpc(tradeId, memberId);
        try {
            return TradeReviewPageDTO.from(stub.getTradeReviewPageInfo(request));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 거래 리뷰
    public void tradeReview(Long tradeId, Long memberId, TradeReviewRequestDTO dto) {
        TradeReviewRequest request = tradeGrpcMapperForGateway.toTradeReviewRequestGrpc(tradeId, memberId, dto);
        try {
            stub.tradeReview(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

}
