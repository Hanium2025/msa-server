package hanium.user_service.dto.response;

import hanium.common.proto.user.GetNicknameResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetNicknameResponseDTO {
    private String nickname;

    public static GetNicknameResponseDTO from(GetNicknameResponse proto) {
        return GetNicknameResponseDTO.builder()
                .nickname(proto.getNickname())
                .build();
    }

}
