package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.CommonResponseDTO;
import hanium.apigateway_service.dto.LoginRequestDTO;
import hanium.apigateway_service.dto.LoginResponseDTO;
import hanium.apigateway_service.dto.SignUpRequestDTO;
import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.proto.CommonResponse;
import hanium.common.proto.user.LoginResponse;
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
@RequestMapping("/user")
public class UserController {

    private final UserGrpcClient userGrpcClient;

    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDTO<CommonResponseDTO>> signUp(@RequestBody SignUpRequestDTO dto){
        // grpc 클라이언트 호출
        CommonResponse protoResponse = userGrpcClient.signUp(dto);
        CommonResponseDTO commonResponseDTO = CommonResponseDTO.fromProto(protoResponse);
        // 응답 생성
        ResponseDTO<CommonResponseDTO> response = new ResponseDTO<>(
                commonResponseDTO,
                commonResponseDTO.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO dto){
        log.trace("✅[gRPC] 요청: email = {}", dto.getEmail());
        // grpc 클라이언트 호출
        LoginResponse protoResponse = userGrpcClient.login(dto);
        LoginResponseDTO loginResponseDTO = UserGrpcMapperForGateway.toLoginDTO(protoResponse);
        log.trace("✅[gRPC] 응답: email = {}, token = {}",
                loginResponseDTO.getEmail(), loginResponseDTO.getAccessToken());
        // 응답 생성
        ResponseDTO<LoginResponseDTO> response = new ResponseDTO<>(
                loginResponseDTO, HttpStatus.OK, "정상적으로 로그인되었습니다."
        );
        return ResponseEntity.ok(response);
    }
}
