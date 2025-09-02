package hanium.product_service.repository;

import hanium.product_service.domain.MessageImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MessageImageRepository extends JpaRepository<MessageImage, Long> {
    List<MessageImage> findAllByMessageIdIn(Collection<Long> messageId);
}
