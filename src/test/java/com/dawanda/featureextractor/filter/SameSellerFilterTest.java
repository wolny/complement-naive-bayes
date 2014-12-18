package com.dawanda.featureextractor.filter;

import com.dawanda.db.Product;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SameSellerFilterTest {
    private static final int S1 = 666;
    private static final int S2 = 667;

    private List<Product> getSampleProducts() {
        List<Product> sample = new ArrayList<>();
        sample.add(new Product(1, S1, 111, "random title", "random description"));
        sample.add(new Product(2, S1, 111, "random title", "random description"));
        sample.add(new Product(3, S2, 222, "random title", "random description"));
        sample.add(new Product(4, S2, 222, "random title", "random description"));
        return sample;
    }

    @Test
    public void shouldFilterSameSellersForGivenCategory() {
        SameSellerFilter filter = new SameSellerFilter(1);
        List<Product> products = filter.filterProducts(getSampleProducts());
        Assert.assertTrue(products.size() == 2);
        Assert.assertTrue(Iterables.any(products, new Predicate<Product>() {
            @Override
            public boolean apply(Product input) {
                return input.getSellerId() == S1;
            }
        }));
        Assert.assertTrue(Iterables.any(products, new Predicate<Product>() {
            @Override
            public boolean apply(Product input) {
                return input.getSellerId() == S2;
            }
        }));
    }
}