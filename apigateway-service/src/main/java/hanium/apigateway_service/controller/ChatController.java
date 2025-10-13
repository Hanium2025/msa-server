package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.chat.request.CreateChatroomRequestDTO;
import hanium.apigateway_service.dto.chat.request.CreatePresignedUrlsApiRequest;
import hanium.apigateway_service.dto.chat.response.*;
import hanium.apigateway_service.grpc.ChatGrpcClient;
import hanium.apigateway_service.grpc.ChatroomGrpcClient;
import hanium.apigateway_service.grpc.PresignFacadeGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hanium.common.exception.ErrorCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatController {

    private final ChatroomGrpcClient chatroomGrpcClient;
    private final PresignFacadeGrpcClient facade;
    private final ChatGrpcClient chatGrpcClient;

    //채팅방 생성
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<CreateChatroomResponseDTO>> createChatroom(@Valid @RequestBody CreateChatroomRequestDTO createChatroomDTO, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        CreateChatroomRequestDTO requestDTO = CreateChatroomRequestDTO.builder()
                .productId(createChatroomDTO.getProductId())
                .receiverId(createChatroomDTO.getReceiverId())
                .build();
        Long chatroomId = chatroomGrpcClient.createChatroom(requestDTO,memberId);

        CreateChatroomResponseDTO responseData = new CreateChatroomResponseDTO(chatroomId, "채팅방 생성 성공!");
        ResponseDTO<CreateChatroomResponseDTO> response =
                new ResponseDTO<>(responseData, HttpStatus.OK, "채팅방 생성 성공!");
        return ResponseEntity.ok(response);
    }

    //내가 참여한 채팅방 조회
    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<GetMyChatroomResponseDTO>>> getAllChatrooms(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        var items = chatroomGrpcClient.getMyChatrooms(memberId);
        return ResponseEntity.ok(new ResponseDTO<>(items, HttpStatus.OK, "채팅방 목록 조회 성공!"));
    }

    //S3 Presigned-urls 발급하기

    @PostMapping("/presigned-urls")
    public List<PresignedUrlDTO> create(@RequestBody CreatePresignedUrlsApiRequest req) {
        if (req.getChatroomId() == null) {
            throw new CustomException(CHATROOM_ID_NOT_FOUND);
        }
        if (req.getCount() == null || req.getCount() < 1 || req.getCount() > 3) {
            throw new CustomException(INVALID_CHAT_IMAGE_REQUEST);
        }
        if (req.getContentType() == null || !req.getContentType().startsWith("image/"))
            throw new CustomException(NOT_IMAGE);
        return facade.create(req.getChatroomId(), req.getCount(), req.getContentType());
    }

    //채팅방 별 채팅 내역 조회하기
    @GetMapping("/get/{ChatRoomId}/allMessages")
public ResponseEntity<ResponseDTO<List<ChatMessageResponseDTO>>> getAllMessagesByChatroomId(@PathVariable("ChatRoomId") Long chatroomId){

        List<ChatMessageResponseDTO> messageResponseDto = chatGrpcClient.getAllMessagesByChatroomId(chatroomId);
        ResponseDTO<List<ChatMessageResponseDTO>> response = new ResponseDTO<>(messageResponseDto, HttpStatus.OK, "채팅 조회 성공!");
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방 메시지 커서 조회
     *  GET /api/chatrooms/45/messages?limit=20
     *  GET /api/chatrooms/45/messages?cursor=...&direction=BEFORE&limit=20  (과거 더보기)
     *  GET /api/chatrooms/45/messages?cursor=...&direction=AFTER&limit=20   (새 메시지 보강)
     */
    @GetMapping("/{chatroomId}/messages")
    public ResponseEntity<ChatMessagesCursorDTO> getMessagesByCursor(
            @PathVariable Long chatroomId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "BEFORE") String direction,
            @RequestParam(defaultValue = "20") int limit
    ) {
        boolean isAfter = "AFTER".equalsIgnoreCase(direction);
        ChatMessagesCursorDTO page = chatGrpcClient.getMessagesByCursor(chatroomId, cursor, limit, isAfter);
        return ResponseEntity.ok(page);
    }
}
