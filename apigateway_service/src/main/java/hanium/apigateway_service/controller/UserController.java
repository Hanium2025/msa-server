package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.*;
import hanium.apigateway_service.dto.user.*;
import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.apigateway_service.mapper.UserGrpcMapperForGateway;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.common.proto.user.GetMemberResponse;
import hanium.common.proto.user.LoginResponse;
import hanium.common.proto.user.SignUpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserGrpcClient userGrpcClient;

    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDTO<SignUpResponseDTO>> signUp(@RequestBody SignUpRequestDTO dto){
        log.trace("✅[gRPC] 회원가입 요청: email = {}", dto.getEmail());
        // grpc 클라이언트 호출
        SignUpResponse protoResponse = userGrpcClient.signUp(dto);
        SignUpResponseDTO responseDTO = UserGrpcMapperForGateway.toSignUpDTO(protoResponse);
        // 응답 생성
        ResponseDTO<SignUpResponseDTO> response = new ResponseDTO<>(
                responseDTO, HttpStatus.CREATED, "정상적으로 회원가입되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO dto){
        log.trace("✅[gRPC] 로그인 요청: email = {}", dto.getEmail());
        // grpc 클라이언트 호출
        LoginResponse protoResponse = userGrpcClient.login(dto);
        LoginResponseDTO loginResponseDTO = UserGrpcMapperForGateway.toLoginDTO(protoResponse);
        // 응답 생성
        ResponseDTO<LoginResponseDTO> response = new ResponseDTO<>(
                loginResponseDTO, HttpStatus.OK, "정상적으로 로그인되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ResponseDTO<MemberResponseDTO>> getMemberById(@PathVariable Long memberId) {
        GetMemberResponse protoResponse = userGrpcClient.getMemberById(memberId);
        MemberResponseDTO responseDTO = UserGrpcMapperForGateway.toMemberDTO(protoResponse);

        ResponseDTO<MemberResponseDTO> response = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "해당하는 회원이 정상적으로 조회되었습니다."
        );
        return ResponseEntity.ok(response);
    }
}
