package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.LoginRequestDTO;
import hanium.apigateway_service.dto.SignUpRequestDTO;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.common.proto.CommonResponse;
import hanium.common.proto.user.LoginRequest;
import hanium.common.proto.user.LoginResponse;
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

    // 회원가입
    public CommonResponse signUp(SignUpRequestDTO dto) {
        SignUpRequest request = UserGrpcMapperForGateway.toSignUpGrpc(dto);
        try {
            return stub.signUp(request);
        } catch (StatusRuntimeException e) {
            log.error("grpc 호출 실패 - {}", e.getStatus().getDescription());
            throw e;
        }
    }

    // 로그인
    public LoginResponse login(LoginRequestDTO dto) {
        LoginRequest request = UserGrpcMapperForGateway.toLoginGrpc(dto);
        try {
            return stub.login(request);
        } catch (StatusRuntimeException e) {
            log.error("grpc 호출 실패 - {}", e.getStatus().getDescription());
            throw e;
        }
    }
}
