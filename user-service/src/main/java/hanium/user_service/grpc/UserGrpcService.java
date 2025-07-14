package hanium.user_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
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
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
        try {
            SignUpRequestDTO requestDTO = MemberGrpcMapper.toSignupDto(request);
            SignUpResponseDTO responseDTO = MemberEntityMapper.toSignupResponseDTO(authService.signUp(requestDTO));
            responseObserver.onNext(MemberGrpcMapper.toSignupResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(generateException(e.getErrorCode()));
        }
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        try {
            LoginRequestDTO requestDTO = MemberGrpcMapper.toLoginDto(request);
            LoginResponseDTO responseDTO = authService.login(requestDTO);
            responseObserver.onNext(MemberGrpcMapper.toLoginResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(generateException(e.getErrorCode()));
        }
    }

    @Override
    public void getMember(GetMemberRequest request, StreamObserver<GetMemberResponse> responseObserver) {
        try {
            MemberResponseDTO responseDTO = MemberEntityMapper.toMemberResponseDto(
                    memberService.getMemberById(request.getMemberId()));
            responseObserver.onNext(MemberGrpcMapper.toGetMemberResponse(responseDTO));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(generateException(e.getErrorCode()));
        }
    }

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
