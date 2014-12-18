package com.dawanda.db;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ProductTest {
    private List<Product> getTestProducts() {
        Product p1 = new Product(1, 666, 123, "Funky Gold Neckless", "Mr. T approved");
        Product p2 = new Product(2, 666, 123, "Boston Red Socks Full Cap", "Red and awesome!!!");
        return Arrays.asList(p1, p2);
    }

    @Test
    public void shouldBeJsonSerializableToFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("testProduct.json");
        objectMapper.writeValue(file, getTestProducts());

        List<Product> result = objectMapper.readValue(file, new TypeReference<List<Product>>() {
        });

        Assert.assertTrue(result.get(0).getId() == 1);
        Assert.assertTrue(result.get(1).getId() == 2);

        file.delete();
    }
}