package hanium.community_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hanium.community_service.entity.Post;
/**
 * PostRepository는 Spring Data JPA의 JpaRepository를 상속하여
 * Post 엔티티에 대한 기본 CRUD 및 페이징 기능을 제공합니다.
 *
 * 별도의 구현 없이도 기본적인 데이터베이스 접근이 가능하며,
 * 필요 시 커스텀 쿼리를 추가할 수 있습니다.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

}