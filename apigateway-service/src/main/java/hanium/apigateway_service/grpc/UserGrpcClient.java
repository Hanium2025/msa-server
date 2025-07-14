package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.user.LoginRequestDTO;
import hanium.apigateway_service.dto.user.SignUpRequestDTO;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.user.*;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserGrpcClient {

    @GrpcClient("user-service") //discovery:///user-service 사용
    private UserServiceGrpc.UserServiceBlockingStub stub;

    // 회원가입
    public SignUpResponse signUp(SignUpRequestDTO dto) {
        SignUpRequest request = UserGrpcMapperForGateway.toSignUpGrpc(dto);
        try {
            return stub.signUp(request); // UserGrpcService > signUp
        } catch (StatusRuntimeException e) {
            throw new CustomException(extractErrorCode(e));
        }
    }

    // 로그인
    public LoginResponse login(LoginRequestDTO dto) {
        LoginRequest request = UserGrpcMapperForGateway.toLoginGrpc(dto);
        try {
            return stub.login(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(extractErrorCode(e));
        }
    }

    // 회원 조회 by id
    public GetMemberResponse getMemberById(Long memberId) {
        GetMemberRequest request = GetMemberRequest.newBuilder().setMemberId(memberId).build();
        try {
            return stub.getMember(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(extractErrorCode(e));
        }
    }

    // 회원 권한 조회 by email
    public GetAuthorityResponse getAuthority(String email) {
        GetAuthorityRequest request = GetAuthorityRequest.newBuilder().setEmail(email).build();
        try {
            return stub.getAuthority(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(extractErrorCode(e));
        }
    }

    /**
     * 전달된 StatusRuntimeException서 CustomError proto 메시지를 가져오고
     * 해당 메시지에서 errorName을 가져와 알맞은 ErrorCode를 반환합니다.
     *
     * @param e gRPC 서버에서 전달된 StatusRuntimeException
     * @return http 클라이언트로 전송할 ErrorCode
     */
    private ErrorCode extractErrorCode(StatusRuntimeException e) {
        Metadata metadata = Status.trailersFromThrowable(e);
        Metadata.Key<CustomError> customErrorKey = ProtoUtils.keyForProto(CustomError.getDefaultInstance());

        assert metadata != null;
        CustomError customError = metadata.get(customErrorKey);

        assert customError != null;
        String errorName = customError.getErrorName();
        return ErrorCode.valueOf(errorName);
    }
}
