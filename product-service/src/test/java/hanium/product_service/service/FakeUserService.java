package hanium.product_service.service;

import hanium.common.proto.user.GetNicknameRequest;
import hanium.common.proto.user.GetNicknameResponse;
import hanium.common.proto.user.UserServiceGrpc;
import io.grpc.stub.StreamObserver;

public class FakeUserService extends UserServiceGrpc.UserServiceImplBase{

    @Override
    public void getNicknameByMemberId(GetNicknameRequest request,
                                      StreamObserver<GetNicknameResponse> responseObserver) {

        long memberId = request.getMemberId();
        if(memberId == 2L){
            var resp = GetNicknameResponse.newBuilder()
                    .setNickname("피키")
                    .build();

            responseObserver.onNext(resp);
            responseObserver.onCompleted();

    }else{
            responseObserver.onError(
                    io.grpc.Status.NOT_FOUND.withDescription("no user").asRuntimeException()
            );
        }

    }
}
