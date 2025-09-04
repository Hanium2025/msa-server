package hanium.apigateway_service.controller;

import hanium.apigateway_service.grpc.TradeGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
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
public class TradeController {

    public final TradeGrpcClient tradeGrpcClient;

    //직거래 거래 요청
    @PostMapping("/direct-request/chatroom/{chatroomId}")
    public ResponseEntity<?> requestDirectTrade(@PathVariable Long chatroomId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        tradeGrpcClient.DirectTrade(chatroomId, memberId);
        ResponseDTO<Void> response = new ResponseDTO<>(
                null, HttpStatus.OK, "직거래 요청을 성공했습니다.");
        return ResponseEntity.ok(response);
    }


    //택배 거래 요청
    @PostMapping("/parcel-request/chatroom/{chatroomId}")
    public ResponseEntity<?> requestParcelTrade(@PathVariable Long chatroomId, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        tradeGrpcClient.ParcelTrade(chatroomId, memberId);
        ResponseDTO<Void> response = new ResponseDTO<>(
                null, HttpStatus.OK, "택배 거래 요청을 성공했습니다.");
        return ResponseEntity.ok(response);
    }

}
