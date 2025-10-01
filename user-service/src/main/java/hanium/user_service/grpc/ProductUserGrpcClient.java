package hanium.user_service.grpc;

import hanium.common.exception.CustomException;
import hanium.common.exception.GrpcUtil;
import hanium.common.proto.product.ProductMainRequest;
import hanium.common.proto.product.ProductServiceGrpc;
import hanium.user_service.dto.response.SimpleProductDTO;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductUserGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    // 프로필 > 주요 활동 카테고리 조회
    public List<String> getMainCategories(Long memberId) {
        try {
            ProductMainRequest request = ProductMainRequest.newBuilder().setMemberId(memberId).build();
            return stub.getMainCategory(request).getCategoryList().stream().toList();
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }

    // 상대 프로필 > 판매 사품 조회
    public List<SimpleProductDTO> getSellingProducts(Long memberId) {
        try {
            ProductMainRequest request = ProductMainRequest.newBuilder().setMemberId(memberId).build();
            return stub.getSellingProducts(request)
                    .getProductsList()
                    .stream()
                    .map(SimpleProductDTO::from)
                    .toList();
        } catch (StatusRuntimeException e) {
            throw new CustomException(GrpcUtil.extractErrorCode(e));
        }
    }
}
