package hanium.apigateway_service.dto.chat.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class ChatMessagesCursorDTO {
    private final List<ChatMessageResponseDTO> messages;
    private final String nextCursor;
    private final String prevCursor;
    private final boolean hasMore;
}
