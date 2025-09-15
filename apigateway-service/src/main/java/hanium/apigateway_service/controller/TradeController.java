package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.product.response.TradeReviewPageDTO;
import hanium.apigateway_service.dto.trade.request.TradeReviewRequestDTO;
import hanium.apigateway_service.grpc.GrpcChatStreamClient;
import hanium.apigateway_service.grpc.TradeGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.product.ProductServiceGrpc;
import hanium.common.proto.product.TradeResponse;
import hanium.common.proto.product.TradeStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trade")
@Slf4j
public class TradeController {

    public final TradeGrpcClient tradeGrpcClient;
    public final GrpcChatStreamClient grpcChatStreamClient;

    //직거래 거래 요청
    @PostMapping("/direct-request/chatroom/{chatroomId}")
    public ResponseEntity<ResponseDTO<Long>> requestDirectTrade(@PathVariable Long chatroomId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        TradeResponse tradeResponse = tradeGrpcClient.directTrade(chatroomId, memberId);
        Long sellerId = tradeResponse.getOpponentId();

        //Trade만들고 상대방 아이디 가져오기
        try {
            grpcChatStreamClient.sendDirectRequest(chatroomId, memberId, sellerId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_DIRECT_REQUEST_CHAT);
        }
        ResponseDTO<Long> response = new ResponseDTO<>(
                sellerId, HttpStatus.OK, "직거래 요청을 성공했습니다.");
        return ResponseEntity.ok(response);
    }

    //직거래 거래 수락
    @PostMapping("/direct-accept/chatroom/{chatroomId}")
    public ResponseEntity<ResponseDTO<Long>> acceptDirectTrade(@PathVariable Long chatroomId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        TradeResponse tradeResponse = tradeGrpcClient.acceptDirectTrade(chatroomId, memberId);
        Long buyerId = tradeResponse.getOpponentId();
        log.info("판매자: {}", memberId);
        log.info("구매자: {}", buyerId);
        try {
            grpcChatStreamClient.sendDirectAccept(chatroomId, memberId, buyerId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_DIRECT_ACCEPT_CHAT);
        }
        ResponseDTO<Long> response = new ResponseDTO<>(
                buyerId, HttpStatus.OK, "직거래 요청 수락을 성공했습니다.");
        return ResponseEntity.ok(response);

    }


    //거래 완료버튼을 보여주기 위한 상태 응답 받아오기
    @GetMapping("/status/chatroom/{chatroomId}")
    public ResponseEntity<ResponseDTO<String>> getTradeStatus(@PathVariable Long chatroomId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        final Long memberId;
        try {
            memberId = (Long) authentication.getPrincipal();
        } catch (ClassCastException | NullPointerException e) {
            Object p = (authentication != null ? authentication.getPrincipal() : null);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        log.info("chatroomId={}, memberId={}", chatroomId, memberId);

        TradeStatusResponse status = tradeGrpcClient.getTradeStatus(chatroomId,memberId);
        String tradeStatus = status.getStatus();
        ResponseDTO<String> response = new ResponseDTO<>(
                tradeStatus, HttpStatus.OK, "현재 거래 진행사항 가져오기 성공");
        return ResponseEntity.ok(response);
    }


    //택배 거래 요청
    @PostMapping("/parcel-request/chatroom/{chatroomId}")
    public ResponseEntity<ResponseDTO<Long>> requestParcelTrade(@PathVariable Long chatroomId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        final Long memberId;
        try {
            memberId = (Long) authentication.getPrincipal();
        } catch (ClassCastException | NullPointerException e) {
            Object p = (authentication != null ? authentication.getPrincipal() : null);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        log.info("chatroomId={}, memberId={}", chatroomId, memberId);

        TradeResponse tradeResponse = tradeGrpcClient.parcelTrade(chatroomId, memberId);
        Long sellerId = tradeResponse.getOpponentId();
        //Trade만들고 상대방 아이디 가져오기
        try {
            grpcChatStreamClient.sendParcelRequest(chatroomId, memberId, sellerId);
        } catch (Exception e) {
            log.error("택배거래 요청 실패");
            throw new CustomException(ErrorCode.FAIL_DIRECT_REQUEST_CHAT);
        }
        ResponseDTO<Long> response = new ResponseDTO<>(
                sellerId, HttpStatus.OK, "택배 거래 요청을 성공했습니다.");
        return ResponseEntity.ok(response);
    }


    //택배거래 거래 수락
    @PostMapping("/parcel-accept/chatroom/{chatroomId}")
    public ResponseEntity<ResponseDTO<Long>> acceptParcelTrade(@PathVariable Long chatroomId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        TradeResponse tradeResponse = tradeGrpcClient.acceptParcelTrade(chatroomId, memberId);

        Long buyerId = tradeResponse.getOpponentId();

        try {
            grpcChatStreamClient.sendParcelAccept(chatroomId, memberId, buyerId);
        } catch (Exception e) {
            log.error("");
            throw new CustomException(ErrorCode.FAIL_DIRECT_ACCEPT_CHAT);
        }
        ResponseDTO<Long> response = new ResponseDTO<>(
                buyerId, HttpStatus.OK, "택배 거래 수락을 성공했습니다.");
        return ResponseEntity.ok(response);
    }

    // 거래 평가 패이지
    @GetMapping("/review/{tradeId}")

    public ResponseEntity<ResponseDTO<TradeReviewPageDTO>> requestReviewPage(@PathVariable Long tradeId,
                                                                             Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        TradeReviewPageDTO result = tradeGrpcClient.getTradeReviewPageInfo(tradeId, memberId);
        ResponseDTO<TradeReviewPageDTO> response = new ResponseDTO<>(result, HttpStatus.OK, "거래 평가 페이지 정보입니다.");
        return ResponseEntity.ok(response);
    }

    // 거래 평가
    @PostMapping("/review/{tradeId}")
    public ResponseEntity<ResponseDTO<?>> requestReview(@PathVariable Long tradeId,
                                                        Authentication authentication,
                                                        @RequestBody TradeReviewRequestDTO dto) {
        Long memberId = (Long) authentication.getPrincipal();
        tradeGrpcClient.tradeReview(tradeId, memberId, dto);
        ResponseDTO<?> response = new ResponseDTO<>(null, HttpStatus.OK, "거래 평가가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
    //결제 요청
//    public ResponseEntity<ResponseDTO<Long>> requestPayment(@PathVariable Long chatroomId, Authentication authentication){
//        Long memberId = (Long) authentication.getPrincipal();
//        return ResponseEntity.ok();
//    }


}
