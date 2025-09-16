package hanium.product_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.*;
import hanium.product_service.domain.TradeStatus;
import hanium.product_service.dto.request.*;
import hanium.product_service.dto.response.*;
import hanium.product_service.mapper.ChatGrpcMapper;
import hanium.product_service.mapper.ProductGrpcMapper;
import hanium.product_service.mapper.TradeGrpcMapper;
import hanium.product_service.s3.PresignService;
import hanium.product_service.service.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@GrpcService
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;
    private final ProductLikeService likeService;
    private final ProductReportService reportService;
    private final ProductSearchService productSearchService;
    private final ProductUserService productUserService;
    private final ChatService chatService;
    private final PresignService presign;
    private final ProfileGrpcClient profileGrpcClient;
    private final TradeService tradeService;
    private final TradeReviewService tradeReviewService;

    // 메인페이지 조회
    @Override
    public void getProductMain(ProductMainRequest request, StreamObserver<ProductMainResponse> responseObserver) {
        try {
            ProductMainDTO dto = productService.getProductMain(request.getMemberId());
            responseObserver.onNext(ProductGrpcMapper.toProductMainResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 카테고리별 조회
    @Override
    public void getProductByCategory(GetProductByCategoryRequest request,
                                     StreamObserver<SimpleProductsResponse> responseObserver) {
        try {
            List<SimpleProductDTO> dto =
                    productService.getProductByCategory(GetProductByCategoryRequestDTO.from(request));
            responseObserver.onNext(ProductGrpcMapper.toSimpleProducts(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 등록
    @Override
    public void registerProduct(RegisterProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductResponseDTO dto = productService.registerProduct(RegisterProductRequestDTO.from(request));
            responseObserver.onNext(ProductGrpcMapper.toProductResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (Exception e) {
            CustomException ce = new CustomException(ErrorCode.ERROR_ADD_PRODUCT);
            responseObserver.onError(GrpcUtil.generateException(ce.getErrorCode()));
        }
    }

    // 상품 조회
    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductResponseDTO dto = productService.getProductAndViewLog(request.getMemberId(), request.getProductId());
            responseObserver.onNext(ProductGrpcMapper.toProductResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try {
            ProductResponseDTO dto = productService.updateProduct(UpdateProductRequestDTO.from(request));
            responseObserver.onNext(ProductGrpcMapper.toProductResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    @Override
    public void deleteImage(DeleteImageRequest request, StreamObserver<DeleteImageResponse> responseObserver) {
        try {
            int leftImageCount = productService.deleteProductImage(DeleteImageRequestDTO.from(request));
            responseObserver.onNext(DeleteImageResponse.newBuilder().setLeftImgCount(leftImageCount).build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 삭제
    @Override
    public void deleteProduct(GetProductRequest request, StreamObserver<Empty> responseObserver) {
        try {
            productService.deleteProductById(request.getProductId(), request.getMemberId());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 찜/찜 취소
    @Override
    public void likeProduct(GetProductRequest request, StreamObserver<LikeProductResponse> responseObserver) {
        try {
            responseObserver.onNext(LikeProductResponse.newBuilder()
                    .setLikeCanceled(
                            likeService.likeProduct(request.getMemberId(), request.getProductId()))
                    .build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 찜 목록 조회
    @Override
    public void getLikeProducts(GetLikedProductsRequest request,
                                StreamObserver<LikedProductsResponse> responseObserver) {
        try {
            responseObserver.onNext(
                    ProductGrpcMapper.toLikedProductsResponse(
                            likeService.getLikedProducts(request.getMemberId(), request.getPage()))
            );
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 검색
    @Override
    public void searchProduct(ProductSearchRequest request, StreamObserver<ProductSearchResponse> responseObserver) {
        try {
            ProductSearchResponseDTO dto = productSearchService.searchProduct(ProductSearchRequestDTO.from(request));
            responseObserver.onNext(ProductGrpcMapper.toProductSearchResponseGrpc(dto));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 검색 기록
    public void productSearchHistory(ProductSearchHistoryRequest request, StreamObserver<ProductSearchHistoryResponse> responseObserver) {
        try {
            List<ProductSearchHistoryDTO> historyList = productSearchService.productSearchHistory(request.getMemberId());
            ProductSearchHistoryResponse response =
                    ProductGrpcMapper.toProductSearchHistoryResponseGrpc(historyList);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 검색 기록 선택 삭제
    public void deleteProductSearchHistory(DeleteProductSearchHistoryRequest request, StreamObserver<Empty> responseObserver) {
        try {
            productSearchService.deleteProductSearchHistory(request.getSearchId(), request.getMemberId());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }

    }

    // 상품 검색 기록 전체 삭제
    public void deleteAllProductSearchHistory(DeleteAllProductSearchHistoryRequest request, StreamObserver<Empty> responseObserver) {
        try {
            productSearchService.deleteAllProductSearchHistory(request.getMemberId());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 상품 신고
    @Override
    public void reportProduct(ReportProductRequest request, StreamObserver<Empty> responseObserver) {
        try {
            ReportProductRequestDTO requestDTO = ReportProductRequestDTO.from(request);
            reportService.reportProduct(requestDTO);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 거래 평가 페이지
    @Override
    public void getTradeReviewPageInfo(GetTradeReviewPageRequest request, StreamObserver<GetTradeReviewPageResponse> responseObserver) {
        try {
            responseObserver.onNext(
                    TradeGrpcMapper.toGetTradeReviewPageResponseGrpc(
                            tradeReviewService.getTradeReviewPageInfo(request.getTradeId(), request.getMemberId())));
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    // 거래 평가
    @Override
    public void tradeReview(TradeReviewRequest request, StreamObserver<Empty> responseObserver) {
        try {
            TradeReviewRequestDTO requestDTO = TradeReviewRequestDTO.from(request);
            tradeReviewService.tradeReview(requestDTO);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }
    }

    //실시간 채팅
    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatResponseMessage> responseObserver) {
        return chatService.chat(responseObserver); // 서비스에 위임
    }

    //Presigned Url 발급
    @Override
    public void createPresignedUrls(CreatePresignedUrlsRequest request, StreamObserver<CreatePresignedUrlsResponse> responseObserver) {
        try {
            var urls = presign.issue(request.getChatroomId(), request.getCount(), request.getContentType());
            var b = CreatePresignedUrlsResponse.newBuilder();

            for (var u : urls) {
                b.addUrls(PresignedUrl.newBuilder()
                        .setPutUrl(u.putUrl())
                        .setGetUrl(u.getUrl())
                        .setKey(u.key())
                        .build());
            }
            responseObserver.onNext(b.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    //채팅방 별 모든 메시지 조회
    @Override
    public void getAllMessagesByChatroomId(GetAllMessagesByChatroomIdRequest request, StreamObserver<GetAllMessagesByChatroomResponse> responseObserver) {
        Long chatroomId = request.getChatRoomId();
        List<ChatMessageResponseDTO> allMessagesByChatroomId = chatService.getAllMessageByChatroomId(chatroomId);

        GetAllMessagesByChatroomResponse.Builder resp = GetAllMessagesByChatroomResponse.newBuilder();

        for (ChatMessageResponseDTO dto : allMessagesByChatroomId) {

            ProfileResponseDTO profileResponseDTO = profileGrpcClient.getProfileByMemberId(dto.getReceiverId());

            ChatResponseMessage.Builder responseMessage =
                    ChatResponseMessage.newBuilder()
                            .setMessageId(dto.getMessageId())
                            .setChatroomId(dto.getChatroomId())
                            .setSenderId(dto.getSenderId())
                            .setReceiverId(dto.getReceiverId())
                            .setContent(dto.getContent())
                            .setTimestamp(dto.getTimestamp())
                            .setReceiverNickname(profileResponseDTO.getNickname())
                            .setType(MessageType.valueOf(dto.getType()));

            if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
                responseMessage.addAllImageUrls(dto.getImageUrls());
            }

            resp.addChatResponseMessage(responseMessage);

        }

        responseObserver.onNext(resp.build());
        responseObserver.onCompleted();

    }

    //채팅방 생성
    @Override
    public void createChatroom(CreateChatroomRequest request, StreamObserver<CreateChatroomResponse> responseObserver) {
        CreateChatroomResponseDTO dto = chatService.createChatroom(CreateChatroomRequestDTO.from(request));
        responseObserver.onNext(ChatGrpcMapper.toCreateChatroomResponseGrpc(dto));
        responseObserver.onCompleted();
    }

    //내가 참여한 채팅방 조회
    @Override
    public void getMyChatrooms(ListMyChatroomsRequest request, StreamObserver<ListMyChatroomsResponse> responseObserver) {
        Long memberId = request.getMemberId();
        List<GetMyChatroomResponseDTO> items = chatService.getMyChatrooms(memberId);

        ListMyChatroomsResponse.Builder resp = ListMyChatroomsResponse.newBuilder();

        for (GetMyChatroomResponseDTO dto : items) {
            long latestMillis = dto.getLatestTime() == null
                    ? 0L
                    : dto.getLatestTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            ChatroomSummary summary =
                    ChatroomSummary.newBuilder()
                            .setChatroomId(dto.getChatroomId())
                            .setRoomName(dto.getRoomName() == null ? "" : dto.getRoomName())
                            .setLatestMessage(dto.getLatestMessage() == null ? "" : dto.getLatestMessage())
                            .setProductId(dto.getProductId() == null ? 0L : dto.getProductId())
                            .setOpponentId(dto.getOpponentId() == null ? 0L : dto.getOpponentId())
                            .setLatestTime(latestMillis)
                            .setOpponentProfileUrl(dto.getOpponentProfileUrl())
                            .setOpponentNickname(dto.getOpponentNickname())
                            .setSellerId(dto.getSellerId() == null ? 0L : dto.getSellerId())
                            .build();

            resp.addItems(summary);
        }

        responseObserver.onNext(resp.build());
        responseObserver.onCompleted();
    }

    // 직거래 요청
    @Override
    public void directTrade(TradeRequest request, StreamObserver<TradeResponse> responseObserver) {
        Long chatroomId = request.getChatroomId();
        Long buyerId = request.getMemberId(); //요청한 사람이 구매자
        log.info("직거래 요청 chatroomId : {} , 구매자 아이디 : {}", chatroomId, buyerId);
        TradeInfoDTO tradeInfoDTO = chatService.getTradeInfoByChatroomIdAndMemberId(chatroomId, buyerId);
        TradeResponse tradeResponse = TradeResponse.newBuilder().setOpponentId(tradeInfoDTO.getSellerId()).build();
        Long productId = tradeInfoDTO.getProductId();

        //해당 상품 거래 상태 확인
        String status = productService.getProductStatusById(productId);

        if ("SELLING".equals(status)) {//판매중이라면 Trade 생성
            tradeService.directTrade(chatroomId, tradeInfoDTO);
        }

        responseObserver.onNext(tradeResponse);
        responseObserver.onCompleted();
    }

    //직거래 수락
    @Override
    public void acceptDirectTrade(TradeRequest request, StreamObserver<TradeResponse> responseObserver) {
        Long chatroomId = request.getChatroomId();
        Long sellerId = request.getMemberId(); //수락하는 사람은 판매자
        log.info("직거래 수락 chatroomId : {} , 판매자 아이디 : {}", chatroomId, sellerId);

        TradeInfoDTO tradeInfoDTO = chatService.getTradeInfoByChatroomIdAndMemberId(chatroomId, sellerId);
        TradeResponse tradeResponse = TradeResponse.newBuilder().setOpponentId(tradeInfoDTO.getBuyerId()).build();

        Long productId = tradeInfoDTO.getProductId();

        //해당 상품 거래 상태 확인
        String status = productService.getProductStatusById(productId);
        if ("SELLING".equals(status)) {
            //trade상태를 업데이트 시키고
            tradeService.acceptDirectTrade(chatroomId);
            //상품 상태를 판매중으로 바꾸기
            productService.updateProductStatusById(productId);

        }
        responseObserver.onNext(tradeResponse);
        responseObserver.onCompleted();
    }

    //거래 진행 상태 조회
    @Override
    public void getTradeStatus(TradeRequest request, StreamObserver<TradeStatusResponse> responseObserver) {
        Long chatroomId = request.getChatroomId();
        Long memberId = request.getMemberId();
        log.info("거래 진행 조회 chatroomId : {} , 사용자 아이디 : {}", chatroomId, memberId);
        TradeStatus status = tradeService.getTradeStatus(chatroomId, memberId);
        TradeStatusResponse tradeStatusResponse = TradeStatusResponse.newBuilder().setStatus(status.name()).build();
        responseObserver.onNext(tradeStatusResponse);
        responseObserver.onCompleted();
    }

    //거래 완료 -> product status를 SOLD_OUT으로 바꿔줘야함.
    @Override
    public void completeTrade(TradeRequest request, StreamObserver<CompleteTradeResponse> responseObserver) {
        Long chatroomId = request.getChatroomId();
        Long memberId = request.getMemberId();

        log.info("거래완료 Grpc Service : chatroomId = {}, memberId = {} ", chatroomId, memberId);
        CompleteTradeInfoDTO completeTradeInfoDTO = tradeService.completeTrade(chatroomId, memberId);

        Long tradeId = completeTradeInfoDTO.getTradeId();
        Long opponentId = completeTradeInfoDTO.getOpponentId();
        log.info("거래완료 Grpc Service : tradeId = {}, opponentId = {}", tradeId, opponentId);
        CompleteTradeResponse completeTradeResponse = CompleteTradeResponse.newBuilder().setTradeId(tradeId).setOpponentId(opponentId).build();
        responseObserver.onNext(completeTradeResponse);
        responseObserver.onCompleted();
    }

    // 택배 거래 요청
    @Override
    public void parcelTrade(TradeRequest request, StreamObserver<TradeResponse> responseObserver) {
        Long chatroomId = request.getChatroomId();
        Long buyerId = request.getMemberId();
        TradeInfoDTO tradeInfoDTO = chatService.getTradeInfoByChatroomIdAndMemberId(chatroomId, buyerId);
        TradeResponse tradeResponse = TradeResponse.newBuilder().setOpponentId(tradeInfoDTO.getSellerId()).build();
        Long productId = tradeInfoDTO.getProductId();

        //해당 상품 거래 상태 확인
        String status = productService.getProductStatusById(productId);

        if ("SELLING".equals(status)) {//판매중이라면 Trade 생성
            tradeService.parcelTrade(chatroomId, tradeInfoDTO);
        }

        responseObserver.onNext(tradeResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void acceptParcelTrade(TradeRequest request, StreamObserver<TradeResponse> responseObserver) {
        Long chatroomId = request.getChatroomId();
        Long sellerId = request.getMemberId(); //수락하는 사람은 판매자
        TradeInfoDTO tradeInfoDTO = chatService.getTradeInfoByChatroomIdAndMemberId(chatroomId, sellerId);
        TradeResponse tradeResponse = TradeResponse.newBuilder().setOpponentId(tradeInfoDTO.getBuyerId()).build();

        Long productId = tradeInfoDTO.getProductId();

        //해당 상품 거래 상태 확인
        String status = productService.getProductStatusById(productId);
        if ("SELLING".equals(status)) {
            //trade상태를 업데이트 시키고
            tradeService.acceptDirectTrade(chatroomId);
            //상품 상태를 판매중으로 바꾸기
            productService.updateProductStatusById(productId);

        }
        responseObserver.onNext(tradeResponse);
        responseObserver.onCompleted();
    }


    // 프로필 > 주요 활동 카테고리 조회
    @Override
    public void getMainCategory(ProductMainRequest request, StreamObserver<GetMainCategoryResponse> responseObserver) {
        try {
            List<String> result = productUserService.getMainCategoryByMemberId(request.getMemberId());
            responseObserver.onNext(GetMainCategoryResponse.newBuilder().addAllCategory(result).build());
            responseObserver.onCompleted();
        } catch (CustomException e) {
            responseObserver.onError(GrpcUtil.generateException(e.getErrorCode()));
        }

    }

}
