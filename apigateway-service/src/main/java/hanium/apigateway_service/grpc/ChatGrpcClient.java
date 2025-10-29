package hanium.apigateway_service.grpc;

import hanium.apigateway_service.dto.chat.response.ChatMessageResponseDTO;
import hanium.apigateway_service.dto.chat.response.ChatMessagesCursorDTO;
import hanium.apigateway_service.mapper.ChatMessageMapperForGateway;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import hanium.common.proto.product.*;
@Service
@RequiredArgsConstructor
public class ChatGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    //채팅방 별 메시지 가져오기
    public List<ChatMessageResponseDTO> getAllMessagesByChatroomId(Long chatroomId) {
        GetAllMessagesByChatroomIdRequest requestChatroomId = ChatMessageMapperForGateway.chatroomIdToGrpc(chatroomId);
        GetAllMessagesByChatroomResponse response = stub.getAllMessagesByChatroomId(requestChatroomId);
        return ChatMessageResponseDTO.from(response);

    }

    /**
     * 🔹 커서 기반 메시지 조회 (페이징)
     *
     * @param chatroomId 채팅방 ID
     * @param cursor     마지막 커서 (null이면 최신부터)
     * @param limit      조회 개수
     * @param isAfter    true면 새로운 쪽(AFTER), false면 과거 쪽(BEFORE)
     */
    public ChatMessagesCursorDTO getMessagesByCursor(Long chatroomId, String cursor, int limit, boolean isAfter) {

        // limit
        int pageSize = (limit <= 0) ? 20 : Math.min(limit, 200);

        GetMessagesByCursorRequest.Direction direction =
                isAfter ? GetMessagesByCursorRequest.Direction.AFTER
                        : GetMessagesByCursorRequest.Direction.BEFORE;

        GetMessagesByCursorRequest request = GetMessagesByCursorRequest.newBuilder()
                .setChatRoomId(chatroomId)
                .setCursor(cursor == null ? "" : cursor)
                .setLimit(limit)
                .setDirection(direction)
                .build();

        GetMessagesByCursorResponse response = stub.getAllMessagesByCursor(request);
        // 메시지 리스트 변환
        List<ChatMessageResponseDTO> list = response.getChatResponseMessageList().stream()
                .map(m -> ChatMessageResponseDTO.builder()
                        .messageId(m.getMessageId())
                        .chatroomId(m.getChatroomId())
                        .senderId(m.getSenderId())
                        .receiverId(m.getReceiverId())
                        .content(m.getContent())
                        .timestamp(m.getTimestamp())
                        .type(m.getType().name())
                        .imageUrl(m.getImageUrlsList())
                        .build())
                .toList();
        // gRPC 응답을 DTO 리스트로 변환
        // 커서 + 리스트 묶어서 반환
        return ChatMessagesCursorDTO.builder()
                .messages(list)
                .nextCursor(response.getNextCursor())
                .prevCursor(response.getPrevCursor())
                .hasMore(response.getHasMore())
                .build();
    }
}

