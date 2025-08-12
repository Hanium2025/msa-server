package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.Chatroom;
import hanium.product_service.domain.Message;
import hanium.product_service.dto.request.ChatMessageRequestDTO;
import hanium.product_service.repository.ChatRepository;
import hanium.product_service.repository.ChatroomRepository;
import hanium.product_service.service.ChatMessageTxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageTxServiceImpl implements ChatMessageTxService {

    public final ChatroomRepository chatroomRepository;
    public final ChatRepository chatRepository;

    @Transactional
    @Override
    public Message handleMessage(ChatMessageRequestDTO dto) {
        Message message = Message.from(dto); //엔티티로 바꿈
        Message saved = chatRepository.save(message); // 여기서 createdAt 세팅

        Chatroom room = chatroomRepository.findById(dto.getChatroomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        room.updateLatest(message.getContent(), saved.getCreatedAt() != null ? saved.getCreatedAt() : LocalDateTime.now());

        return saved;
    }
}
