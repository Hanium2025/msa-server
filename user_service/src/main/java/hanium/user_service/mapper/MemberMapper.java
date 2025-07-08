package hanium.user_service.mapper;

import hanium.common.proto.user.SignUpRequest;
import hanium.user_service.domain.Member;
import hanium.user_service.dto.request.MemberSignupRequestDto;
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

    // SignUpRequest gRPC -> dto
    public static MemberSignupRequestDto toSignupDto(SignUpRequest request) {
        return MemberSignupRequestDto.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .confirmPassword(request.getConfirmPassword())
                .phoneNumber(request.getPhoneNumber())
                .nickname(request.getNickname())
                .agreeMarketing(request.getAgreeMarketing())
                .agreeThirdParty(request.getAgreeThirdParty())
                .build();
    }
}
