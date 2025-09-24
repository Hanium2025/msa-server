package hanium.product_service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.dto.response.DeliveryStatusSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class SweetTrackerUtil {
    @Value("${spring.sweetTracker.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public SweetTrackerUtil(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public List<DeliveryStatusSummaryDTO> fetchTrackingInfo(String code, String invoiceNo) {
        try {
            String jsonResponse = webClient.get()
                    .uri("http://info.sweettracker.co.kr/api/v1/trackingInfo", uriBuilder -> uriBuilder
                            .queryParam("t_key", apiKey)
                            .queryParam("t_code", code)
                            .queryParam("t_invoice", invoiceNo)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode node = objectMapper.readTree(jsonResponse);
            JsonNode trackingInfo = node.path("trackingDetails");

            return objectMapper.convertValue(
                    trackingInfo,
                    new TypeReference<List<DeliveryStatusSummaryDTO>>() {}
            );
        } catch (Exception e) {
            throw new CustomException(ErrorCode.API_CALL_FAIL);
        }
    }

}
