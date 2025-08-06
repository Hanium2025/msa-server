package hanium.apigateway_service.dto.user.response;

import hanium.common.proto.user.NaverConfigResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NaverConfigResponseDTO {
    private String naverClientId;
    private String naverRedirectUri;
    private String state;

    public static NaverConfigResponseDTO from(NaverConfigResponse proto) {
        return NaverConfigResponseDTO.builder()
                .naverClientId(proto.getClientId())
                .naverRedirectUri(proto.getRedirectUri())
                .state(proto.getState())
                .build();
    }
}
