package hanium.product_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.user.GetNicknameRequest;
import hanium.common.proto.user.GetNicknameResponse;
import hanium.common.proto.user.UserServiceGrpc;
import hanium.product_service.mapper.ProfileGrpcMapper;
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

    public String getNicknameByMemberId(Long memberId) {
        try {
            GetNicknameRequest request = ProfileGrpcMapper.toGrpc(memberId);

            GetNicknameResponse response = stub.getNicknameByMemberId(request);
            return response.getNickname();

        } catch (
                StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }
}