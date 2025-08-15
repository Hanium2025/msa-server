package hanium.apigateway_service.dto.chat.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePresignedUrlsApiRequest {
    private Long chatroomId;
    private Integer count;
    private String contentType;
}
