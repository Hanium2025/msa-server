package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.CommonResponseDTO;
import hanium.apigateway_service.dto.MemberSignupRequestDTO;
import hanium.apigateway_service.grpc.GrpcUserClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.proto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final GrpcUserClient grpcUserClient;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<CommonResponseDTO>> signUp(@RequestBody MemberSignupRequestDTO dto){
        CommonResponse protoResponse = grpcUserClient.signUp(dto);
        CommonResponseDTO commonResponseDTO = CommonResponseDTO.fromProto(protoResponse);

        ResponseDTO<CommonResponseDTO> response = new ResponseDTO<>(
                commonResponseDTO,
                commonResponseDTO.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
