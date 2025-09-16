package hanium.product_service.service.impl;

import hanium.product_service.domain.Product;
import hanium.product_service.domain.Trade;
import hanium.product_service.domain.TradeReview;
import hanium.product_service.dto.request.TradeReviewRequestDTO;
import hanium.product_service.dto.response.ProfileResponseDTO;
import hanium.product_service.dto.response.TradeReviewPageDTO;
import hanium.product_service.grpc.ProfileGrpcClient;
import hanium.product_service.repository.TradeRepository;
import hanium.product_service.repository.TradeReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@DisplayName("거래 평가 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class TradeReviewServiceImplTest {

    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private TradeReviewRepository tradeReviewRepository;
    @Mock
    private ProfileGrpcClient profileGrpcClient;

    @InjectMocks
    private TradeReviewServiceImpl sut;

    @Test
    @DisplayName("첫 진입 시 거래 평가 페이지 정보 조회")
    void getTradeReviewPageInfo() {
        // given
        Long tradeId = 1L;
        Long memberId = 100L;
        Long targetMemberId = 200L;

        Product mockProduct = mock(Product.class);
        given(mockProduct.getTitle()).willReturn("유모차");

        Trade mockTrade = mock(Trade.class);
        given(mockTrade.getProduct()).willReturn(mockProduct);
        given(mockTrade.getOtherParty(memberId)).willReturn(targetMemberId);
        given(tradeRepository.findByIdWithProduct(tradeId)).willReturn(Optional.of(mockTrade));

        ProfileResponseDTO mockProfile = ProfileResponseDTO.builder().nickname("거래상대방닉네임").build();
        given(profileGrpcClient.getProfileByMemberId(targetMemberId)).willReturn(mockProfile);

        // when
        TradeReviewPageDTO result = sut.getTradeReviewPageInfo(tradeId, memberId);

        // then
        assertThat(result.getTitle()).isEqualTo("유모차");
        assertThat(result.getNickname()).isEqualTo("거래상대방닉네임");
        then(tradeRepository).should().findByIdWithProduct(tradeId);
        then(profileGrpcClient).should().getProfileByMemberId(targetMemberId);
    }

    @Test
    @DisplayName("거래 평가 등록")
    void tradeReview() {
        // given
        TradeReviewRequestDTO dto = TradeReviewRequestDTO.builder()
                .tradeId(1L)
                .memberId(100L)
                .rating(5.0)
                .comment("완전 친절!!")
                .build();

        Trade mockTrade = mock(Trade.class);
        given(tradeRepository.findByIdAndDeletedAtIsNull(dto.getTradeId())).willReturn(Optional.of(mockTrade));
        given(tradeReviewRepository.existsByTradeIdAndMemberId(dto.getTradeId(), dto.getMemberId())).willReturn(false);

        // when
        sut.tradeReview(dto);

        // then
        then(tradeRepository).should().findByIdAndDeletedAtIsNull(dto.getTradeId());
        then(tradeReviewRepository).should().existsByTradeIdAndMemberId(dto.getTradeId(), dto.getMemberId());
        then(tradeReviewRepository).should().save(any(TradeReview.class));
    }
}