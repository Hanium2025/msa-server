package hanium.product_service.mapper;

import hanium.common.proto.user.GetNicknameRequest;

public class ProfileGrpcMapper {
    public static GetNicknameRequest toGrpc(Long memberId) {
        return GetNicknameRequest.newBuilder()
                .setMemberId(memberId)
                .build();
    }
}
