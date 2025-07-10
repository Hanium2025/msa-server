package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.user.LoginRequestDTO;
import hanium.apigateway_service.dto.user.SignUpRequestDTO;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.common.proto.CommonResponse;
import hanium.common.proto.user.*;
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
    public SignUpResponse signUp(SignUpRequestDTO dto) {
        // dto -> grpc
        SignUpRequest request = UserGrpcMapperForGateway.toSignUpGrpc(dto);
        try {
            return stub.signUp(request); // UserGrpcService > signUp
        } catch (StatusRuntimeException e) {
            log.error("[gRPC] signUp 호출 실패 - {}", e.getStatus().getDescription());
            throw e;
        }
    }

    // 로그인
    public LoginResponse login(LoginRequestDTO dto) {
        // dto -> grpc
        LoginRequest request = UserGrpcMapperForGateway.toLoginGrpc(dto);
        try {
            return stub.login(request);
        } catch (StatusRuntimeException e) {
            log.error("[gRPC] login 호출 실패 - {}", e.getStatus().getDescription());
            throw e;
        }
    }

    // 회원 조회 by id
    public GetMemberResponse getMemberById(Long memberId) {
        GetMemberRequest request = GetMemberRequest.newBuilder().setMemberId(memberId).build();
        try {
            return stub.getMember(request);
        } catch (StatusRuntimeException e) {
            log.error("[gRPC] getMemberById 호출 실패 - {}", e.getStatus().getDescription());
            throw e;
        }
    }
}
