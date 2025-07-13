package hanium.user_service.grpc;

import hanium.common.proto.CommonResponse;
import hanium.common.proto.user.*;
import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.LoginRequestDTO;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.LoginResponseDTO;
import hanium.user_service.dto.response.MemberResponseDTO;
import hanium.user_service.dto.response.SignUpResponseDTO;
import hanium.user_service.mapper.entity.MemberEntityMapper;
import hanium.user_service.mapper.grpc.MemberGrpcMapper;
import hanium.user_service.service.AuthService;
import hanium.user_service.service.MemberService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final MemberService memberService;
    @Value("${eureka.instance.hostname:unknown-host}")
    private String hostname;

    private final AuthService authService;

    @Override
    public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
        try {
            // grpc -> dto 변환
            SignUpRequestDTO requestDTO = MemberGrpcMapper.toSignupDto(request);
            // 서비스 호출
            SignUpResponseDTO responseDTO = MemberEntityMapper.toSignupResponseDTO(authService.signUp(requestDTO));
            // 성공 응답 생성
            responseObserver.onNext(MemberGrpcMapper.toSignupResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("⚠️ 회원가입 실패", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            // grpc -> dto 변환
            LoginRequestDTO requestDTO = MemberGrpcMapper.toLoginDto(request);
            // 서비스 호출
            LoginResponseDTO responseDTO = authService.login(requestDTO);
            // 성공 응답 생성
            responseObserver.onNext(MemberGrpcMapper.toLoginResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("⚠️ 로그인 실패", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getMember(GetMemberRequest request, StreamObserver<GetMemberResponse> responseObserver) {
        try {
            MemberResponseDTO responseDTO = MemberEntityMapper.toMemberResponseDto(
                    memberService.getMemberById(request.getMemberId()));
            responseObserver.onNext(MemberGrpcMapper.toGetMemberResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getAuthority(GetAuthorityRequest request, StreamObserver<GetAuthorityResponse> responseObserver) {
        try {
            Member member = memberService.getMemberByEmail(request.getEmail());
            Collection<? extends GrantedAuthority> authorities = member.getAuthorities();
            GrantedAuthority grantedAuthority = authorities.stream().findAny()
                    .orElseThrow(() -> new Exception("권한을 조회할 수 없습니다."));
            String result = grantedAuthority.getAuthority();
            log.info("✅ gRPC 서비스 - getAuthority 호출 : String authority = {}",  result);
            responseObserver.onNext(MemberGrpcMapper.toAuthorityResponse(result));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}
