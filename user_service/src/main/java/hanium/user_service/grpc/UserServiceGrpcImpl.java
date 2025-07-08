package hanium.user_service.grpc;

import hanium.common.proto.CommonResponse;
import hanium.common.proto.user.SignUpRequest;
import hanium.common.proto.user.UserServiceGrpc;
import hanium.user_service.dto.request.MemberSignupRequestDto;
import hanium.user_service.mapper.MemberMapper;
import hanium.user_service.service.AuthService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    @Value("${eureka.instance.hostname:unknown-host}")
    private String hostname;

    private final AuthService authService;

    @Override
    public void signUp(SignUpRequest request, StreamObserver<CommonResponse> responseObserver) {
        try {
            // grpc -> dto 변환
            MemberSignupRequestDto dto = MemberMapper.toSignupDto(request);
            // 서비스 호출
            authService.signUp(dto);
            // 성공 응답 생성
            CommonResponse response = CommonResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("회원가입에 성공했습니다 - " + hostname)
                    .setErrorCode(0)
                    .build();
            // 응답 전송
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("회원가입 실패", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("회원가입에 실패했습니다 - " + hostname + "\nErrorMessage: " + e.getMessage())
                    .setErrorCode(-1)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
