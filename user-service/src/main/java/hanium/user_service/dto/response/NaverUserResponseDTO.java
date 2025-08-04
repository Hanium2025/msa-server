package hanium.user_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserResponseDTO {

    // 호출 결과 메시지
    @JsonProperty("message")
    private String message;

    // 계정 정보
    @JsonProperty("response")
    public Response naverAccount;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        @JsonProperty("nickname")
        public String nickname;

        @JsonProperty("email")
        public String email;

        @JsonProperty("mobile")
        public String mobile;

        @JsonProperty("profile_image")
        public String profileImage;
    }
}
