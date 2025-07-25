package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.user.request.LoginRequestDTO;
import hanium.apigateway_service.dto.user.request.SignUpRequestDTO;
import hanium.apigateway_service.dto.user.request.SmsRequestDTO;
import hanium.apigateway_service.dto.user.request.VerifySmsRequestDTO;
import hanium.apigateway_service.dto.user.response.MemberResponseDTO;
import hanium.apigateway_service.dto.user.response.SignUpResponseDTO;
import hanium.apigateway_service.dto.user.response.TokenResponseDTO;
import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.apigateway_service.security.JwtUtil;
import hanium.common.proto.user.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/auth/refresh")
    public ResponseEntity<ResponseDTO<TokenResponseDTO>> refresh(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        TokenResponse grpcResult = userGrpcClient.reissueToken(
                JwtUtil.extractRefreshToken(request), response
        );
        ResponseDTO<TokenResponseDTO> responseDTO = new ResponseDTO<>(
                TokenResponseDTO.from(grpcResult), HttpStatus.OK, "토큰 재발급에 성공했습니다."
        );
        return ResponseEntity.ok(responseDTO);
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
    public ResponseEntity<?> sendSms(@RequestBody SmsRequestDTO dto) {
        String message = userGrpcClient.sendSms(dto.getPhoneNumber()).getMessage();
        ResponseDTO<String> response = new ResponseDTO<>(
                null, HttpStatus.OK, message
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sms/verify")
    public ResponseEntity<?> verifySms(@RequestBody VerifySmsRequestDTO dto) {
        if (userGrpcClient.verifySms(dto).getVerified()) {
            ResponseDTO<String> response = new ResponseDTO<>(
                    null, HttpStatus.OK, "인증번호 확인되었습니다."
            );
            return ResponseEntity.ok(response);
        } else {
            ResponseDTO<String> response = new ResponseDTO<>(
                    null, HttpStatus.BAD_REQUEST, "인증번호를 다시 확인해주세요."
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
