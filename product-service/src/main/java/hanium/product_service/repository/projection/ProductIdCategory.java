package hanium.product_service.repository.projection;

import hanium.product_service.domain.Category;

public interface ProductIdCategory {
    
    Long getId();

    Category getCategory();
}
