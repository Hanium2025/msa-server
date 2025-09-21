package hanium.product_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryStatusSummaryDTO {
    @JsonProperty("timeString")
    private String time;

    @JsonProperty("where")
    private String location;

    @JsonProperty("kind")
    private String status;

}
