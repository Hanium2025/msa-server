package hanium.product_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.product_service.domain.*;
import hanium.product_service.dto.response.CompleteTradeInfoDTO;
import hanium.product_service.dto.response.TradeInfoDTO;
import hanium.product_service.dto.response.TradeStatusDTO;
import hanium.product_service.repository.ProductRepository;
import hanium.product_service.repository.TradeCompleteRepository;
import hanium.product_service.repository.TradeRepository;
import hanium.product_service.repository.TradeStatusRepository;
import hanium.product_service.service.ProductService;
import hanium.product_service.service.TradeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeService {
    private final ProductRepository productRepository;
    private final ProductServiceImpl productServiceImpl;
    private final TradeStatusRepository tradeStatusRepository;
    @PersistenceContext
    private EntityManager em;

    private final TradeRepository tradeRepository;
    private final ProductService productService;
    private final TradeCompleteRepository tradeCompleteRepository;

    //직거래 요청
    @Transactional
    @Override
    public void directTrade(Long chatroomId, TradeInfoDTO tradeInfoDTO) {
        // 판매 처리

        Product productRef = em.getReference(Product.class, tradeInfoDTO.getProductId());
        Chatroom chatroomRef = em.getReference(Chatroom.class, chatroomId);

        Trade trade = Trade.builder()
                .buyerId(tradeInfoDTO.getBuyerId())
                .sellerId(tradeInfoDTO.getSellerId())
                .product(productRef)
                .type(TradeType.DIRECT)
                .tradeStatus(TradeStatus.REQUESTED)
                .chatroom(chatroomRef)
                .build();
        tradeRepository.save(trade);
    }

    //거래 수락
    @Transactional
    @Override
    public int acceptDirectTrade(Long chatroomId) {
        return tradeRepository.updateStatus(chatroomId, TradeStatus.ACCEPTED);
    }

    //택배 거래 요청
    @Transactional
    @Override
    public void parcelTrade(Long chatroomId, TradeInfoDTO tradeInfoDTO) {
        Product productRef = em.getReference(Product.class, tradeInfoDTO.getProductId());
        Chatroom chatroomRef = em.getReference(Chatroom.class, chatroomId);
        Trade trade = Trade.builder()
                .buyerId(tradeInfoDTO.getBuyerId())
                .sellerId(tradeInfoDTO.getSellerId())
                .product(productRef)
                .type(TradeType.PARCEL)
                .tradeStatus(TradeStatus.REQUESTED)
                .chatroom(chatroomRef)
                .build();
        tradeRepository.save(trade);
    }

    @Transactional
    @Override
    public int acceptParcelTrade(Long chatroomId) {
        return tradeRepository.updateStatus(chatroomId, TradeStatus.ACCEPTED);
    }

    @Override
    public TradeStatusDTO getTradeStatus(Long chatroomId, Long memberId) {
        return tradeStatusRepository.findTradeStatusByChatroomId(chatroomId, memberId);
    }

    @Override
    public CompleteTradeInfoDTO completeTrade(Long chatroomId,Long memberId) {
        //chatroomId로 해당 거래아이디와 상품 아이디를 찾음 -> Trade 엔티티에서 상품 아이디를 찾음
        CompleteTradeInfoDTO completeTradeInfoDTO;
        try {
            completeTradeInfoDTO = tradeCompleteRepository.findTradeInfoByChatroomId(chatroomId,memberId);
            Long productId = completeTradeInfoDTO.getProductId();
            tradeRepository.updateStatus(chatroomId, TradeStatus.COMPLETED);
            //해당 아이디의 상품 상태를 SOLD_OUT으로 바꿈.
            productService.updateProductStatusById(productId);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.CHATROOM_ID_NOT_FOUND);
        }
        return completeTradeInfoDTO;
    }

}
