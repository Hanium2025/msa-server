package hanium.common.exception;

import hanium.common.proto.common.CustomError;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcUtil {

    /**
     * 전달된 StatusRuntimeException서 CustomError proto 메시지를 가져오고
     * 해당 메시지에서 errorName을 가져와 알맞은 ErrorCode를 반환합니다.
     *
     * @param e gRPC 서버에서 전달된 StatusRuntimeException
     * @return http 클라이언트로 전송할 ErrorCode
     */
    public static ErrorCode extractErrorCode(StatusRuntimeException e) {
        log.info("✅ 서비스에서 grpc 예외 감지됨");
        Metadata metadata = Status.trailersFromThrowable(e);
        Metadata.Key<CustomError> customErrorKey = ProtoUtils.keyForProto(CustomError.getDefaultInstance());

        if (metadata != null) {
            CustomError customError = metadata.get(customErrorKey);
            log.info("✅ 던져진 예외가 CustomException인 것이 확인됨, 커스텀 ErrorCode 추출 시도");
            if (customError != null) {
                String errorName = customError.getErrorName();
                log.info("✅ 커스텀 ErrorCode 추출됨: {}", errorName);
                return ErrorCode.valueOf(errorName);
            }
        }
        log.info("✅ ErrorCode 추출 실패, 커스텀으로 처리될 수 있는 예외가 아닌 gRPC 오류로 추정");
        log.error("⚠️ 실제 gPRC 오류 메시지: {}", e.getMessage());
        return ErrorCode.INTERNAL_ERROR;
    }

    /**
     * 서비스 로직의 CustomException을 캐치해 ErrorCode를 가지고
     * proto 메시지인 CustomError에 해당 ErrorCode 정보를 Metadata 로써 삽입합니다.
     * 해당 Metadata를 가진 StatusRuntimeException을 반환합니다.
     *
     * @param e 발생한 CustomError의 ErrorCode
     * @return gRPC client에 전달할 StatusRuntimeException
     */
    public static StatusRuntimeException generateException(ErrorCode e) {
        Metadata metadata = new Metadata();
        Metadata.Key<CustomError> customErrorKey = ProtoUtils.keyForProto(CustomError.getDefaultInstance());
        metadata.put(customErrorKey, CustomError.newBuilder()
                .setErrorName(e.name())
                .setMessage(e.getMessage())
                .build());
        return Status.INTERNAL.asRuntimeException(metadata);
    }
}
