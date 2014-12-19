package com.dawanda.featureextractor.filter;

import com.dawanda.db.Product;
import com.dawanda.utils.ProductSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Filter a given documents, filters the products using the sequence
 * of {@link com.dawanda.featureextractor.filter.ProductFilter}s and outputs filtered collection
 * of products to the destination directory
 * <p/>
 * Created by awolny on 10/12/14.
 */
public class ProductFilterPipeline {
    private static final Logger LOG = LoggerFactory.getLogger(ProductFilterPipeline.class);

    private final List<ProductFilter> filterPipeline;

    public ProductFilterPipeline(List<ProductFilter> filterPipeline) {
        this.filterPipeline = filterPipeline;
    }

    public ProductFilterPipeline() {
        // by default takes  all product and filter same sellers
        this(Arrays.asList(new RandomSubsetFilter(1.0), new SameSellerFilter(3)));
    }

    public List<Product> filterProducts(List<Product> products) {
        LOG.info(String.format("Filtering %d products with: %s ...", products.size(), filterPipeline));
        for (ProductFilter filter : filterPipeline) {
            products = filter.filterProducts(products);
        }
        return products;
    }

    public List<Product> filterProducts(String srcDir) {
        return filterProducts(ProductSerializer.readFromSourceDir(srcDir));
    }
}
