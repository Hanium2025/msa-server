package hanium.product_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.user.GetProfileRequest;
import hanium.common.proto.user.UserServiceGrpc;
import hanium.product_service.dto.response.ProfileResponseDTO;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProfileGrpcClient {
    @GrpcClient("user-service") //discovery:///user-service 사용
    private UserServiceGrpc.UserServiceBlockingStub stub;

    // 프로필 (닉네임, 사진) 조회
    public ProfileResponseDTO getProfileByMemberId(Long memberId) {
        try {
            GetProfileRequest request = GetProfileRequest.newBuilder().setMemberId(memberId).build();
            return ProfileResponseDTO.from(stub.getProfile(request));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }
}