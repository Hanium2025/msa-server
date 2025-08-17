package hanium.user_service.service;

import hanium.user_service.dto.request.GetNicknameRequestDTO;
import hanium.user_service.dto.response.GetNicknameResponseDTO;

public interface ProfileService {
    public GetNicknameResponseDTO getNicknameByMemberId(GetNicknameRequestDTO requestDTO);
}
