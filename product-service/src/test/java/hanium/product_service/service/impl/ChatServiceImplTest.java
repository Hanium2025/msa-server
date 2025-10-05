package hanium.product_service.service.impl;

import hanium.common.proto.product.*;
import hanium.common.proto.product.MessageType;
import hanium.product_service.domain.*;

import hanium.product_service.domain.Product;
import hanium.product_service.dto.request.ChatMessageRequestDTO;
import hanium.product_service.dto.request.CreateChatroomRequestDTO;
import hanium.product_service.dto.response.ChatMessageResponseDTO;
import hanium.product_service.dto.response.CreateChatroomResponseDTO;
import hanium.product_service.dto.response.GetMyChatroomResponseDTO;
import hanium.product_service.dto.response.ProfileResponseDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.*;


import hanium.product_service.service.ChatMessageTxService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static hanium.product_service.domain.MessageType.IMAGE;
import static hanium.product_service.domain.MessageType.TEXT;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


//단위 테스트 : Mock으로 외부 다 막고, 서비스 로직만 빠르게 검증
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    @Mock
    ChatroomRepository chatroomRepository;
    @Mock
    ChatRepository chatRepository;
    @Mock
    MessageImageRepository messageImageRepository;
    @Mock
    ChatMessageTxService chatMessageTxService;

    @Mock
    ProfileGrpcClient profileGrpcClient;
    @Mock
    ProductRepository productRepository;
    @Mock PlatformTransactionManager txm;
    @InjectMocks
    ChatServiceImpl sut; //테스트 대상

    @Mock ChatroomUpsertDao chatroomUpsertDao;

    @Test
    @DisplayName("createChatroom: UPSERT로 id 반환(멱등 엔드포인트)")
    void createChatroom_upsert_returnsId() {
        long productId = 10L, senderId = 1L, receiverId = 2L;
        var req = CreateChatroomRequestDTO.builder()
                .productId(productId).senderId(senderId).receiverId(receiverId).build();

        given(chatroomUpsertDao.upsertAndGetId(productId, senderId, receiverId)).willReturn(777L);

        var res = sut.createChatroom(req);

        assertThat(res.getChatroomId()).isEqualTo(777L);
        assertThat(res.getMessage()).isNotBlank(); // 메시지는 고정 문구여도 OK
        verify(chatroomUpsertDao).upsertAndGetId(productId, senderId, receiverId);
        verifyNoInteractions(chatroomRepository); // 선택
    }

    @Test
    @DisplayName("createChatroom: 같은 키로 2번 호출해도 동일 ID 반환(멱등)")
    void createChatroom_doubleCall_sameId() {
        long productId = 10L, senderId = 1L, receiverId = 2L;
        var req = CreateChatroomRequestDTO.builder()
                .productId(productId).senderId(senderId).receiverId(receiverId).build();

        given(chatroomUpsertDao.upsertAndGetId(productId, senderId, receiverId)).willReturn(777L);

        var r1 = sut.createChatroom(req);
        var r2 = sut.createChatroom(req);

        assertThat(r1.getChatroomId()).isEqualTo(777L);
        assertThat(r2.getChatroomId()).isEqualTo(777L);
        verify(chatroomUpsertDao, times(2)).upsertAndGetId(productId, senderId, receiverId);
    }


    @Test
    @DisplayName("getAllMessageByChatroomId : 특정 채팅방의 모든 메시지 조회")
    void getAllMessageByChatroomId_returnDtosWithImagesAndTimestamps() {

        Long chatroomId = 123L;
        Chatroom chatroom = Chatroom.builder().id(chatroomId).build();

        //Message는 구체 클래스로 가정
        Message m1 = mock(Message.class);
        Message m2 = mock(Message.class);


        //공통 시각
        LocalDateTime t1 = LocalDateTime.of(2025, 8, 31, 12, 0);
        LocalDateTime t2 = LocalDateTime.of(2025, 8, 31, 13, 30);

        given(m1.getId()).willReturn(1L);
        given(m1.getChatroom()).willReturn(chatroom);
        given(m1.getSenderId()).willReturn(10L);
        given(m1.getReceiverId()).willReturn(20L);
        given(m1.getContent()).willReturn("hello");
        given(m1.getMessageType()).willReturn(TEXT);
        given(m1.getCreatedAt()).willReturn(t1);

        given(m2.getId()).willReturn(2L);
        given(m2.getChatroom()).willReturn(chatroom);
        given(m2.getSenderId()).willReturn(20L);
        given(m2.getReceiverId()).willReturn(10L);
        given(m2.getContent()).willReturn("사진 보냄");
        given(m2.getMessageType()).willReturn(IMAGE);
        given(m2.getCreatedAt()).willReturn(t2);

        given(chatRepository.findAllByChatroomIdOrderByCreatedAtAsc(chatroomId))
                .willReturn(List.of(m1, m2));

        //MessageImage mock
        MessageImage mi1 = mock(MessageImage.class);
        MessageImage mi2 = mock(MessageImage.class);

        Message msgRef = m2;

        given(mi1.getMessage()).willReturn(msgRef);
        given(mi1.getImageUrl()).willReturn("https://s3/img-1");
        given(mi2.getMessage()).willReturn(msgRef);
        given(mi2.getImageUrl()).willReturn("https://s3/img-2");


        given(messageImageRepository.findAllByMessageIdIn(List.of(1L, 2L)))
                .willReturn(List.of(mi1, mi2));

        //when
        List<ChatMessageResponseDTO> dtos = sut.getAllMessageByChatroomId(chatroomId);

        //then
        assertThat(dtos).hasSize(2);

        ChatMessageResponseDTO d1 = dtos.get(0);
        ChatMessageResponseDTO d2 = dtos.get(1);

        assertThat(d1.getChatroomId()).isEqualTo(chatroomId);
        assertThat(d1.getMessageId()).isEqualTo(1L);
        assertThat(d1.getSenderId()).isEqualTo(10L);
        assertThat(d1.getReceiverId()).isEqualTo(20L);
        assertThat(d1.getContent()).isEqualTo("hello");
        assertThat(d1.getType()).isEqualTo("TEXT");

        long ts1 = t1.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        assertThat(d1.getTimestamp()).isEqualTo(ts1);
        assertThat(d2.getChatroomId()).isEqualTo(chatroomId);
        assertThat(d2.getMessageId()).isEqualTo(2L);
        assertThat(d2.getSenderId()).isEqualTo(20L);
        assertThat(d2.getReceiverId()).isEqualTo(10L);
        assertThat(d2.getContent()).isEqualTo("사진 보냄");
        assertThat(d2.getType()).isEqualTo("IMAGE");
        assertThat(d2.getImageUrls()).containsExactly(
                "https://s3/img-1", "https://s3/img-2"
        );
        long ts2 = t2.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        assertThat(d2.getTimestamp()).isEqualTo(ts2);

        then(chatRepository).should().findAllByChatroomIdOrderByCreatedAtAsc(chatroomId);
        then(messageImageRepository).should().findAllByMessageIdIn(List.of(1L, 2L));


    }

    @Test
    @DisplayName("getMyChatrooms: 내가 참여한 채팅방 리스트 조회")
    void getMyChatrooms() {
        Long memberId = 10L; //내 아이디

        Chatroom chatroom1 = mock(Chatroom.class);
        Chatroom chatroom2 = mock(Chatroom.class);
        //내가 보낸 채팅방 상대방 : 20L
        given(chatroom1.getId()).willReturn(100L);
        given(chatroom1.getSenderId()).willReturn(10L);
        given(chatroom1.getReceiverId()).willReturn(20L);
        given(chatroom1.getProductId()).willReturn(1000L);
        given(chatroom1.getLatestContent()).willReturn("안녕하세요");

        //내가 받은 채팅방 상대방: 30L
        given(chatroom2.getId()).willReturn(101L);
        given(chatroom2.getSenderId()).willReturn(30L);
        given(chatroom2.getProductId()).willReturn(1001L);
        given(chatroom2.getLatestContent()).willReturn("안녕하세요2");

        //최신순 정렬
        given(chatroomRepository.findBySenderIdOrReceiverIdOrderByLatestContentTimeDesc(memberId, memberId))
                .willReturn(List.of(chatroom2, chatroom1));
        //내가 보낸 채팅방의 상대방 아이디 20L
        ProfileResponseDTO profileResponseDTO = ProfileResponseDTO.builder()
                .nickname("프로필1")
                .profileImageUrl("https://s3/profile1").build();

        given(profileGrpcClient.getProfileByMemberId(20L))
                .willReturn(profileResponseDTO);

        //내가 받음 채팅방 의상대 30L
        ProfileResponseDTO profileResponseDTO2 = ProfileResponseDTO.builder()
                .nickname("프로필3")
                .profileImageUrl("https://s3/profile3").build();

        given(profileGrpcClient.getProfileByMemberId(30L))
                .willReturn(profileResponseDTO2);

        // 상품 타이틀
        Product product1 = mock(Product.class);
        given(product1.getTitle())
                .willReturn("유모차");
        given(productRepository.findByIdAndDeletedAtIsNull(1000L))
                .willReturn(Optional.of(product1));


        Product product2 = mock(Product.class);
        given(product2.getTitle()).willReturn("유모차3");
        given(productRepository.findByIdAndDeletedAtIsNull(1001L))
                .willReturn(Optional.of(product2));


        //when
        List<GetMyChatroomResponseDTO> dtos = sut.getMyChatrooms(memberId);
        GetMyChatroomResponseDTO d2 = dtos.get(0);  //chatroom2
        GetMyChatroomResponseDTO d1 = dtos.get(1); //chatroom1

        //then
        assertThat(d2.getChatroomId()).isEqualTo(chatroom2.getId());
        assertThat(d2.getOpponentId()).isEqualTo(chatroom2.getSenderId());
        assertThat(d2.getOpponentNickname()).isEqualTo("프로필3");
        assertThat(d2.getProductId()).isEqualTo(chatroom2.getProductId());
        assertThat(d2.getRoomName()).isEqualTo("프로필3/유모차3");

        assertThat(d1.getChatroomId()).isEqualTo(chatroom1.getId());
        assertThat(d1.getOpponentId()).isEqualTo(chatroom1.getReceiverId());
        assertThat(d1.getOpponentNickname()).isEqualTo("프로필1");
        assertThat(d1.getProductId()).isEqualTo(chatroom1.getProductId());
        assertThat(d1.getRoomName()).isEqualTo("프로필1/유모차");


        then(chatroomRepository).should()
                .findBySenderIdOrReceiverIdOrderByLatestContentTimeDesc(memberId, memberId);
        then(profileGrpcClient).should().getProfileByMemberId(20L);
        then(profileGrpcClient).should().getProfileByMemberId(30L);
        then(productRepository).should().findByIdAndDeletedAtIsNull(1000L);
        then(productRepository).should().findByIdAndDeletedAtIsNull(1001L);
    }

    @Test
    @DisplayName("chat(): onNext 수신 시 DB 저장 -> responseObserver.onNext 호출")
    void chat_onNext() {

        @SuppressWarnings("unchecked")
        StreamObserver<ChatResponseMessage> responseObserver = mock(StreamObserver.class);

        StreamObserver<ChatMessage> serverObserver = sut.chat(responseObserver);

        ChatMessage msg = ChatMessage.newBuilder()
                .setChatroomId(999L)
                .setContent("안녕하세요")
                .setSenderId(1L)
                .setReceiverId(2L)
                .setType(MessageType.TEXT)
                .addAllImageUrls(List.of())
                .build();

        Message saved = mock(Message.class);
        given(saved.getId()).willReturn(123L);
        given(saved.getCreatedAt()).willReturn(LocalDateTime.of(2025, 8, 31, 14, 0));
        given(chatMessageTxService.handleMessage(any(ChatMessageRequestDTO.class)))
                .willReturn(saved);

        //when 서버가 메시지 수신
        serverObserver.onNext(msg);

        //then
        ArgumentCaptor<ChatResponseMessage> resCap = ArgumentCaptor.forClass(ChatResponseMessage.class);
        then(responseObserver).should().onNext(resCap.capture());

        ChatResponseMessage responseMessage = resCap.getValue();
        assertThat(responseMessage.getMessageId()).isEqualTo(123L);
        assertThat(responseMessage.getSenderId()).isEqualTo(1L);
        assertThat(responseMessage.getReceiverId()).isEqualTo(2L);
        assertThat(responseMessage.getChatroomId()).isEqualTo(999L);
        assertThat(responseMessage.getContent()).isEqualTo("안녕하세요");
        assertThat(responseMessage.getType()).isEqualTo(MessageType.TEXT);


        serverObserver.onCompleted();
        then(responseObserver).should().onCompleted();
        ;


    }




}
