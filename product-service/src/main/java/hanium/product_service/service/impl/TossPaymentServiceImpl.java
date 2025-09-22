package hanium.product_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.common.exception.CustomException;
import hanium.product_service.domain.PaymentLog;
import hanium.product_service.domain.PaymentMethod;
import hanium.product_service.domain.PaymentStatus;
import hanium.product_service.domain.Trade;
import hanium.product_service.dto.request.ConfirmPaymentRequestDTO;
import hanium.product_service.dto.response.TossSuccessResponseDTO;
import hanium.product_service.repository.PaymentLogRepository;
import hanium.product_service.service.TossPaymentService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Base64.Encoder;

import static hanium.common.exception.ErrorCode.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TossPaymentServiceImpl implements TossPaymentService {

    private final PaymentLogRepository paymentLogRepository;
    @Value("${toss.secret-key}")
    private String tossApiKey;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    // 토스에게 결제 승인 요청
    @Override
    @Transactional
    public void confirmPayment(ConfirmPaymentRequestDTO dto) {
        try {
            HttpResponse<String> tossResponse = requestConfirmForPayment(dto);

            log.info("✅ confirmPayment: 토스 응답 받음");
            if (tossResponse.statusCode() == 200) {
                log.info("✅ confirmPayment: 토스 응답 200");
                TossSuccessResponseDTO successDTO =
                        objectMapper.readValue(tossResponse.body(), TossSuccessResponseDTO.class);
                log.info("✅ confirmPayment: 토스 응답을 TossSuccessResponseDTO로 읽어옴");
                PaymentLog paymentLog = PaymentLog.builder()
                        .trade(entityManager.getReference(Trade.class, dto.tradeId()))
                        .paymentKey(successDTO.getPaymentKey())
                        .orderName(successDTO.getOrderName())
                        .orderId(successDTO.getOrderId())
                        .totalPrice((long) successDTO.getTotalAmount())
                        .paymentMethod(parsePaymentMethod(successDTO))
                        .paymentStatus(PaymentStatus.valueOf(successDTO.getStatus()))
                        .requestedAt(parseStringToLocalDateTime(successDTO.getRequestedAt()))
                        .approvedAt(parseStringToLocalDateTime(successDTO.getApprovedAt()))
                        .build();
                log.info("✅ confirmPayment: dto에서 PaymentLog 객체 빌드함");
                paymentLogRepository.save(paymentLog);

            } else {
                log.info("⚠️ confirmPayment: 토스 응답 500, cancel 호출 중...");
                cancelPayment(dto);
            }
        } catch (Exception e) {
            log.info("⚠️ 토스에 거래 승인 요청을 보내는 데에 실패함 !!");
            throw new CustomException(PAYMENT_SERVER_ERROR);
        }
    }

    // 토스에게 결제 취소 요청
    private void cancelPayment(ConfirmPaymentRequestDTO dto) {
        HttpResponse<String> cancelResponse;
        try {
            cancelResponse = requestCancelForPayment(dto.paymentKey());
        } catch (IOException | InterruptedException e) {
            log.info("⚠️ cancelPayment: 토스에 거래 취소 요청 보내는 데에 실패함 !!");
            throw new CustomException(PAYMENT_CANCEL_ERROR);
        }
        log.info("⚠️ cancelPayment: 토스 응답 받음");
        if (cancelResponse.statusCode() == 200) {
            log.info("⚠️ cancelPayment: 토스 상 거래 취소 성공");
            throw new CustomException(PAYMENT_DB_ERROR_CANCELED);
        } else {
            log.info("⚠️ cancelPayment: 토스 상 거래 취소 실패 !!");
            throw new CustomException(PAYMENT_CANCEL_ERROR);
        }
    }

    // 토스의 API 호출해 결제 승인 요청
    private HttpResponse<String> requestConfirmForPayment(ConfirmPaymentRequestDTO dto)
            throws IOException, InterruptedException {

        log.info("✅ requestConfirmForPayment: 토스 요청 보내는 메서드 진입");
        JsonNode requestObj = objectMapper.createObjectNode()
                .put("orderId", dto.orderId())
                .put("amount", dto.amount())
                .put("paymentKey", dto.paymentKey());
        log.info("✅ requestConfirmForPayment: requestObj 생성됨");

        String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CONFIRM_URL))
                .header(HttpHeaders.AUTHORIZATION, getAuthorizationBasicHeader(tossApiKey))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .method(HttpMethod.POST, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestObj)))
                .build();
        log.info("✅ requestConfirmForPayment: http request 생성됨");

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    // 토스의 API 호출해 결제 취소 요청
    private HttpResponse<String> requestCancelForPayment(String paymentKey)
            throws IOException, InterruptedException {

        log.info("⚠️ requestCancelForPayment: 토스 cancel 요청 보내는 메서드 진입");
        JsonNode requestObj = objectMapper.createObjectNode().put("cancelReason", "서버 데이터베이스 저장 오류");
        String CANCEL_URL = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CANCEL_URL))
                .header(HttpHeaders.AUTHORIZATION, getAuthorizationBasicHeader(tossApiKey))
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .method(HttpMethod.POST, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestObj)))
                .build();
        log.info("⚠️ requestCancelForPayment: http 요청 생성됨");

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String getAuthorizationBasicHeader(String text) {
        text += ":";
        byte[] byteText = text.getBytes();

        Encoder encoder = Base64.getEncoder();
        byte[] encodedText = encoder.encode(byteText);

        return "Basic " + new String(encodedText);
    }

    private PaymentMethod parsePaymentMethod(TossSuccessResponseDTO dto) {
        String method = dto.getMethod();
        String easyPayProvider = dto.getEasyPayProvider();

        if (method.startsWith("카드")) {
            return PaymentMethod.CARD;
        } else if (method.startsWith("계좌이체")) {
            return PaymentMethod.TRANSFER;
        } else if (method.startsWith("휴대폰")) {
            return PaymentMethod.MOBILE;

        } else {
            if (easyPayProvider.startsWith("토스")) {
                return PaymentMethod.TOSS;
            } else if (easyPayProvider.startsWith("네이버")) {
                return PaymentMethod.NAVER;
            } else if (easyPayProvider.startsWith("카카오")) {
                return PaymentMethod.KAKAO;
            } else if (easyPayProvider.startsWith("페이코")) {
                return PaymentMethod.PAYCO;
            }
        }

        return PaymentMethod.UNKNOWN;
    }

    private LocalDateTime parseStringToLocalDateTime(String date) {
        return OffsetDateTime
                .parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .toLocalDateTime();
    }
}
