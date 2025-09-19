package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.product.request.ConfirmPaymentRequestDTO;
import hanium.apigateway_service.dto.product.request.SavePayInfoRequestDTO;
import hanium.apigateway_service.grpc.ProductGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.exception.CustomException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hanium.common.exception.ErrorCode.INVALID_PRICE_INFO;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final ProductGrpcClient productGrpcClient;

    // 결제 요청 전 데이터 무결성을 확인하기 위한 저장
    @PostMapping("/temp-save")
    public ResponseEntity<ResponseDTO<?>> savePayInfo(HttpSession session,
                                                      @RequestBody SavePayInfoRequestDTO dto) {
        session.setAttribute(dto.orderId(), dto.amount());
        ResponseDTO<?> response =
                new ResponseDTO<>(null, HttpStatus.OK, "세션에 결제금액이 저장되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 결제 요청, 인증 후 승인 요청하기 전 데이터 검증
    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO<?>> verifySession(HttpSession session,
                                                        @RequestBody SavePayInfoRequestDTO dto) {
        Long totalPrice = (Long) session.getAttribute(dto.orderId());
        if (totalPrice == null || !totalPrice.equals(dto.amount())) {
            throw new CustomException(INVALID_PRICE_INFO);
        }
        session.removeAttribute(dto.orderId());
        return ResponseEntity.ok(new ResponseDTO<>(null, HttpStatus.OK, "결제 금액이 유효합니다."));
    }

    // 토스에 결제 승인 요청
    @PostMapping("/confirm")
    public ResponseEntity<ResponseDTO<?>> confirmPayment(@RequestBody ConfirmPaymentRequestDTO dto) {
        productGrpcClient.confirmPayment(dto);
        return ResponseEntity.ok(new ResponseDTO<>(null, HttpStatus.OK, "결제 승인되었습니다."));
    }
}
