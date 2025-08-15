package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.user.request.LoginRequestDTO;
import hanium.apigateway_service.dto.user.request.SignUpRequestDTO;
import hanium.apigateway_service.dto.user.request.VerifySmsRequestDTO;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.apigateway_service.security.JwtUtil;
import hanium.common.exception.CustomException;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.user.*;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpHeaders;
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
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 로그인
    public TokenResponse login(LoginRequestDTO dto, HttpServletResponse response) {
        LoginRequest grpcRequest = UserGrpcMapperForGateway.toLoginGrpc(dto);
        try {
            TokenResponse tokenResponse = stub.login(grpcRequest);
//            response.addCookie(JwtUtil.removeCookie());
//            response.addCookie(JwtUtil.createCookie(tokenResponse.getRefreshToken()));
            response.addHeader(HttpHeaders.SET_COOKIE, JwtUtil.refreshDeleteCookie().toString());
            response.addHeader(HttpHeaders.SET_COOKIE, JwtUtil.refreshSetCookie(tokenResponse.getRefreshToken()).toString());
            response.setHeader("Authorization", tokenResponse.getAccessToken());
            return tokenResponse;
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 회원 조회 by id
    public GetMemberResponse getMemberById(Long memberId) {
        GetMemberRequest request = GetMemberRequest.newBuilder().setMemberId(memberId).build();
        try {
            return stub.getMember(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 회원 권한 조회 by email
    public GetAuthorityResponse getAuthority(String email) {
        GetAuthorityRequest request = GetAuthorityRequest.newBuilder().setEmail(email).build();
        try {
            return stub.getAuthority(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 토큰 재발급
    public TokenResponse reissueToken(String refreshToken, HttpServletResponse response) {
        ReissueTokenRequest request = ReissueTokenRequest.newBuilder().setRefreshToken(refreshToken).build();
        try {
            TokenResponse tokenResponse = stub.reissueToken(request);
//            response.addCookie(JwtUtil.removeCookie());
//            response.addCookie(JwtUtil.createCookie(tokenResponse.getRefreshToken()));
            // 기존 addCookie() 제거하고 Set-Cookie로
            response.addHeader(HttpHeaders.SET_COOKIE, JwtUtil.refreshDeleteCookie().toString());
            response.addHeader(HttpHeaders.SET_COOKIE, JwtUtil.refreshSetCookie(tokenResponse.getRefreshToken()).toString());
            response.setHeader("Authorization", tokenResponse.getAccessToken());
            return tokenResponse;
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // SMS 인증번호 전송
    public SendSmsResponse sendSms(String phoneNumber) {
        SendSmsRequest request = SendSmsRequest.newBuilder().setPhoneNumber(phoneNumber).build();
        try {
            return stub.sendSms(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // SMS 인증번호 검증
    public VerifySmsResponse verifySms(VerifySmsRequestDTO dto) {
        VerifySmsRequest request = UserGrpcMapperForGateway.toVerifySmsGrpc(dto);
        try {
            return stub.verifySmsCode(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }
}
