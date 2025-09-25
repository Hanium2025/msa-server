package hanium.user_service.service;

import hanium.common.proto.user.UpdateProfileRequest;
import hanium.common.proto.user.UpdateScoreRequest;
import hanium.user_service.dto.request.GetNicknameRequestDTO;
import hanium.user_service.dto.request.GetPresignedUrlRequestDTO;
import hanium.user_service.dto.response.GetNicknameResponseDTO;
import hanium.user_service.dto.response.PresignedUrlResponseDTO;
import hanium.user_service.dto.response.ProfileDetailResponseDTO;
import hanium.user_service.dto.response.ProfileResponseDTO;

public interface ProfileService {

    GetNicknameResponseDTO getNicknameByMemberId(GetNicknameRequestDTO requestDTO);

    ProfileResponseDTO getProfileByMemberId(Long memberId);

    PresignedUrlResponseDTO getPresignedUrl(GetPresignedUrlRequestDTO dto);

    ProfileResponseDTO updateProfile(UpdateProfileRequest request);

    ProfileDetailResponseDTO getProfileDetailByMemberId(Long memberId);

    void updateScore(UpdateScoreRequest request);
}
