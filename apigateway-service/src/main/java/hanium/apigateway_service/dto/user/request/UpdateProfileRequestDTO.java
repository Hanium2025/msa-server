package hanium.apigateway_service.dto.user.request;

public record UpdateProfileRequestDTO(
        String nickname,
        String imageUrl
) {
}
