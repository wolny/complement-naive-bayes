package com.dawanda.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by awolny on 17/12/14.
 */
public class RowToProduct implements RowMapper<Product> {
    private final int categoryId;

    public RowToProduct(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public Product mapRow(ResultSet resultSet) throws SQLException {
        int productId = resultSet.getInt("id");
        int sellerId = resultSet.getInt("seller");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        return new Product(productId, sellerId, categoryId, title, description);
    }
}
