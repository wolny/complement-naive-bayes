package com.dawanda.featureextractor.filter;

import com.dawanda.db.Product;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Created by awolny on 10/12/14.
 */
public class RandomSubsetFilter implements ProductFilter {
    private final double fraction;

    public RandomSubsetFilter(double fraction) {
        this.fraction = fraction;
    }

    @Override
    public List<Product> filterProducts(List<Product> products) {
        int subsetSize = (int) Math.round(products.size() * fraction);
        Collections.shuffle(products);
        return Lists.newArrayList(Iterables.limit(products, subsetSize));
    }
}
