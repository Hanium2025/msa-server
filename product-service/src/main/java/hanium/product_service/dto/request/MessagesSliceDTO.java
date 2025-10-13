package hanium.product_service.dto.request;

import hanium.product_service.dto.response.ChatMessageResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MessagesSliceDTO {
    private final List<ChatMessageResponseDTO> items;
    private final String prevCursor; // 가장 최신(리스트 첫번째)
    private final String nextCursor; // 가장 오래된(리스트 마지막)
    private final boolean hasMore;
}
