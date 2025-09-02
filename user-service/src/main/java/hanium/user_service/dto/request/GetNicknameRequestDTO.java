package hanium.user_service.dto.request;

import hanium.common.proto.user.GetNicknameRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetNicknameRequestDTO {
    private Long memberId;

    //grpc -> dto
    public static GetNicknameRequestDTO from(GetNicknameRequest proto) {
        return GetNicknameRequestDTO.builder()
                .memberId(proto.getMemberId())
                .build();
    }

}
