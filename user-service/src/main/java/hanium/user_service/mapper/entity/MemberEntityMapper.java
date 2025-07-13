package hanium.user_service.mapper.entity;

import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.SignUpRequestDTO;
import hanium.user_service.dto.response.MemberResponseDTO;
import hanium.user_service.dto.response.SignUpResponseDTO;

public class MemberEntityMapper {

    // Member 엔티티 -> MemberResponseDto
    public static MemberResponseDTO toMemberResponseDto(Member member) {
        return MemberResponseDTO.builder()
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .provider(String.valueOf(member.getProvider()))
                .role(String.valueOf(member.getRole()))
                .build();
    }

    // Member 엔티티 -> SignupResponseDTO
    public static SignUpResponseDTO toSignupResponseDTO(Member member) {
        return SignUpResponseDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .provider(String.valueOf(member.getProvider()))
                .role(String.valueOf(member.getRole()))
                .agreeMarketing(member.isAgreeMarketing())
                .agreeThirdParty(member.isAgreeThirdParty())
                .build();
    }
}
