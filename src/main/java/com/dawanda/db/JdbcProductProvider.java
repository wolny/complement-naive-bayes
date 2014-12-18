package com.dawanda.db;

import com.dawanda.utils.CategoryUtils;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mchange.v2.c3p0.DataSources;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by awolny on 08/12/14.
 */
public class JdbcProductProvider implements ProductProvider {
    static final int MAX_THREADS = 32;
    private final List<Integer> categoryIds;
    private final DataSource dataSource;
    private final String sql;

    public JdbcProductProvider(Properties properties, List<Integer> categoryIds) {
        this.categoryIds = ImmutableList.copyOf(categoryIds);
        sql = properties.getProperty("db.sql");
        try {
            this.dataSource = configureDataSource(properties);
        } catch (SQLException e) {
            throw Throwables.propagate(e);
        }
    }

    public JdbcProductProvider(String propertiesFile, List<Integer> categoryIds) throws IOException {
        FileInputStream fis = new FileInputStream(propertiesFile);
        Properties properties = new Properties();
        properties.load(fis);
        this.categoryIds = ImmutableList.copyOf(categoryIds);
        sql = properties.getProperty("db.sql");
        try {
            this.dataSource = configureDataSource(properties);
        } catch (SQLException e) {
            throw Throwables.propagate(e);
        }
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public DataSource getDataSource() {
        return dataSource;
    }


    public String getSql() {
        return sql;
    }

    @Override
    public void fetchTo(String outputDir) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        DataSource ds = getDataSource();
        String sql = getSql();
        for (int categoryId : getCategoryIds()) {
            RowToProduct rowMapper = new RowToProduct(categoryId);
            FetchProductsForCategory command = new FetchProductsForCategory(categoryId, ds, sql, rowMapper, outputDir);
            executor.execute(command);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES); // wait 10 minutes
        } catch (InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        JdbcProductProvider productProvider = new JdbcProductProvider("db.properties", CategoryUtils.parseCategoryIds());
        productProvider.fetchTo("./productsEN");
    }


    private DataSource configureDataSource(Properties properties) throws SQLException {
        String dbUrl = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        DataSource unpooledDataSource = DataSources.unpooledDataSource(dbUrl, username, password);
        Map options = ImmutableMap.of("maxPoolSize", MAX_THREADS);
        return DataSources.pooledDataSource(unpooledDataSource, options);
    }

}
