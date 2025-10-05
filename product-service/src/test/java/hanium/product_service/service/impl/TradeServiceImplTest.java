//package hanium.product_service.service.impl;
//
//import hanium.common.proto.product.CompleteTradeResponse;
//import hanium.common.proto.product.TradeRequest;
//import hanium.common.proto.product.TradeResponse;
//import hanium.common.proto.product.TradeStatusResponse;
//import hanium.product_service.domain.TradeStatus;
//import hanium.product_service.dto.response.CompleteTradeInfoDTO;
//import hanium.product_service.dto.response.TradeInfoDTO;
//import hanium.product_service.grpc.ProductGrpcService;
//import hanium.product_service.repository.TradeRepository;
//import hanium.product_service.service.ChatService;
//import hanium.product_service.service.ProductService;
//import hanium.product_service.service.TradeService;
//import io.grpc.stub.StreamObserver;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.mockito.Mockito.*;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ActiveProfiles("test")
//@DisplayName("상품 거래 서비스 테스트")
//@ExtendWith(MockitoExtension.class)
//public class TradeServiceImplTest {
//
//    @Mock
//    private TradeRepository tradeRepository;
//    @Mock
//    private TradeService tradeService;
//    @Mock
//    private ChatService chatService;
//
//    @Mock
//    private ProductService productService;
//
//    //gRPC 응답 옵저버
//    @Mock
//    StreamObserver<TradeResponse> responseObserver;
//    @Mock
//    StreamObserver<TradeStatusResponse> TradeStatusResponseObserver;
//
//    @Mock
//    StreamObserver<CompleteTradeResponse> completeTradeResponseObserver;
//
//    @InjectMocks
//    ProductGrpcService productGrpcService;
//
//    private static final long CHATROOM_ID = 1L;
//    private static final long SELLER_ID = 1L;
//    private static final long TRADE_ID = 1L;
//    private static final long BUYER_ID = 2L;
//    private static final long PRODUCT_ID = 3L;
//
//    TradeInfoDTO tradeInfoDTO;
//    CompleteTradeInfoDTO completeTradeInfoDTO;
//
//    @BeforeEach
//    void setUp() {
//        tradeInfoDTO = TradeInfoDTO.builder().productId(PRODUCT_ID)
//                .sellerId(SELLER_ID).buyerId(BUYER_ID).build();
//    }
//
//    //직거래 요청
//    @Test
//    @DisplayName("SELLING 상태면 tradeService.directTrade가 호출되고, 응답에 opponentId 가 sellerId로 설정된다.")
//    void request_Direct_Trade_when_StatusSelling_createsTrade_andReturnOpponenetId() {
//        //given
//        TradeRequest request = TradeRequest.newBuilder()
//                .setChatroomId(CHATROOM_ID)
//                .setMemberId(BUYER_ID)
//                .build();
//
//        when(chatService.getTradeInfoByChatroomIdAndMemberId(CHATROOM_ID, BUYER_ID))
//                .thenReturn(tradeInfoDTO);
//        when(productService.getProductStatusById(PRODUCT_ID)).thenReturn("SELLING");
//
//        ArgumentCaptor<TradeResponse> responseCaptor = ArgumentCaptor.forClass(TradeResponse.class);
//
//        //when
//        productGrpcService.directTrade(request, responseObserver);
//
//        //then
//        verify(tradeService, times(1)).directTrade(eq(CHATROOM_ID), eq(tradeInfoDTO));
//        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
//
//        TradeResponse response = responseCaptor.getValue();
//        assertThat(response.getOpponentId()).isEqualTo(SELLER_ID);
//        verify(responseObserver, times(1)).onCompleted();
//
//    }
//
//    //택배 거래 요청
//    @Test
//    @DisplayName("SELLING 상태면 tradeService.parcelTrade가 호출되고, 응답에 opponentId 가 sellerId로 설정된다.")
//    void request_Parcel_Trade_when_StatusSelling_createsTrade_andReturnOpponenetId() {
//        //given
//        TradeRequest request = TradeRequest.newBuilder()
//                .setChatroomId(CHATROOM_ID)
//                .setMemberId(BUYER_ID)
//                .build();
//
//        when(chatService.getTradeInfoByChatroomIdAndMemberId(CHATROOM_ID, BUYER_ID))
//                .thenReturn(tradeInfoDTO);
//        when(productService.getProductStatusById(PRODUCT_ID)).thenReturn("SELLING");
//
//        ArgumentCaptor<TradeResponse> responseCaptor = ArgumentCaptor.forClass(TradeResponse.class);
//
//        //when
//        productGrpcService.parcelTrade(request, responseObserver);
//
//        //then
//        verify(tradeService, times(1)).parcelTrade(eq(CHATROOM_ID), eq(tradeInfoDTO));
//        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
//
//        TradeResponse response = responseCaptor.getValue();
//        assertThat(response.getOpponentId()).isEqualTo(SELLER_ID);
//        verify(responseObserver, times(1)).onCompleted();
//
//    }
//
//    //직거래 수락
//    @Test
//    @DisplayName("SELLING이면 tradeService.acceptDirectTrade(chatroomId) 호출 + productService.updateProductStatusById(productId) 호출 + opponentId = buyerId")
//    void accept_directTrade_withStatusSELLING() {
//        //given
//        TradeRequest request = TradeRequest.newBuilder()
//                .setChatroomId(CHATROOM_ID)
//                .setMemberId(SELLER_ID)
//                .build();
//
//
//        when(chatService.getTradeInfoByChatroomIdAndMemberId(CHATROOM_ID, SELLER_ID))
//                .thenReturn(tradeInfoDTO);
//        when(productService.getProductStatusById(PRODUCT_ID)).thenReturn("SELLING");
//
//
//        ArgumentCaptor<TradeResponse> responseCaptor = ArgumentCaptor.forClass(TradeResponse.class);
//
//        productGrpcService.acceptDirectTrade(request, responseObserver);
//
//        verify(tradeService).acceptDirectTrade(CHATROOM_ID);
//        verify(productService).updateProductStatusById(PRODUCT_ID);
//        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
//        verify(responseObserver, times(1)).onCompleted();
//
//        assertThat(responseCaptor.getValue().getOpponentId()).isEqualTo(BUYER_ID);
//    }
//
//
//    //택배 거래 수락
//    @Test
//    @DisplayName("SELLING이면 tradeService.acceptParcelTrade(chatroomId) 호출 + productService.updateProductStatusById(productId) 호출 + opponentId = buyerId")
//    void accept_parcelTrade_withStatusSELLING() {
//        //given
//        TradeRequest request = TradeRequest.newBuilder()
//                .setChatroomId(CHATROOM_ID)
//                .setMemberId(SELLER_ID)
//                .build();
//
//
//        when(chatService.getTradeInfoByChatroomIdAndMemberId(CHATROOM_ID, SELLER_ID))
//                .thenReturn(tradeInfoDTO);
//        when(productService.getProductStatusById(PRODUCT_ID)).thenReturn("SELLING");
//
//
//        ArgumentCaptor<TradeResponse> responseCaptor = ArgumentCaptor.forClass(TradeResponse.class);
//
//        productGrpcService.acceptParcelTrade(request, responseObserver);
//
//        verify(tradeService).acceptParcelTrade(CHATROOM_ID);
//        verify(productService).updateProductStatusById(PRODUCT_ID);
//        verify(responseObserver, times(1)).onNext(responseCaptor.capture());
//        verify(responseObserver, times(1)).onCompleted();
//
//        assertThat(responseCaptor.getValue().getOpponentId()).isEqualTo(BUYER_ID);
//    }
//
//    @Test
//    @DisplayName("거래 상태 조회")
//    void getTradeStatus_returnsStatus() {
//        TradeRequest request = TradeRequest.newBuilder()
//                .setChatroomId(CHATROOM_ID)
//                .setMemberId(BUYER_ID)
//                .build();
//        when(tradeService.getTradeStatus(CHATROOM_ID, BUYER_ID))
//                .thenReturn(TradeStatus.REQUESTED);
//
//        ArgumentCaptor<TradeStatusResponse> responseCaptor = ArgumentCaptor.forClass(TradeStatusResponse.class);
//        productGrpcService.getTradeStatus(request, TradeStatusResponseObserver);
//
//        verify(tradeService, times(1)).getTradeStatus(CHATROOM_ID, BUYER_ID);
//        verify(TradeStatusResponseObserver, times(1)).onNext(responseCaptor.capture());
//        verify(TradeStatusResponseObserver, times(1)).onCompleted();
//
//        assertThat(responseCaptor.getValue().getStatus()).isEqualTo(TradeStatus.REQUESTED.name());
//    }
//
//    //거래 완료
//    @Test
//    @DisplayName("거래 완료")
//    void completeTrade() {
//       //given
//        TradeRequest request = TradeRequest.newBuilder()
//                .setChatroomId(CHATROOM_ID)
//                .setMemberId(BUYER_ID)
//                .build();
//
//        CompleteTradeInfoDTO completeTradeInfoDTO = CompleteTradeInfoDTO.builder().tradeId(TRADE_ID).productId(PRODUCT_ID).opponentId(SELLER_ID).build();
//
//
//        when(tradeService.completeTrade(CHATROOM_ID, BUYER_ID)).thenReturn(completeTradeInfoDTO);
//
//        ArgumentCaptor<CompleteTradeResponse> responseCaptor = ArgumentCaptor.forClass(CompleteTradeResponse.class);
//
//        //when
//        productGrpcService.completeTrade(request, completeTradeResponseObserver);
//
//        //then
//        verify(tradeService, times(1)).completeTrade(CHATROOM_ID, BUYER_ID);
//        verify(completeTradeResponseObserver, times(1)).onNext(responseCaptor.capture());
//        verify(completeTradeResponseObserver, times(1)).onCompleted();
//        assertThat(responseCaptor.getValue().getOpponentId()).isEqualTo(SELLER_ID);
//        assertThat(responseCaptor.getValue().getTradeId()).isEqualTo(TRADE_ID);
//
//    }
//}
