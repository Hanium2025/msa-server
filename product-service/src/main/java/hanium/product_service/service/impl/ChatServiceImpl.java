package hanium.product_service.service.impl;

import chat.Chat;
import hanium.product_service.domain.Chatroom;
import hanium.product_service.domain.Message;
import hanium.product_service.domain.MessageImage;
import hanium.product_service.domain.MessageType;
import hanium.product_service.dto.request.ChatMessageRequestDTO;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.ChatMessageResponseDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.dto.response.GetMyChatroomResponseDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ChatroomRepository;
import hanium.product_service.repository.MessageImageRepository;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.service.ChatMessageTxService;
import hanium.product_service.service.ChatService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatroomRepository chatroomRepository;
    private final ProductRepository productRepository;
    private final ProfileGrpcClient profileGrpcClient;
    private final ChatMessageTxService chatMessageTxService;
    private final ChatRepository chatRepository;
    private final MessageImageRepository messageImageRepository;
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

            @Override
            public void onNext(Chat.ChatMessage msg) {
                log.info("📥 product-service gRPC 수신: {}", msg);
                // grpc -> dto
                ChatMessageRequestDTO dto = ChatMessageRequestDTO.from(msg);

                Message saved = chatMessageTxService.handleMessage(dto);

                long tsMillis = (saved.getCreatedAt() != null)
                        ? saved.getCreatedAt()
                        .atZone(ZoneId.of("Asia/Seoul"))   // 원하는 타임존
                        .toInstant()
                        .toEpochMilli()
                        : System.currentTimeMillis();
                // 공통 응답 생성
                Chat.ChatResponseMessage response = Chat.ChatResponseMessage.newBuilder()
                        .setChatroomId(msg.getChatroomId())
                        .setSenderId(msg.getSenderId())
                        .setReceiverId(msg.getReceiverId())
                        .setContent(msg.getContent())
                        .setTimestamp(tsMillis)
                        .setMessageId(saved.getId()) // DB 생성된 메시지 ID
                        .setType(msg.getType())
                        .addAllImageUrls(msg.getImageUrlsList())
                        .build();

                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                log.warn("gRPC stream onError", t);
            }

            @Override
            public void onCompleted() {
                log.info("gRPC stream completed");
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

    @Override
    public List<ChatMessageResponseDTO> getAllMessageByChatroomId(Long chatroomId) {

        List<Message> messages = chatRepository.findAllByChatroomIdOrderByCreatedAtAsc(chatroomId);

        List<Long> ids = messages.stream().map(Message::getId).toList();
        List<MessageImage> images = messageImageRepository.findAllByMessageIdIn(ids);

        Map<Long, List<String>> imageMap = new LinkedHashMap<>();

        for (MessageImage mi : images) {
            if (mi == null || mi.getMessage() == null)
                continue;
            Long messageId = mi.getMessage().getId(); //해당 이미지가 가지고 있는 id
            String url = mi.getImageUrl();   //해당 이미지의 url

            if (url == null || url.isBlank())
                continue;

            List<String> bucket = imageMap.get(messageId); //해당 메시지로 만들어진 bucket이 있는지 확인

            if (bucket == null) { //없다면 ArrayList만들고 넣어주기
                bucket = new ArrayList<>();
                imageMap.put(messageId, bucket);
            }
            bucket.add(url);
        }
        List<ChatMessageResponseDTO> dtos = new ArrayList<>(messages.size());

        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            if (m == null)
                continue;


            String type = (m.getMessageType() != null ? m.getMessageType() : MessageType.TEXT).name();

            List<String> imagesUrls = imageMap.get(m.getId());
            if (imagesUrls == null) {
                imagesUrls = List.of();
            }
            long timestamp = 0L;
            if (m.getCreatedAt() != null) {
                timestamp = m.getCreatedAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
            }


            ChatMessageResponseDTO dto = ChatMessageResponseDTO.builder()
                    .messageId(m.getId())
                    .chatroomId(m.getChatroom().getId())
                    .senderId(m.getSenderId())
                    .receiverId(m.getReceiverId())
                    .content(m.getContent())
                    .timestamp(timestamp)
                    .type(type)
                    .imageUrls(imagesUrls)
                    .build();

            dtos.add(dto);

        }

        return dtos;
    }

}
