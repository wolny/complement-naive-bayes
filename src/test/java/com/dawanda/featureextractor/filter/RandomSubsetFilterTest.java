package com.dawanda.featureextractor.filter;

import com.dawanda.db.Product;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RandomSubsetFilterTest {
    private static final double FR = 0.8;

    private List<Product> getSampleProducts() {
        Product p = new Product(1, 11, 111, "random title", "random description");
        return Arrays.asList(p, p, p, p, p);
    }

    @Test
    public void shouldReturnFractionOfProducts() {
        RandomSubsetFilter filter = new RandomSubsetFilter(FR);
        List<Product> products = filter.filterProducts(getSampleProducts());
        Assert.assertEquals(products.size(), 4);
    }
}