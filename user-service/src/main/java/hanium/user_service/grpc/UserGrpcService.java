package hanium.user_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.user.*;
import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.request.VerifySmsDTO;
import hanium.user_service.dto.response.MemberResponseDTO;
import hanium.user_service.dto.response.SignUpResponseDTO;
import hanium.user_service.dto.response.TokenResponseDTO;
import hanium.user_service.mapper.MemberGrpcMapper;
import hanium.user_service.security.JwtUtil;
import hanium.user_service.service.AuthService;
import hanium.user_service.service.MemberService;
import hanium.user_service.service.SmsService;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@GrpcService
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final MemberService memberService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final SmsService smsService;

    // 회원가입
    @Override
    public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
        try {
            SignUpResponseDTO responseDTO = SignUpResponseDTO.from(
                    authService.signUp(SignUpRequestDTO.from(request)));
            responseObserver.onNext(MemberGrpcMapper.toSignupResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(generateException(e.getErrorCode()));
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
            responseObserver.onError(generateException(e.getErrorCode()));
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
            responseObserver.onError(generateException(e.getErrorCode()));
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
            String result = grantedAuthority.getAuthority();

            responseObserver.onNext(MemberGrpcMapper.toAuthorityResponse(result));
            responseObserver.onCompleted();

        } catch (CustomException e) {
            responseObserver.onError(generateException(e.getErrorCode()));
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
            responseObserver.onError(generateException(e.getErrorCode()));
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
            responseObserver.onError(generateException(e.getErrorCode()));
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
            responseObserver.onError(generateException(e.getErrorCode()));
        }
    }

    /**
     * 서비스 로직의 CustomException을 캐치해 ErrorCode를 가지고
     * proto 메시지인 CustomError에 해당 ErrorCode 정보를 Metadata 로써 삽입합니다.
     * 해당 Metadata를 가진 StatusRuntimeException을 반환합니다.
     *
     * @param e 발생한 CustomError의 ErrorCode
     * @return gRPC client에 전달할 StatusRuntimeException
     */
    private StatusRuntimeException generateException(ErrorCode e) {
        Metadata metadata = new Metadata();
        Metadata.Key<CustomError> customErrorKey = ProtoUtils.keyForProto(CustomError.getDefaultInstance());
        metadata.put(customErrorKey, CustomError.newBuilder()
                .setErrorName(e.name())
                .setMessage(e.getMessage())
                .build());
        return Status.INTERNAL.asRuntimeException(metadata);
    }
}
