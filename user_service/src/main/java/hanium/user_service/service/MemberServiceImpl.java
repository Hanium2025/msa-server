package hanium.user_service.service;

import hanium.user_service.dto.MemberSignupRequestDTO;
import hanium.user_service.dto.ResponseMemberDTO;
import hanium.user_service.domain.MemberEntity;
import hanium.user_service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ModelMapper mapper;

    /**
     * @param dto 회원 가입 요청
     * @return 회원 가입 응답
     * @apiNote 회원을 생성합니다.
     */
    @Override
    public ResponseMemberDTO createMember(MemberSignupRequestDTO dto) {
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        MemberEntity memberEntity = mapper.map(dto, MemberEntity.class);
        memberRepository.save(memberEntity);

        return mapper.map(memberEntity, ResponseMemberDTO.class);
    }

    /**
     * @param memberId 회원 ID
     * @return 회원 조회 응답
     * @apiNote 회원 ID로 회원을 조회합니다.
     */
    @Override
    public ResponseMemberDTO getMemberById(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 id 값입니다."));
        return mapper.map(memberEntity, ResponseMemberDTO.class);
    }

    @Override
    public MemberEntity getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일입니다."));
    }
}
