package hanium.community_service.mapper.entity;

import hanium.community_service.dto.CreatePostRequestDTO;
import hanium.community_service.entity.Post;

/**
 * PostEntityMapper는 게시글 관련 DTO와 Entity 간의 변환을 담당하는 매퍼 클래스입니다.
 *
 * 주로 CreatePostRequestDTO와 Post 엔티티 객체 간 상호 변환 기능을 제공합니다.
 */
public class PostEntityMapper {
    // DTO → Entity
    public static Post toEntity(CreatePostRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writerId(dto.getWriterId())
                .build();
    }

    // Entity → DTO
    public static CreatePostRequestDTO toDto(Post post) {
        if (post == null) {
            return null;
        }

        return CreatePostRequestDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .writerId(post.getWriterId())
                .build();
    }
}
