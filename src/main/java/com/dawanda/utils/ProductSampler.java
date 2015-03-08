package com.dawanda.utils;

import com.dawanda.db.Product;
import com.dawanda.featureextractor.filter.ProductFilter;
import com.dawanda.featureextractor.filter.ProductFilterPipeline;
import com.dawanda.featureextractor.filter.RandomSubsetFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Takes random sample of all the products from the given dir and writes it to the destination dir.
 * <p/>
 *
 * @author slo
 */
public class ProductSampler {
    public static void sampleProduct(String srcDir, String destFile, double fraction) {
        ProductFilterPipeline filter = new ProductFilterPipeline(Arrays.<ProductFilter>asList(new RandomSubsetFilter(fraction)));
        List<Product> products = filter.filterProducts(srcDir);
        ProductSerializer.writeToFile(products, destFile);
    }

    public static void main(String[] args) {
        String srcDir = System.getProperty("user.home") + "/.cbayes/train";
        String destFile = System.getProperty("user.home") + "/.cbayes/test/sample.json";
        // take random 5%
        sampleProduct(srcDir, destFile, 0.05);
    }
}
