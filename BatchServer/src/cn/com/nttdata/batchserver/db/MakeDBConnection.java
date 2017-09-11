package cn.com.nttdata.batchserver.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import cn.com.nttdata.batchserver.errors.ConnectionFailureError;

final class MakeDBConnection {
    private MakeDBConnection() {
        //NOP
    }

    static void flushConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch(SQLException e) {}
        }
    }

    @Deprecated
    static Connection getInstance() {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@172.16.131.74:1521:orcl", "xbrl01", "xbrl01");
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new ConnectionFailureError(e.toString() + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new ConnectionFailureError(e.toString() + e.getMessage(), e);
        } finally {}
        return conn;
    }

    static Connection connectTo(String serverInfo,
                                                    String serviceInfo,
                                                    String user,
                                                    String password) {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@" + serverInfo + ":" + serviceInfo, user, password);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new ConnectionFailureError(e.toString() + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new ConnectionFailureError(e.toString() + e.getMessage(), e);
        } finally {}
        return conn;
    }

    static void commitSession(Connection conn) {
        if(conn != null) {
            try {
                conn.commit();
            } catch(SQLException e) {}
        }
    }

    static void rollBackSession(Connection conn) {
        if(conn != null) {
            try {
                conn.rollback();
            } catch(SQLException e) {}
        }
    }
}
