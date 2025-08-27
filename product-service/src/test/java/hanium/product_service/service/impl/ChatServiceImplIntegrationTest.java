//package hanium.product_service.service.impl;
//
//import hanium.product_service.domain.Category;
//import hanium.product_service.domain.Chatroom;
//import hanium.product_service.domain.Product;
//import hanium.product_service.domain.Status;
//import hanium.product_service.dto.request.CreateChatroomRequestDTO;
//import hanium.product_service.dto.response.CreateChatroomResponseDTO;
//import hanium.product_service.repository.ChatroomRepository;
//import hanium.product_service.repository.ProductRepository;
//import hanium.product_service.service.FakeUserService;
//import io.grpc.Server;
//import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
////통합 테스트
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@ActiveProfiles("test")
//public class ChatServiceImplIntegrationTest {
//
//    //가짜 user-service grpc서버
//    static Server userServer;
//    static int userPort;
//
//    //테스트 시작 전에 Netty gRPC 서버 띄우기
//    @BeforeAll
//    static void startFakeUserService() throws Exception{
//        userServer = NettyServerBuilder.forPort(0)//임의 포트 할당
//                .addService(new FakeUserService()) // memberId=2 → "피키" 반환하도록 구현
//                .build()
//                .start();
//        userPort = userServer.getPort();
//
//    }
//
//    @AfterAll
//    static void stopFakeUserService(){
//        if(userServer!= null){
//            userServer.shutdownNow();
//        }
//    }
//
//    //테스트 컨텍스트가 뜰 때 동적으로 스프링 프로퍼티를 주입하는 훅
//    @DynamicPropertySource
//    static void grpcClientProps(DynamicPropertyRegistry r) {
//        r.add("grpc.client.user-service.address",
//                () -> "static://127.0.0.1:" + userPort);  //유레카 사용 X -> 고정 주소 설정
//        r.add("grpc.client.user-service.negotiationType",
//                () -> "PLAINTEXT");
//        // discovery/eureka를 쓰고 있다면 test에선 끄는게 안전
//        r.add("eureka.client.enabled", () -> "false");
//    }
//
//    @Autowired
//    ChatServiceImpl chatService;
//
//    @Autowired
//    ProductRepository productRepository;
//    @Autowired
//    ChatroomRepository chatroomRepository;
//
//    private Product makeProduct(String title){
//        return Product.builder()
//                .title(title)
//                .content("설명")
//                .price(2_000_000L)
//                .sellerId(1L)
//                .status(Status.SELLING)
//                .category(Category.ELECTRONICS)
//                .build();
//    }
//
//
//    @Test
//    void createChatroom_통합_가짜유저서비스호출_닉네임반영(){
//
//        //given
//        Product p = makeProduct("맥북프로");
//        p = productRepository.save(p);
//        Long productId = p.getId();
//
//        Long senderId = 1L;
//        Long receiverId = 2L;
//
//        CreateChatroomRequestDTO req = CreateChatroomRequestDTO.builder()
//                .productId(productId)
//                .senderId(senderId)
//                .receiverId(receiverId)
//                .build();
//
//        //when
//
//        CreateChatroomResponseDTO resp = chatService.createChatroom(req);
//
//        //then
//        assertThat(resp.getChatroomId()).isNotNull();
//        assertThat(resp.getMessage()).isEqualTo("채팅방 생성 성공");
//
//
//        // 저장된 Chatroom 검증 (roomName = "피키/맥북프로")
//        Optional<Chatroom> saved = chatroomRepository.findById(resp.getChatroomId());
//        assertThat(saved).isPresent();
//        assertThat(saved.get().getRoomName()).isEqualTo("피키/맥북프로");
//        assertThat(saved.get().getProductId()).isEqualTo(productId);
//        assertThat(saved.get().getSenderId()).isEqualTo(senderId);
//        assertThat(saved.get().getReceiverId()).isEqualTo(receiverId);
//
//
//
//    }
//
//
//}
