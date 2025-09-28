package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.user.request.LoginRequestDTO;
import hanium.apigateway_service.dto.user.request.SignUpRequestDTO;
import hanium.apigateway_service.dto.user.request.UpdateProfileRequestDTO;
import hanium.apigateway_service.dto.user.request.VerifySmsRequestDTO;
import hanium.apigateway_service.dto.user.response.OtherProfileResponseDTO;
import hanium.apigateway_service.dto.user.response.PresignedUrlResponseDTO;
import hanium.apigateway_service.dto.user.response.ProfileDetailResponseDTO;
import hanium.apigateway_service.dto.user.response.ProfileResponseDTO;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.apigateway_service.security.JwtUtil;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.common.Empty;
import hanium.common.proto.user.*;
import io.grpc.StatusRuntimeException;
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
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 로그인
    public TokenResponse login(LoginRequestDTO dto, HttpServletResponse response) {
        LoginRequest grpcRequest = UserGrpcMapperForGateway.toLoginGrpc(dto);
        try {
            TokenResponse tokenResponse = stub.login(grpcRequest);
            response.addCookie(JwtUtil.removeCookie());
            response.addCookie(JwtUtil.createCookie(tokenResponse.getRefreshToken()));
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
            response.addCookie(JwtUtil.removeCookie());
            response.addCookie(JwtUtil.createCookie(tokenResponse.getRefreshToken()));
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

    // 카카오 로그인 설정 값
    public KakaoConfigResponse getKakaoConfig() {
        try {
            return stub.getKakaoConfig(Empty.newBuilder().build());
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 네이버 로그인 설정 값
    public NaverConfigResponse getNaverConfig() {
        try {
            return stub.getNaverConfig(Empty.newBuilder().build());
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 소셜 로그인 code 받아서 회원가입 or 로그인
    public TokenResponse socialLogin(String code, String provider) {
        SocialLoginRequest request = SocialLoginRequest.newBuilder()
                .setCode(code).setProvider(provider).build();
        try {
            return stub.socialLogin(request);
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 프로필사진 수정용 Presigned url 발급
    public PresignedUrlResponseDTO getPresignedUrl(Long memberId, String contentType) {
        GetPresignedUrlRequest request = GetPresignedUrlRequest.newBuilder()
                .setMemberId(memberId).setContentType(contentType).build();
        try {
            return PresignedUrlResponseDTO.from(stub.getPresignedUrl(request));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 프로필 수정
    public ProfileResponseDTO updateProfile(Long memberId, UpdateProfileRequestDTO dto) {
        UpdateProfileRequest request = UserGrpcMapperForGateway.toUpdateProfileGrpc(memberId, dto);
        try {
            return ProfileResponseDTO.from(stub.updateProfile(request));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 내 프로필 조회
    public ProfileDetailResponseDTO getDetailProfile(Long memberId) {
        GetProfileRequest request = GetProfileRequest.newBuilder().setMemberId(memberId).build();
        try {
            return ProfileDetailResponseDTO.from(stub.getDetailProfile(request));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 마이페이지 마케팅 동의 토글
    public String toggleAgreeMarketing(Long memberId) {
        GetProfileRequest request = GetProfileRequest.newBuilder().setMemberId(memberId).build();
        try {
            return stub.toggleMarketing(request).getMessage();
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 마이페이지 제3자 동의 토글
    public String toggleAgreeThirdParty(Long memberId) {
        GetProfileRequest request = GetProfileRequest.newBuilder().setMemberId(memberId).build();
        try {
            return stub.toggleThirdParty(request).getMessage();
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 타 사용자 프로필 조회
    public OtherProfileResponseDTO getOtherProfile(Long myId, Long memberId) {
        if (myId.equals(memberId)) {
            throw new CustomException(ErrorCode.OTHER_PROFILE_ERROR);
        }
        GetProfileRequest request = GetProfileRequest.newBuilder().setMemberId(memberId).build();
        try {
            return OtherProfileResponseDTO.from(stub.getOtherProfile(request));
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }
}
