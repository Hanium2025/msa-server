package hanium.user_service.service;

import hanium.user_service.dto.request.GetNicknameRequestDTO;
import hanium.user_service.dto.response.GetNicknameResponseDTO;
import hanium.user_service.dto.response.ProfileResponseDTO;

public interface ProfileService {
    GetNicknameResponseDTO getNicknameByMemberId(GetNicknameRequestDTO requestDTO);

    ProfileResponseDTO getProfileByMemberId(Long memberId);
}
