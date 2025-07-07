package hanium.community_service.Service.impl;

import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import hanium.community_service.Service.PostService;
import hanium.community_service.dto.CreatePostRequestDTO;
import hanium.community_service.entity.Post;
import hanium.community_service.mapper.entity.PostEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import hanium.community_service.Repository.PostRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * PostServiceImpl은 PostService 인터페이스의 구현체로,
 * 게시글 생성과 관련된 비즈니스 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Transactional
    @Override
    public void createPost(CreatePostRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        Post post = PostEntityMapper.toEntity(dto);

        postRepository.save(post);
    }
}
