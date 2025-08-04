package hanium.user_service.service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.user_service.dto.request.GetNicknameRequestDTO;
import hanium.user_service.dto.response.GetNicknameResponseDTO;
import hanium.user_service.repository.ProfileRepository;
import hanium.user_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
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
}
