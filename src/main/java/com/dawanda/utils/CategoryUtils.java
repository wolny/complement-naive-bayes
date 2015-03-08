package com.dawanda.utils;

import com.google.common.base.Throwables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryUtils {
    public static Map<String, String> CAT_TO_NAME = CategoryUtils.readCategories();

    public static String getName(String categoryId) {
        return CAT_TO_NAME.get(categoryId);
    }

    public static HashMap<String, String> readCategories() {
        HashMap<String, String> catNameHash = new HashMap<>();
        BufferedReader buf = new BufferedReader(new InputStreamReader(
                CategoryUtils.class.getClassLoader().getResourceAsStream("catIdName.csv")));
        String line;
        try {
            while ((line = buf.readLine()) != null) {
                String[] catName = line.split(",");
                catNameHash.put(catName[0], catName[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return catNameHash;
    }

    public static List<Integer> parseCategoryIds() {
        List<Integer> parentCategoryIds = new ArrayList<>();
        BufferedReader br = null;
        try {
            // csv file which keeps ordered list of category ids and number of products that belong to the category
            InputStream resourceAsStream = CategoryUtils.class.getClassLoader().getResourceAsStream("valid_product_distribution.csv");
            InputStreamReader in = new InputStreamReader(resourceAsStream);
            br = new BufferedReader(in);
            String line;
            br.readLine(); // consume header
            while ((line = br.readLine()) != null) {
                String categoryIdStr = line.split(",")[0];
                Integer categoryId = Integer.parseInt(categoryIdStr);
                parentCategoryIds.add(categoryId);
            }
            return parentCategoryIds;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
            }
        }
    }

    private CategoryUtils() {
    }
}
