package hanium.product_service.repository.projection;

public interface ProductWithFirstImage {

    Long getProductId();

    String getTitle();

    Long getPrice();

    String getImageUrl();
}
