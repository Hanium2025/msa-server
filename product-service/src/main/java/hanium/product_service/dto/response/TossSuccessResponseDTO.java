package hanium.product_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class TossSuccessResponseDTO {
    public String paymentKey;
    public String orderId;
    public String orderName;
    public String status;
    public String requestedAt;
    public String approvedAt;
    public String method;
    public String easyPayProvider;
    public String transfer;
    public String mobilePhone;
    public int totalAmount;

    @JsonProperty("easyPay")
    private void extractEasyPayProvider(Map<String, Object> easyPay) {
        this.easyPayProvider = easyPay.get("easyPayProvider").toString();
    }
}
