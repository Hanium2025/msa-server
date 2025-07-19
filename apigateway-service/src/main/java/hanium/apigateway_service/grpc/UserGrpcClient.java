package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.user.request.LoginRequestDTO;
import hanium.apigateway_service.dto.user.request.SignUpRequestDTO;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.user.*;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public TokenResponse login(LoginRequestDTO dto, HttpServletResponse response) {
        LoginRequest request = UserGrpcMapperForGateway.toLoginGrpc(dto);
        try {
            TokenResponse tokenResponse = stub.login(request);
            response.addCookie(createCookie(tokenResponse.getRefreshToken()));
            response.setHeader("Authorization", tokenResponse.getAccessToken());
            return tokenResponse;
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

    // 토큰 재발급
    public TokenResponse reissueToken(String refreshToken) {
        ReissueTokenRequest request = ReissueTokenRequest.newBuilder().setRefreshToken(refreshToken).build();
        try {
            return stub.reissueToken(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(extractErrorCode(e));
        }
    }

    // SMS 인증번호 전송
    public SendSmsResponse sendSms(String phoneNumber) {
        SendSmsRequest request = SendSmsRequest.newBuilder().setPhoneNumber(phoneNumber).build();
        try {
            return stub.sendSms(request);
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

    /**
     * Refresh 토큰 문자열로 쿠키를 생성해 반환합니다.
     *
     * @param refreshToken 전달할 Refresh 토큰
     * @return 헤더의 쿠키 객체
     */
    private Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie("RefreshToken", refreshToken);
        cookie.setMaxAge(12 * 60 * 60); // 12h
        cookie.setHttpOnly(true);   // JS로 접근 불가, 탈취 위험 감소
        return cookie;
    }
}
