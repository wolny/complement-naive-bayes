package com.dawanda.featureextractor.filter;

import com.dawanda.db.Product;

import java.util.List;

/**
 * General interface for a filter used in the {@link com.dawanda.featureextractor.filter.ProductFilterPipeline}
 * <p/>
 * Created by awolny on 10/12/14.
 */
public interface ProductFilter {
    /**
     * @param products collection of products to be filtered
     * @return filtered collection of products
     */
    List<Product> filterProducts(List<Product> products);
}
