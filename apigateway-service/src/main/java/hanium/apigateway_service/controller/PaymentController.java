package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.product.request.SavePayInfoRequestDTO;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.exception.CustomException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hanium.common.exception.ErrorCode.INVALID_PRICE_INFO;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    // 세션에 프론트엔드에서 넘어온 orderId와 결제 금액 저장
    @PostMapping("/temp-save")
    public ResponseEntity<ResponseDTO<?>> savePayInfo(HttpSession session,
                                                      @RequestBody SavePayInfoRequestDTO dto) {
        session.setAttribute(dto.orderId(), dto.totalPrice());
        ResponseDTO<?> response =
                new ResponseDTO<>(null, HttpStatus.OK, "세션에 결제금액이 저장되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 결제 후 넘어온 orderId, 결제 금액이 이전에 세션에 저장된 것과 같은지 검증
    @PostMapping("/verify-session")
    public ResponseEntity<ResponseDTO<?>> verifySession(HttpSession session,
                                                        @RequestBody SavePayInfoRequestDTO dto) {
        Long totalPrice = (Long) session.getAttribute(dto.orderId());
        if (totalPrice == null || !totalPrice.equals(dto.totalPrice())) {
            throw new CustomException(INVALID_PRICE_INFO);
        }
        session.removeAttribute(dto.orderId());
        return ResponseEntity.ok(new ResponseDTO<>(null, HttpStatus.OK, "결제 금액이 유효합니다."));
    }

}
