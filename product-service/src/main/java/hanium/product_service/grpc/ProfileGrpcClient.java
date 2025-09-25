package hanium.product_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.user.GetProfileRequest;
import hanium.common.proto.user.UpdateScoreRequest;
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

    // 거래 평가 시 신뢰도 업데이트
    public void updateReliabilityScoreByReview(Long memberId, int rating) {
        try {
            final int RATIO_FOR_REVIEW = 2;
            UpdateScoreRequest request = UpdateScoreRequest.newBuilder()
                    .setMemberId(memberId)
                    .setAmount(rating * RATIO_FOR_REVIEW)
                    .build();
            stub.updateScore(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상품 신고 시 신뢰도 업데이트
    public void updateReliabilityScoreByReport(Long memberId) {
        try {
            final int SCORE_WHEN_REPORTED = -10;
            UpdateScoreRequest request = UpdateScoreRequest.newBuilder()
                    .setMemberId(memberId)
                    .setAmount(SCORE_WHEN_REPORTED)
                    .build();
            stub.updateScore(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }
}