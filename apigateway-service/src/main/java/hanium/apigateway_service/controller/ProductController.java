package hanium.apigateway_service.controller;

import hanium.apigateway_service.dto.product.RegisterProductRequestDTO;
import hanium.apigateway_service.grpc.ProductGrpcClient;
import hanium.common.proto.product.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductGrpcClient productGrpcClient;

    @PostMapping("/register")
    public ProductResponse registerProduct(@RequestBody RegisterProductRequestDTO requestDTO) {
        return productGrpcClient.registerProduct(requestDTO);
    }
}