package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.user.request.LoginRequestDTO;
import hanium.apigateway_service.dto.user.request.SignUpRequestDTO;
import hanium.apigateway_service.dto.user.request.smsRequestDTO;
import hanium.apigateway_service.dto.user.response.MemberResponseDTO;
import hanium.apigateway_service.dto.user.response.SignUpResponseDTO;
import hanium.apigateway_service.dto.user.response.TokenResponseDTO;
import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<ResponseDTO<SignUpResponseDTO>> signUp(@RequestBody SignUpRequestDTO dto) {
        // grpc 클라이언트 호출
        SignUpResponseDTO responseDTO = SignUpResponseDTO.from(userGrpcClient.signUp(dto));
        // 응답 생성
        ResponseDTO<SignUpResponseDTO> response = new ResponseDTO<>(
                responseDTO, HttpStatus.CREATED, "정상적으로 회원가입되었습니다."
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDTO<TokenResponseDTO>> login(@RequestBody LoginRequestDTO dto,
                                                               HttpServletResponse response) {
        TokenResponseDTO responseDTO = TokenResponseDTO.from(userGrpcClient.login(dto, response));
        ResponseDTO<TokenResponseDTO> result = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "정상적으로 로그인되었습니다."
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ResponseDTO<MemberResponseDTO>> getMemberById(@PathVariable Long memberId) {
        MemberResponseDTO responseDTO = MemberResponseDTO.from(userGrpcClient.getMemberById(memberId));
        ResponseDTO<MemberResponseDTO> response = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "정상적으로 회원이 조회되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sms/send")
    public ResponseEntity<?> sendSms(@RequestBody @Valid smsRequestDTO dto) {
        // TODO: sendSms 컨트롤러 작성, 수정
        return (ResponseEntity<?>) ResponseEntity.ok();
    }
}
