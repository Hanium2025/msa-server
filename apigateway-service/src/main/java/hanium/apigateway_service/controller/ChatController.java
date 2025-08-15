package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.chat.request.CreateChatroomRequestDTO;
import hanium.apigateway_service.dto.chat.request.CreatePresignedUrlsApiRequest;
import hanium.apigateway_service.dto.chat.response.CreateChatroomResponseDTO;
import hanium.apigateway_service.dto.chat.response.GetMyChatroomResponseDTO;
import hanium.apigateway_service.dto.chat.response.PresignedUrlDTO;
import hanium.apigateway_service.grpc.ChatroomGrpcClient;
import hanium.apigateway_service.grpc.PresignFacadeGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatController {

    private final ChatroomGrpcClient chatroomGrpcClient;
    private final PresignFacadeGrpcClient facade;

    //채팅방 생성
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<CreateChatroomResponseDTO>> createChatroom(@RequestBody CreateChatroomRequestDTO createChatroomDTO, Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        CreateChatroomRequestDTO requestDTO = CreateChatroomRequestDTO.builder()
                .productId(createChatroomDTO.getProductId())
                .receiverId(createChatroomDTO.getReceiverId())
                .senderId(memberId)
                .build();
        Long chatroomId = chatroomGrpcClient.createChatroom(requestDTO);

        CreateChatroomResponseDTO responseData = new CreateChatroomResponseDTO(chatroomId, "채팅방 생성 성공!");
        ResponseDTO<CreateChatroomResponseDTO> response =
                new ResponseDTO<>(responseData, HttpStatus.OK, "채팅방 생성 성공!");
        return ResponseEntity.ok(response);
    }

    //TODO : 채팅방 조회
    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<GetMyChatroomResponseDTO>>> getAllChatrooms(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        var items = chatroomGrpcClient.getMyChatrooms(memberId);
        return ResponseEntity.ok(new ResponseDTO<>(items, HttpStatus.OK, "채팅방 목록 조회 성공!"));
    }

    @PostMapping("/presigned-urls")
    public List<PresignedUrlDTO> create(@RequestBody CreatePresignedUrlsApiRequest req) {
        if (req.getChatroomId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "chatroomId required");
        }
        if (req.getCount() == null || req.getCount() < 1 || req.getCount() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "count 1..3");
        }
        if (req.getContentType() == null || !req.getContentType().startsWith("image/"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "images only");
        return facade.create(req.getChatroomId(), req.getCount(), req.getContentType());
    }

}
