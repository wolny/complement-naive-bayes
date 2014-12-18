package com.dawanda.featureextractor.filter;

import com.dawanda.db.Product;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        return filterProducts(readFromSourceDir(srcDir));
    }

    public void writeToDestinationDir(List<Product> products, String destDir) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(destDir + "/filteredProducts.json"), products);
        } catch (IOException e) {
            LOG.error("Error while saving filteredProducts", e);
        }
    }

    // assumes that there is one file per category and fetches all the products
    private List<Product> readFromSourceDir(String srcDir) {
        LOG.info(String.format("Reading product files from: '%s' ...", srcDir));
        List<Product> result = new ArrayList<>();
        for (File file : new File(srcDir).listFiles()) { // for all the categories
            if (!file.getName().endsWith("json")) {
                continue;
            }
            LOG.info("Processing file: " + file.getName());
            try {
                result.addAll(getProductsFromFile(file));
            } catch (Exception e) {
                LOG.error("Error processing file: " + file.getName(), e);
            }
        }
        return result;
    }


    private List<Product> getProductsFromFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, new TypeReference<List<Product>>() {
        });
    }

}
