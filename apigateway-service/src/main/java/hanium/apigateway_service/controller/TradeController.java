package hanium.apigateway_service.controller;

import hanium.apigateway_service.grpc.GrpcChatStreamClient;
import hanium.apigateway_service.grpc.TradeGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.product.TradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        try {
            grpcChatStreamClient.sendDirectAccept(chatroomId, memberId, buyerId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FAIL_DIRECT_ACCEPT_CHAT);
        }
        ResponseDTO<Long> response = new ResponseDTO<>(
                buyerId, HttpStatus.OK, "직거래 요청 수락을 성공했습니다.");
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
    //결제 요청
//    public ResponseEntity<ResponseDTO<Long>> requestPayment(@PathVariable Long chatroomId, Authentication authentication){
//        Long memberId = (Long) authentication.getPrincipal();
//        return ResponseEntity.ok();
//    }


}
