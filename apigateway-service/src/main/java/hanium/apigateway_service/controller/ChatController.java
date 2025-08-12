package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.chat.request.CreateChatroomRequestDTO;
import hanium.apigateway_service.dto.chat.response.CreateChatroomResponseDTO;
import hanium.apigateway_service.dto.chat.response.GetMyChatroomResponseDTO;
import hanium.apigateway_service.grpc.ChatroomGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatController {

    private final ChatroomGrpcClient chatroomGrpcClient;

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
    public ResponseEntity<ResponseDTO<List<GetMyChatroomResponseDTO>>> getAllChatrooms(Authentication authentication){
        Long memberId = (Long) authentication.getPrincipal();
        var items = chatroomGrpcClient.getMyChatrooms(memberId);
        return ResponseEntity.ok(new ResponseDTO<>(items,HttpStatus.OK,"채팅방 목록 조회 성공!"));
    }




}
