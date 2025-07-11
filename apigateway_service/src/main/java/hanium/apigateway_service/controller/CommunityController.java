package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.CommonResponseDTO;
import hanium.apigateway_service.dto.community.CreatePostRequestDTO;
import hanium.apigateway_service.grpc.CommunityGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.proto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityGrpcClient grpcClient;

//    @GetMapping("/ping")
//    public String pingCommunityService() {
//        return grpcClient.ping();
//    }

    @PostMapping("/post")
    public ResponseEntity<ResponseDTO<CommonResponseDTO>> createPost(@RequestBody CreatePostRequestDTO dto) {
        CommonResponse protoResponse = grpcClient.createPost(dto);
        CommonResponseDTO responseDTO = CommonResponseDTO.fromProto(protoResponse);

        ResponseDTO<CommonResponseDTO> response = new ResponseDTO<>(
                responseDTO,
                responseDTO.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(response.getCode()).body(response);
    }
}