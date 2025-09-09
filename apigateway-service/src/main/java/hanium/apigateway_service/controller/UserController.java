package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.user.request.*;
import hanium.apigateway_service.dto.user.response.*;
import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import hanium.apigateway_service.security.JwtUtil;
import hanium.common.proto.user.KakaoConfigResponse;
import hanium.common.proto.user.NaverConfigResponse;
import hanium.common.proto.user.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserGrpcClient userGrpcClient;

    // 회원가입
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

    // 일반 로그인
    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDTO<TokenResponseDTO>> login(@RequestBody LoginRequestDTO dto,
                                                               HttpServletResponse response) {
        TokenResponseDTO responseDTO = TokenResponseDTO.from(userGrpcClient.login(dto, response));
        ResponseDTO<TokenResponseDTO> result = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "정상적으로 로그인되었습니다."
        );
        return ResponseEntity.ok(result);
    }

    // 토큰 재발급
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

    // 회원 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ResponseDTO<MemberResponseDTO>> getMemberById(@PathVariable Long memberId) {
        MemberResponseDTO responseDTO = MemberResponseDTO.from(userGrpcClient.getMemberById(memberId));
        ResponseDTO<MemberResponseDTO> response = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "정상적으로 회원이 조회되었습니다."
        );
        return ResponseEntity.ok(response);
    }

    // sms 인증번호 전송
    @PostMapping("/sms/send")
    public ResponseEntity<?> sendSms(@RequestBody SmsRequestDTO dto) {
        String message = userGrpcClient.sendSms(dto.getPhoneNumber()).getMessage();
        ResponseDTO<String> response = new ResponseDTO<>(
                null, HttpStatus.OK, message
        );
        return ResponseEntity.ok(response);
    }

    // sms 인증번호 검증
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

    // 프론트엔드에 카카오 로그인 설정 값 전달
    @GetMapping("/auth/kakao-config")
    public ResponseEntity<Map<String, String>> getKakaoConfig() {
        KakaoConfigResponse kakaoConfigResponse = userGrpcClient.getKakaoConfig();
        Map<String, String> body = Map.of(
                "kakaoClientId", kakaoConfigResponse.getClientId(),
                "kakaoRedirectUri", kakaoConfigResponse.getRedirectUri()
        );
        return ResponseEntity.ok()
                // 보안용 헤더 추가
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(body);
    }

    // 프론트엔드에 네이버 로그인 설정 값 전달
    @GetMapping("/auth/naver-config")
    public ResponseEntity<NaverConfigResponseDTO> getNaverConfig() {
        NaverConfigResponse naverConfigResponse = userGrpcClient.getNaverConfig();
        NaverConfigResponseDTO body = NaverConfigResponseDTO.from(naverConfigResponse);
        return ResponseEntity.ok()
                // 보안용 헤더 추가
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(body);
    }

    // 카카오 소셜 로그인 code 받아서 회원가입 or 로그인
    @GetMapping("/auth/kakao/redirect")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) {
        TokenResponseDTO responseDTO = TokenResponseDTO.from(userGrpcClient.socialLogin(code, "kakao"));
        ResponseDTO<TokenResponseDTO> result = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "카카오 로그인에 성공했습니다."
        );
        return ResponseEntity.ok(result);
    }

    // 네이버 소셜 로그인 code 받아서 회원가입 or 로그인
    @GetMapping("/auth/naver/redirect")
    public ResponseEntity<?> naverLogin(@RequestParam("code") String code) {
        TokenResponseDTO responseDTO = TokenResponseDTO.from(userGrpcClient.socialLogin(code, "naver"));
        ResponseDTO<TokenResponseDTO> result = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "네이버 로그인에 성공했습니다."
        );
        return ResponseEntity.ok(result);
    }

    // 프로필사진 수정용 Presigned url 발급
    @GetMapping("/presigned-url")
    public ResponseEntity<ResponseDTO<PresignedUrlResponseDTO>> getPresignedUrl(Authentication authentication,
                                                                                @RequestParam String contentType) {
        Long memberId = (Long) authentication.getPrincipal();
        PresignedUrlResponseDTO dto = userGrpcClient.getPresignedUrl(memberId, contentType);
        ResponseDTO<PresignedUrlResponseDTO> result = new ResponseDTO<>(
                dto, HttpStatus.OK, "Presigned URL이 발급되었습니다."
        );
        return ResponseEntity.ok(result);
    }

    // 프로필 수정
    @PutMapping("profile")
    public ResponseEntity<ResponseDTO<ProfileResponseDTO>> updateProfile(Authentication authentication,
                                                                         @RequestBody UpdateProfileRequestDTO dto) {
        Long memberId = (Long) authentication.getPrincipal();
        ProfileResponseDTO responseDTO = userGrpcClient.updateProfile(memberId, dto);
        ResponseDTO<ProfileResponseDTO> result = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "프로필이 수정되었습니다."
        );
        return ResponseEntity.ok(result);
    }

    // (마이페이지) 프로필 상세 조회
    @GetMapping("my-profile")
    public ResponseEntity<ResponseDTO<ProfileDetailResponseDTO>> getMyProfile(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        ProfileDetailResponseDTO responseDTO = userGrpcClient.getDetailProfile(memberId);
        ResponseDTO<ProfileDetailResponseDTO> result = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "나의 프로필이 조회되었습니다."
        );
        return ResponseEntity.ok(result);
    }
}
