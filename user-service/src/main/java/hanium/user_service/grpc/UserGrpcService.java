package hanium.user_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.common.Empty;
import hanium.common.proto.user.*;
import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.*;
import hanium.user_service.dto.response.*;
import hanium.user_service.mapper.MemberGrpcMapper;
import hanium.user_service.service.*;
import hanium.user_service.util.JwtUtil;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@GrpcService
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final MemberService memberService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final SmsService smsService;
    private final ProfileService profileService;
    private final OAuthService oAuthService;

    // 회원가입
    @Override
    public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
        try {
            SignUpResponseDTO responseDTO = SignUpResponseDTO.from(
                    authService.signUp(SignUpRequestDTO.from(request)));
            responseObserver.onNext(MemberGrpcMapper.toSignupResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 로그인
    @Override
    public void login(LoginRequest request, StreamObserver<TokenResponse> responseObserver) {
        try {
            TokenResponseDTO responseDTO = authService.login(LoginRequestDTO.from(request));
            responseObserver.onNext(MemberGrpcMapper.toTokenResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 회원 조회
    @Override
    public void getMember(GetMemberRequest request, StreamObserver<GetMemberResponse> responseObserver) {
        try {
            MemberResponseDTO responseDTO = MemberResponseDTO.from(
                    memberService.getMemberById(request.getMemberId()));
            responseObserver.onNext(MemberGrpcMapper.toGetMemberResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 회원 권한 조회
    @Override
    public void getAuthority(GetAuthorityRequest request, StreamObserver<GetAuthorityResponse> responseObserver) {
        try {
            Member member = memberService.getMemberByEmail(request.getEmail());
            Collection<? extends GrantedAuthority> authorities = member.getAuthorities();
            GrantedAuthority grantedAuthority = authorities.stream().findAny()
                    .orElseThrow(() -> new CustomException(ErrorCode.AUTHORITY_NOT_FOUND));

            String authority = grantedAuthority.getAuthority();
            Long memberId = member.getId();

            responseObserver.onNext(MemberGrpcMapper.toAuthorityResponse(authority, memberId));
            responseObserver.onCompleted();

        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 토큰 재발급
    @Override
    public void reissueToken(ReissueTokenRequest request, StreamObserver<TokenResponse> responseObserver) {
        try {
            TokenResponseDTO dto = jwtUtil.checkRefreshTokenAndReissue(request.getRefreshToken());
            responseObserver.onNext(MemberGrpcMapper.toTokenResponse(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // sms 인증번호 전송
    @Override
    public void sendSms(SendSmsRequest request, StreamObserver<SendSmsResponse> responseObserver) {
        try {
            smsService.sendSms(request.getPhoneNumber());
            responseObserver.onNext(SendSmsResponse.newBuilder().setMessage("메시지 발송 완료").build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // sms 인증번호 검증
    @Override
    public void verifySmsCode(VerifySmsRequest request, StreamObserver<VerifySmsResponse> responseObserver) {
        try {
            boolean isVerified = smsService.verifyCode(VerifySmsDTO.from(request));
            responseObserver.onNext(VerifySmsResponse.newBuilder().setVerified(isVerified).build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 카카오 로그인 키 전송
    @Override
    public void getKakaoConfig(Empty request, StreamObserver<KakaoConfigResponse> responseObserver) {
        try {
            Map<String, String> map = oAuthService.getKakaoConfig();
            responseObserver.onNext(MemberGrpcMapper.toKakaoConfigResponse(map));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 네이버 로그인 키 전송
    @Override
    public void getNaverConfig(Empty request, StreamObserver<NaverConfigResponse> responseObserver) {
        try {
            NaverConfigResponseDTO responseDTO = oAuthService.getNaverConfig();
            responseObserver.onNext(MemberGrpcMapper.toNaverConfigResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
        super.getNaverConfig(request, responseObserver);
    }

    // 소셜 로그인 code로 회원가입 or 로그인
    @Override
    public void socialLogin(SocialLoginRequest request, StreamObserver<TokenResponse> responseObserver) {
        try {
            TokenResponseDTO dto = request.getProvider().equals("kakao") ?
                    oAuthService.kakaoLogin(request.getCode()) :
                    oAuthService.naverLogin(request.getCode());
            responseObserver.onNext(MemberGrpcMapper.toTokenResponse(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    //닉네임 받아오기
    @Override
    public void getNicknameByMemberId(GetNicknameRequest request, StreamObserver<GetNicknameResponse> responseObserver) {
        try {
            GetNicknameRequestDTO requestDTO = GetNicknameRequestDTO.from(request);
            GetNicknameResponseDTO dto = profileService.getNicknameByMemberId(requestDTO);
            GetNicknameResponse response = GetNicknameResponse.newBuilder()
                    .setNickname(dto.getNickname())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 닉네임, 프로필사진 반환
    @Override
    public void getProfile(GetProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        try {
            responseObserver.onNext(
                    MemberGrpcMapper.toProfileResponse(
                            profileService.getProfileByMemberId(request.getMemberId())
                    )
            );
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 프로필사진 수정용 presigned url
    @Override
    public void getPresignedUrl(GetPresignedUrlRequest request, StreamObserver<PresignedUrlResponse> responseObserver) {
        try {
            PresignedUrlResponseDTO dto =
                    profileService.getPresignedUrl(GetPresignedUrlRequestDTO.from(request));
            responseObserver.onNext(MemberGrpcMapper.toPresignedUrlResponse(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 프로필 수정
    @Override
    public void updateProfile(UpdateProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        try {
            ProfileResponseDTO dto = profileService.updateProfile(request);
            responseObserver.onNext(MemberGrpcMapper.toProfileResponse(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 프로필 상세 조회
    @Override
    public void getDetailProfile(GetProfileRequest request, StreamObserver<ProfileDetailResponse> responseObserver) {
        try {
            ProfileDetailResponseDTO dto = profileService.getProfileDetailByMemberId(request.getMemberId());
            responseObserver.onNext(MemberGrpcMapper.toProfileDetailResponse(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 마이페이지 마케팅 동의 토글
    @Override
    public void toggleMarketing(GetProfileRequest request, StreamObserver<SendSmsResponse> responseObserver) {
        try {
            String message = memberService.toggleAgreeMarketing(request.getMemberId());
            responseObserver.onNext(SendSmsResponse.newBuilder().setMessage(message).build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 마이페이지 제3자 동의 토글
    @Override
    public void toggleThirdParty(GetProfileRequest request, StreamObserver<SendSmsResponse> responseObserver) {
        try {
            String message = memberService.toggleAgreeThirdParty(request.getMemberId());
            responseObserver.onNext(SendSmsResponse.newBuilder().setMessage(message).build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 거래 평가 시 신뢰도 업데이트
    @Override
    public void updateScore(UpdateScoreRequest request, StreamObserver<Empty> responseObserver) {
        try {
            profileService.updateScore(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 타 사용자 프로필 조회
    @Override
    public void getOtherProfile(GetProfileRequest request, StreamObserver<GetOtherProfileResponse> responseObserver) {
        try {
            OtherProfileResponseDTO dto = profileService.getOtherProfile(request.getMemberId());
            responseObserver.onNext(MemberGrpcMapper.toOtherProfileResponse(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }
}
