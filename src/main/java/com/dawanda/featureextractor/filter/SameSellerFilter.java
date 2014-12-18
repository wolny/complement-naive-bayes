package com.dawanda.featureextractor.filter;

import com.dawanda.db.Product;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.*;

/**
 * Takes one product per seller per category in order to avoid 'seller bias'
 * <p/>
 * Created by awolny on 10/12/14.
 */
public class SameSellerFilter implements ProductFilter {
    private final int maxProductCountPerSellerPerCategory;

    public SameSellerFilter(int maxProductCountPerSellerPerCategory) {
        this.maxProductCountPerSellerPerCategory = maxProductCountPerSellerPerCategory;
    }

    @Override
    public List<Product> filterProducts(List<Product> products) {
        Map<Integer, List<Product>> groupedProducts = groupByCategory(products);
        List<Product> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Product>> entry : groupedProducts.entrySet()) {
            result.addAll(filterSameSellers(entry.getValue()));
        }
        return result;
    }

    private List<Product> filterSameSellers(List<Product> products) {
        Collections.shuffle(products); // get random products
        Multiset<Integer> sellers = HashMultiset.create();
        List<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (sellers.count(product.getSellerId()) < maxProductCountPerSellerPerCategory) {
                sellers.add(product.getSellerId());
                result.add(product);
            }
        }
        return result;
    }

    private static Map<Integer, List<Product>> groupByCategory(List<Product> products) {
        Map<Integer, List<Product>> result = new HashMap<>();
        for (Product product : products) {
            List<Product> catProds = result.get(product.getCategory());
            if (catProds == null) {
                catProds = new ArrayList<>();
                result.put(product.getCategory(), catProds);
            }
            catProds.add(product);
        }
        return result;
    }
}
