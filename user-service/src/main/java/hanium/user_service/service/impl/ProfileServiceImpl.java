package hanium.user_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.common.proto.user.UpdateProfileRequest;
import hanium.common.proto.user.UpdateScoreRequest;
import hanium.user_service.domain.Member;
import hanium.user_service.domain.Profile;
import hanium.user_service.dto.request.GetNicknameRequestDTO;
import hanium.user_service.dto.request.GetPresignedUrlRequestDTO;
import hanium.user_service.dto.response.*;
import hanium.user_service.grpc.ProductUserGrpcClient;
import hanium.user_service.repository.ProfileRepository;
import hanium.user_service.service.ProfileService;
import hanium.user_service.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProductUserGrpcClient productUserGrpcClient;
    private final S3Util s3Util;

    @Override
    public GetNicknameResponseDTO getNicknameByMemberId(GetNicknameRequestDTO requestDTO) {

        Long memberId = requestDTO.getMemberId();
        String nickname = profileRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))
                .getNickname();

        return GetNicknameResponseDTO.builder()
                .nickname(nickname)
                .build();
    }

    // 프로필 반환
    @Override
    public ProfileResponseDTO getProfileByMemberId(Long memberId) {
        Profile profile = profileRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return ProfileResponseDTO.from(profile);
    }

    // 프로필사진 수정 시 presigned url 반환
    @Override
    public PresignedUrlResponseDTO getPresignedUrl(GetPresignedUrlRequestDTO dto) {
        return s3Util.getPresignedUrl(dto);
    }

    // 프로필 수정
    @Override
    public ProfileResponseDTO updateProfile(UpdateProfileRequest request) {
        Profile profile = profileRepository.findByMemberIdAndDeletedAtIsNull(request.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        profile.update(request.getNickname(), request.getImageUrl());
        return ProfileResponseDTO.from(profile);
    }

    // 마이페이지 정보 조회
    @Override
    public ProfileDetailResponseDTO getProfileDetailByMemberId(Long memberId) {
        Profile profile = profileRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Member member = profile.getMember();

        return ProfileDetailResponseDTO.builder()
                .memberId(memberId)
                .nickname(profile.getNickname())
                .imageUrl(profile.getImageUrl())
                .score(profile.getScore())
                .mainCategory(calculateMainCategory(profile))
                .agreeMarketing(member.isAgreeMarketing())
                .agree3rdParty(member.isAgreeThirdParty())
                .build();
    }

    private List<String> calculateMainCategory(Profile profile) {
        return productUserGrpcClient.getMainCategories(profile.getMember().getId());
    }

    // 신뢰도 점수 업데이트
    @Override
    public void updateScore(UpdateScoreRequest request) {
        Profile profile = profileRepository.findByMemberIdAndDeletedAtIsNull(request.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        profile.addScore((long) request.getAmount());
    }

    // 타 사용자 프로필 조회
    @Override
    public OtherProfileResponseDTO getOtherProfile(Long memberId) {
        Profile profile = profileRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        List<SimpleProductDTO> sellingProducts = productUserGrpcClient.getSellingProducts(memberId);

        return OtherProfileResponseDTO.builder()
                .memberId(memberId)
                .nickname(profile.getNickname())
                .imageUrl(profile.getImageUrl())
                .score(profile.getScore())
                .mainCategory(calculateMainCategory(profile))
                .products(sellingProducts)
                .build();
    }
}
