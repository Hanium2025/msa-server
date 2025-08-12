package hanium.product_service.repository;
import hanium.product_service.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Message, Long> {


}
