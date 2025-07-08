package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.MemberSignupRequestDTO;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.common.proto.CommonResponse;
import hanium.common.proto.user.SignUpRequest;
import hanium.common.proto.user.UserServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserGrpcClient {

    @GrpcClient("user_service") //discovery:///user_service 사용
    private UserServiceGrpc.UserServiceBlockingStub stub;

    public CommonResponse signUp(MemberSignupRequestDTO dto) {
        SignUpRequest request = UserGrpcMapperForGateway.toSignUpGrpc(dto);
        try {
            return stub.signUp(request);
        } catch (StatusRuntimeException e) {
            log.error("grpc 호출 실패 - {}", e.getStatus().getDescription());
            throw e;
        }
    }
}
