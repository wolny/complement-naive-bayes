package com.dawanda.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by awolny on 17/12/14.
 */
public interface RowMapper<T> {
    T mapRow(ResultSet resultSet) throws SQLException;
}
