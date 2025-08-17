package hanium.apigateway_service.dto.chat.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PresignedUrlDTO {
    private String putUrl;
    private String getUrl;
    private String key;
}
