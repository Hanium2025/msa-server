package hanium.product_service.service.impl;

import hanium.product_service.domain.Chatroom;
import hanium.product_service.domain.Product;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.dto.response.ProductResponseDTO;
import hanium.product_service.dto.response.ProfileResponseDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.ChatroomRepository;
import hanium.product_service.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


//단위 테스트 : Mock으로 외부 다 막고, 서비스 로직만 빠르게 검증
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    ChatroomRepository chatroomRepository;


    @InjectMocks
    ChatServiceImpl sut; //테스트 대상


    @Test
    void createChatroom_중복방없음_정상생성_닉네임_포함() {
        Long productId = 10L, senderId = 1L, receiverId = 2L;
        var req = CreateChatroomRequestDTO.builder().productId(productId).senderId(senderId)
                .receiverId(receiverId).build();
        //given
        given(chatroomRepository.findByProductIdAndMembers(productId, senderId, receiverId))
                .willReturn(Optional.empty());

        //저장 결과
        given(chatroomRepository.save(any(Chatroom.class)))
                .willAnswer(invocation -> {
                    Chatroom c = invocation.getArgument(0); // 실제로 save()에 들어온 객체
                    return Chatroom.builder()
                            .id(999L) // DB에서 채번된 것처럼 ID 넣기
                            .productId(c.getProductId())
                            .senderId(c.getSenderId())
                            .receiverId(c.getReceiverId())
                            .latestContentTime(c.getLatestContentTime())
                            .build();
                });

        //when
        CreateChatroomResponseDTO responseDTO = sut.createChatroom(req);


        //then
        assertThat(responseDTO.getChatroomId()).isEqualTo(999L);
        assertThat(responseDTO.getMessage()).isEqualTo("채팅방 생성 성공");

        then(chatroomRepository).should().findByProductIdAndMembers(productId, senderId, receiverId);


        var captor = ArgumentCaptor.forClass(Chatroom.class);
        then(chatroomRepository).should().save(captor.capture());
    }
}
