package hanium.user_service.repository;

import hanium.user_service.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByMemberIdAndDeletedAtIsNull(Long memberId);

}
