package hanium.product_service.service.impl;

import chat.Chat;
import chat.ChatServiceGrpc;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Chatroom;
import hanium.product_service.domain.Message;
import hanium.product_service.dto.request.ChatMessageRequestDTO;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.dto.response.GetMyChatroomResponseDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ChatRepository;
import hanium.product_service.repository.ChatroomRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.service.ChatMessageTxService;
import hanium.product_service.service.ChatService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatroomRepository chatroomRepository;
    private final ProductRepository productRepository;
    private final ProfileGrpcClient profileGrpcClient;
    private final ChatMessageTxService chatMessageTxService;
    // userId → StreamObserver 저장
    private final ConcurrentHashMap<Long, StreamObserver<Chat.ChatResponseMessage>> userStreamMap = new ConcurrentHashMap<>();

    @Transactional
    @Override
    public CreateChatroomResponseDTO createChatroom(CreateChatroomRequestDTO requestDTO) {
        Long productId = requestDTO.getProductId();
        Long senderId = requestDTO.getSenderId();
        Long receiverId = requestDTO.getReceiverId();
        // 1. 중복 채팅방 조회
        Optional<Chatroom> existing = chatroomRepository
                .findByProductIdAndSenderIdAndReceiverId(productId, senderId, receiverId);

        if (existing.isPresent()) {
            return new CreateChatroomResponseDTO(existing.get().getId(), "기존 채팅방입니다");
        }
        // 2. 상품명 가져오기
        String productName = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"))
                .getTitle();

        // 3. receiver 닉네임 조회 (gRPC call to user-service)
        String receiverNickname = profileGrpcClient.getNicknameByMemberId(receiverId);

        // 4. 채팅방 이름 생성
        String roomName = receiverNickname + "/" + productName;
        // 5. 채팅방 저장
        Chatroom chatroom = Chatroom.from(requestDTO, roomName);
        // 중복 채팅방 검사

        Chatroom saved = chatroomRepository.save(chatroom);
        return new CreateChatroomResponseDTO(saved.getId(), "채팅방 생성 성공");
    }

    @Override
    public StreamObserver<Chat.ChatMessage> chat(StreamObserver<Chat.ChatResponseMessage> responseObserver) {
        return new StreamObserver<>() {
            private Long currentUserId;

            @Override
            public void onNext(Chat.ChatMessage msg) {
                log.info("📥 product-service gRPC 수신: {}", msg);
                currentUserId = msg.getSenderId();
                userStreamMap.put(currentUserId, responseObserver);
                // grpc -> dto
                ChatMessageRequestDTO dto = ChatMessageRequestDTO.from(msg);

                Message saved = chatMessageTxService.handleMessage(dto);

                // 공통 응답 생성
                Chat.ChatResponseMessage response = Chat.ChatResponseMessage.newBuilder()
                        .setChatroomId(msg.getChatroomId())
                        .setSenderId(msg.getSenderId())
                        .setReceiverId(msg.getReceiverId())
                        .setContent(msg.getContent())
                        .setTimestamp(System.currentTimeMillis())
                        .setMessageId(saved.getId()) // DB 생성된 메시지 ID
                        .build();
                // 발신자 본인 세션에도 메시지 전달
                responseObserver.onNext(response);

                StreamObserver<Chat.ChatResponseMessage> receiverObserver = userStreamMap.get(msg.getReceiverId());
                if (receiverObserver != null) {
                    receiverObserver.onNext(response); // ← Gateway로 메시지 전달
                }
            }

            @Override
            public void onError(Throwable t) {
                userStreamMap.remove(currentUserId);
            }

            @Override
            public void onCompleted() {
                userStreamMap.remove(currentUserId);
                responseObserver.onCompleted();
            }
        };


    }

    @Transactional
    @Override
    public List<GetMyChatroomResponseDTO> getMyChatrooms(Long memberId) {
        List<Chatroom> rooms =
                chatroomRepository.findBySenderIdOrReceiverIdOrderByLatestContentTimeDesc(memberId, memberId);

        return rooms.stream().map(r -> {
            Long opponentId = r.getSenderId().equals(memberId) ? r.getReceiverId() : r.getSenderId();
            return GetMyChatroomResponseDTO.builder()
                    .chatroomId(r.getId())
                    .roomName(r.getRoomName())
                    .latestMessage(r.getLatestContent())
                    .latestTime(r.getLatestContentTime()) // LocalDateTime 그대로
                    .productId(r.getProductId())
                    .opponentId(opponentId)
                    .build();
        }).toList();
    }

//    @Transactional
//    @Override
//    public void updateLatest(Long chatroomId, String content, LocalDateTime time) {
//        Chatroom room = chatroomRepository.findById(chatroomId)
//                .orElseThrow(()->new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
//        room.updateLatest(content,time);
//
//
//    }


}
