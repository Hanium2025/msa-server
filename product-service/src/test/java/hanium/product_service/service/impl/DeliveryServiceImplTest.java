package hanium.product_service.service.impl;

import hanium.product_service.domain.Chatroom;
import hanium.product_service.domain.Delivery;
import hanium.product_service.domain.Trade;
import hanium.product_service.domain.TradeStatus;
import hanium.product_service.dto.request.CreateWayBillRequestDTO;
import hanium.product_service.dto.response.DeliveryInfoResponseDTO;
import hanium.product_service.dto.response.DeliveryStatusSummaryDTO;
import hanium.product_service.repository.DeliveryRepository;
import hanium.product_service.repository.TradeRepository;
import hanium.product_service.util.SweetTrackerUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@DisplayName("배송 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {
    @Mock
    DeliveryRepository deliveryRepository;
    @Mock
    TradeRepository tradeRepository;
    @Mock
    SweetTrackerUtil sweetTrackerUtil;

    @InjectMocks
    DeliveryServiceImpl sut;

    @Test
    @DisplayName("송장 등록")
    void createWayBill() {
        long tradeId = 1L;
        long memberId = 100L;
        String code = "04";
        String invoiceNo = "123456789";
        long chatroomId = 200L;

        Chatroom mockChatroom = mock(Chatroom.class);
        Trade mockTrade = mock(Trade.class);

        // given
        CreateWayBillRequestDTO req = CreateWayBillRequestDTO.builder()
                .tradeId(tradeId)
                .memberId(memberId)
                .code(code)
                .invoiceNo(invoiceNo)
                .build();

        given(tradeRepository.findByIdAndDeletedAtIsNull(req.getTradeId())).willReturn(Optional.of(mockTrade));
        given(mockTrade.getSellerId()).willReturn(memberId);
        given(mockTrade.getChatroom()).willReturn(mockChatroom);
        given(mockChatroom.getId()).willReturn(chatroomId);

        //when
        sut.createWayBill(req);

        //then
        then(deliveryRepository).should().save(any(Delivery.class));
        then(tradeRepository).should(times(1)).updateStatus(chatroomId, TradeStatus.SHIPPED);
    }

    @Test
    @DisplayName("택배 조회")
    void getDeliveryInfo() {
        long tradeId = 1L;
        long memberId = 100L;
        String code = "04";
        String invoiceNo = "123456789";

        Trade mockTrade = mock(Trade.class);
        Delivery delivery = Delivery.builder()
                .trade(mockTrade)
                .code(code)
                .invoiceNo(invoiceNo)
                .build();

        List<DeliveryStatusSummaryDTO> fakeTrackingDetails = List.of(
                DeliveryStatusSummaryDTO.builder()
                        .time("2025-09-22 14:00:00")
                        .location("서울")
                        .status("배송완료")
                        .build()
        );

        // given
        given(deliveryRepository.findByTradeId(tradeId)).willReturn(Optional.of(delivery));
        given(sweetTrackerUtil.fetchTrackingInfo(code, invoiceNo)).willReturn(fakeTrackingDetails);

        // when
        DeliveryInfoResponseDTO response = sut.getDeliveryInfo(tradeId, memberId);

        // then
        then(deliveryRepository).should(times(1)).findByTradeId(tradeId);
        then(sweetTrackerUtil).should(times(1)).fetchTrackingInfo(code, invoiceNo);
        assertThat(response.getDeliveryStatus().get(0).getStatus()).isEqualTo("배송완료");
    }
}