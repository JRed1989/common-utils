package org.red.common.hive;

import org.red.common.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * hive工具类
 * Created by JRed on 2017/6/4.
 */
public class HiveUtils {

    private static final  Logger LOGGER = LoggerFactory.getLogger(HiveUtils.class);

    public Connection getConnection(){
        String hiveDriverClassName = Config.getProperty("hiveDriverClass");
        String url = Config.getProperty("hiveUrl");
        String userName = Config.getProperty("hiveUserName");
        String password = Config.getProperty("hivePassWord");
        return getConnection(hiveDriverClassName,url,userName,password);
    }

    public Connection getConnection(String driverClassName,String url,String userName,String password){
        LOGGER.debug("hive: [hiveDriverClass]={},[hiveUrl]={},[hiveUserName]={},[hivePassWord]={}",driverClassName,url,userName,password);
        Connection connection = null;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url,userName,password);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage(),e);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(),e);
        }
        return connection;
    }

    public void close(Connection connection,PreparedStatement ps,ResultSet resultSet){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(),e);
            }
        }

        if(ps != null){
            try {
                ps.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
    }

    public void update(Connection connection,PreparedStatement preparedStatement,String sql){
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
