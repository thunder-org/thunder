package org.conqueror.common.utils.db;

import java.sql.*;
import java.util.Properties;


public class DBConnector {

    private Connection connection;

    public DBConnector(String jdbcUrl, Properties properties) throws SQLException {
        this.connection = DriverManager.getConnection(jdbcUrl, properties);
    }

    public ResultSet select(String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeQuery();
        }
    }

    public int count(String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet result = statement.executeQuery();
            if (result.next()) return result.getInt(1);
        }
        return 0;
    }

    public boolean create(String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.execute();
        }
    }

    public boolean exist(String table) throws SQLException {
        try (ResultSet rs = connection.getMetaData().getTables(null, null, table, null)) {
            while (rs.next()) {
                String name = rs.getString("TABLE_NAME");
                if (name != null && name.equals(table)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int insert(String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null) connection.close();
    }

}
