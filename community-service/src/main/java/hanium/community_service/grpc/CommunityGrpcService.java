package hanium.community_service.grpc;

import hanium.common.proto.common.CommonResponse;
import hanium.common.proto.community.CommunityServiceGrpc;
import hanium.common.proto.community.CreatePostRequest;
import hanium.common.proto.community.Empty;
import hanium.common.proto.community.PingResponse;
import hanium.community_service.Service.PostService;
import hanium.community_service.dto.CreatePostRequestDTO;
import hanium.community_service.mapper.grpc.PostGrpcMapper;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;


/**
 * CommunityGrpcService는 gRPC 서버 측 서비스 구현체로,
 * CommunityServiceGrpc.CommunityServiceImplBase를 상속하여
 * CommunityService의 gRPC 메서드를 오버라이드합니다.
 * <p>
 * 주요 역할은 클라이언트로부터 받은 gRPC 요청을 처리하고,
 * 내부 서비스 계층을 호출하여 비즈니스 로직을 수행한 뒤,
 * 적절한 gRPC 응답을 반환하는 것입니다.
 */
@Slf4j
@GrpcService  // Spring Boot gRPC 서버 컴포넌트로 등록
@RequiredArgsConstructor
public class CommunityGrpcService extends CommunityServiceGrpc.CommunityServiceImplBase {

    @Value("${eureka.instance.hostname:unknown-host}")
    private String hostname;

    private final PostService postService;  // final로 선언하고 생성자 주입 받기

    @Override
    public void ping(Empty request, StreamObserver<PingResponse> responseObserver) {
        String hostNameToUse = hostname;  // @Value로 주입된 값 사용
        PingResponse response = PingResponse.newBuilder()
                .setMessage("pong from " + hostNameToUse)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * 게시글 생성 gRPC 메서드 구현.
     * 클라이언트로부터 CreatePostRequest 메시지를 받아,
     * DTO로 변환 후 PostService를 호출해 게시글 생성 비즈니스 로직 수행.
     * 처리 결과에 따라 성공 또는 실패 응답을 CommonResponse 메시지로 반환함.
     *
     * @param request          클라이언트가 보낸 게시글 생성 요청 메시지
     * @param responseObserver 클라이언트에 응답을 보내는 스트림 옵저버
     */

    @Override
    public void createPost(CreatePostRequest request, StreamObserver<CommonResponse> responseObserver) {
        String hostNameToUse = hostname;
        try {
            //1. grpc 메시지를 dto로 변환 (매퍼 사용)
            CreatePostRequestDTO dto = PostGrpcMapper.toDto(request);

            //2. 서비스 호출(비즈니스 로직 실행)
            postService.createPost(dto);

            //3. 성공 응답 생성
            CommonResponse response = CommonResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("게시글 생성 성공 from" + hostNameToUse)
                    .setErrorCode(0)
                    .build();
            //4. 응답 전송
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("!!!게시글 생성 실패", e);

            CommonResponse response = CommonResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("게시글 생성 실패 from" + hostNameToUse + " ErrorMessage: " + e.getMessage())
                    .setErrorCode(-1)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }


}
