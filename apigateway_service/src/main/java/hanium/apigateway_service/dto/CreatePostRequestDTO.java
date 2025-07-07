package hanium.apigateway_service.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CreatePostRequestDTO는 게시글 생성 요청 시 클라이언트에서 전달되는 데이터를 담는
 * 데이터 전송 객체(Data Transfer Object)입니다.
 *
 * Lombok 어노테이션을 통해 기본 생성자, 모든 필드를 받는 생성자, 빌더 패턴,
 * 그리고 Getter 메서드가 자동 생성됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequestDTO {
    private String title;
    private String content;
    private Long writerId;
}
