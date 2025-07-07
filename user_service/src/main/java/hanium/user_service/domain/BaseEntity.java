package hanium.user_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column
    private LocalDateTime updated_at;

    @Column
    private LocalDateTime deletedAt;

    // soft delete 확인
    public boolean isSoftDeleted() {
        return deletedAt != null;
    }

    // 삭제 취소
    public void undoDeletion() {
        this.deletedAt = null;
    }
}