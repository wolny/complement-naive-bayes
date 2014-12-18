package com.dawanda.db;

/**
 * Created by awolny on 18/12/14.
 */
public interface ProductProvider {
    /**
     * Fetches products by category from a given data source (depending on the implementation)
     * and saves products for a given category in JSON file with the name 'category-<id>.json'
     *
     * @param folder dir to which saves product files
     */
    void fetchTo(String folder);
}
