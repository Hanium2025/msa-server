package hanium.apigateway_service.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanium.apigateway_service.dto.CommonResponseDTO;
import hanium.common.exception.CustomException;
import hanium.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 시큐리티 필터 단에서 발생할 수 있는 예외를 잡고, 해당 필터 전에 배치하여 예외 처리하는 필터입니다.
 */
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            ErrorCode errorCode = e.getErrorCode();
            int httpStatus = errorCode.getCode();
            String message = errorCode.getMessage();
            CommonResponseDTO responseDTO = CommonResponseDTO.builder()
                    .success(false)
                    .errorCode(httpStatus)
                    .message(message).build();
            response.setStatus(httpStatus);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
        }
    }
}
