package hanium.user_service.mapper;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.response.MemberResponseDto;

public class MemberMapper {

    // Member 엔티티 -> MemberResponseDto
    public static MemberResponseDto toMemberResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }
}
