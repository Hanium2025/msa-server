package hanium.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NaverConfigResponseDTO {
    private String clientId;
    private String redirectUri;
    private String state;
}
