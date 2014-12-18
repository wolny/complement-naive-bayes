package com.dawanda.db;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves the products for a given category from the database and serializes the product list to a JSON file.
 * JSON file is of the form {@code category-<id>.json}, where {@code id} is an ID of the category.
 * <p/>
 * Created by awolny on 09/12/14.
 */
public class FetchProductsForCategory implements Runnable {
    static final Logger LOG = LoggerFactory.getLogger(FetchProductsForCategory.class);
    private final int categoryId;
    private final DataSource dataSource;
    private final String sql;
    private final RowMapper<Product> rowMapper;
    private final String outputDir;

    public FetchProductsForCategory(int categoryId, DataSource dataSource, String sql, RowMapper<Product> rowMapper, String outputDir) {
        Preconditions.checkNotNull(dataSource);
        Preconditions.checkNotNull(sql);
        Preconditions.checkNotNull(rowMapper);
        Preconditions.checkNotNull(outputDir);
        this.categoryId = categoryId;
        this.dataSource = dataSource;
        this.sql = sql;
        this.rowMapper = rowMapper;
        this.outputDir = outputDir;
    }

    @Override
    public void run() {
        LOG.info("Getting products for category: " + categoryId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String path = String.format("%s/category-%d.json", outputDir, categoryId);
            LOG.info("Serializing products to: " + path);
            objectMapper.writeValue(new File(path), fetchProducts());
        } catch (IOException e) {
            LOG.error("Cannot serialize products for category: " + categoryId, e);
        }
    }

    private List<Product> fetchProducts() {
        List<Product> result = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, categoryId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                result.add(rowMapper.mapRow(rs));
                if (result.size() % 100 == 0) {
                    LOG.info(String.format("Fetched %d products for category %d", result.size(), categoryId));
                }
            }
            rs.close();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw Throwables.propagate(e);
                }
            }
        }
        return result;
    }
}
