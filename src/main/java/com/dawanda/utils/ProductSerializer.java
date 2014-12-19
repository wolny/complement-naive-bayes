package com.dawanda.utils;

import com.dawanda.db.Product;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by awolny on 19/12/14.
 */
public class ProductSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(ProductSerializer.class);

    // assumes that there is one file per category and fetches all the products
    public static List<Product> readFromSourceDir(String srcDir) {
        File dir = new File(srcDir);
        LOG.info(String.format("Reading product files from: '%s' ...", dir.toString()));
        List<Product> result = new ArrayList<>();
        for (File file : dir.listFiles()) { // for all the categories
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

    public static void writeToFile(List<Product> products, String destFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(destFile), products);
        } catch (IOException e) {
            LOG.error("Error while saving filteredProducts", e);
        }
    }

    private static List<Product> getProductsFromFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, new TypeReference<List<Product>>() {
        });
    }

    private ProductSerializer() {
    }
}
