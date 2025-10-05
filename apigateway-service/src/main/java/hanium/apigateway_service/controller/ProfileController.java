package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.product.response.SimpleProductDTO;
import hanium.apigateway_service.dto.user.request.UpdateProfileRequestDTO;
import hanium.apigateway_service.dto.user.response.OtherProfileResponseDTO;
import hanium.apigateway_service.dto.user.response.PresignedUrlResponseDTO;
import hanium.apigateway_service.dto.user.response.ProfileDetailResponseDTO;
import hanium.apigateway_service.dto.user.response.ProfileResponseDTO;
import hanium.apigateway_service.grpc.ProductGrpcClient;
import hanium.apigateway_service.grpc.UserGrpcClient;
import hanium.apigateway_service.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserGrpcClient userGrpcClient;
    private final ProductGrpcClient productGrpcClient;

    // 프로필사진 수정용 Presigned url 발급
    @GetMapping("/edit/presigned")
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
    @PutMapping("/edit")
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
    @GetMapping
    public ResponseEntity<ResponseDTO<ProfileDetailResponseDTO>> getMyProfile(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        ProfileDetailResponseDTO responseDTO = userGrpcClient.getDetailProfile(memberId);
        ResponseDTO<ProfileDetailResponseDTO> result = new ResponseDTO<>(
                responseDTO, HttpStatus.OK, "나의 프로필이 조회되었습니다."
        );
        return ResponseEntity.ok(result);
    }

    // 상대 프로필 상세 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<ResponseDTO<OtherProfileResponseDTO>> getOtherProfile(Authentication authentication,
                                                                                @PathVariable Long memberId) {
        Long myId = (Long) authentication.getPrincipal();
        OtherProfileResponseDTO dto = userGrpcClient.getOtherProfile(myId, memberId);
        ResponseDTO<OtherProfileResponseDTO> result = new ResponseDTO<>(
                dto, HttpStatus.OK, "ID=" + memberId + "의 프로필이 조회되었습니다."
        );
        return ResponseEntity.ok(result);
    }

    // (마이페이지) 마케팅 동의 토글
    @PostMapping("/toggle/marketing")
    public ResponseEntity<?> toggleAgreeMarketing(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        String message = userGrpcClient.toggleAgreeMarketing(memberId);
        ResponseDTO<?> result = new ResponseDTO<>(null, HttpStatus.OK, message);
        return ResponseEntity.ok(result);
    }

    // (마이페이지) 제3자 동의 토글
    @PostMapping("/toggle/third-party")
    public ResponseEntity<?> toggleAgreeThirdParty(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        String message = userGrpcClient.toggleAgreeThirdParty(memberId);
        ResponseDTO<?> result = new ResponseDTO<>(null, HttpStatus.OK, message);
        return ResponseEntity.ok(result);
    }

    // 내 판매내역 조회
    @GetMapping("/trade/sell")
    public ResponseEntity<ResponseDTO<List<SimpleProductDTO>>> getSellingProducts(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        List<SimpleProductDTO> result = productGrpcClient.getSellingProducts(memberId);
        ResponseDTO<List<SimpleProductDTO>> response =
                new ResponseDTO<>(result, HttpStatus.OK, "내 판매내역이 조회되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 내 구매내역 조회
    @GetMapping("/trade/buy")
    public ResponseEntity<ResponseDTO<List<SimpleProductDTO>>> getBuyingProducts(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        List<SimpleProductDTO> result = productGrpcClient.getBuyingProducts(memberId);
        ResponseDTO<List<SimpleProductDTO>> response =
                new ResponseDTO<>(result, HttpStatus.OK, "내 구매내역이 조회되었습니다.");
        return ResponseEntity.ok(response);
    }
}
