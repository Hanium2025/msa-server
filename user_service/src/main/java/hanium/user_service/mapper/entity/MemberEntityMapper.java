package hanium.user_service.mapper.entity;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.response.MemberResponseDTO;

public class MemberEntityMapper {

    // Member 엔티티 -> MemberResponseDto
    public static MemberResponseDTO toMemberResponseDto(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }
}
