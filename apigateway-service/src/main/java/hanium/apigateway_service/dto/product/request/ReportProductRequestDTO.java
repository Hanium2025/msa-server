package hanium.apigateway_service.dto.product.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportProductRequestDTO {
    private String reason;
    private String details;
}
