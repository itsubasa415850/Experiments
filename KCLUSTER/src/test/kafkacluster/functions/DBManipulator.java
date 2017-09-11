package test.kafkacluster.functions;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public final class DBManipulator {
    /**
     ** 函 数 名 :  DBManipulator
     ** 功能描述： 构造方法
     ** 输入参数:
     **
     ** 返 回 值:
     **
     ** 异 常：
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    private DBManipulator() {
        // 本类不允许被实例化，只能作为工具类操纵数据库对象。
    }
    /**
     ** 函 数 名 : flushConnection
     ** 功能描述： 清空数据库连接
     ** 输入参数: Connection conn 数据库连接对象
     **
     ** 返 回 值:void
     **
     ** 异 常：
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static void flushConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    @Deprecated
    /**
     ** 函 数 名 : getConnection
     ** 功能描述： 获取数据库连接(非推荐)
     ** 输入参数:
     **
     ** 返 回 值:Connection 连接
     **
     ** 异 常：Exception 连接失败异常
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static Connection getConnection()
            throws Exception {
        Connection conn = null;
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/pool");
            conn = ds.getConnection();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new Exception(e.toString() + e.getMessage(), e);
        }
        return conn;
    }
    /**
     ** 函 数 名 : getConnection
     ** 功能描述： 获取数据库连接
     ** 输入参数:
     **
     ** 返 回 值:Connection 连接
     **
     ** 异 常：Exception 连接失败异常
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static Connection getConnection(String dataSrc) throws Exception {
        Connection conn = null;
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(dataSrc);
            conn = ds.getConnection();
            conn.setAutoCommit(true); 
        } catch (Exception e) {
            throw new Exception(e.toString() + e.getMessage(), e);
        }
        return conn;
    }
    
    @Deprecated
    /**
     ** 函 数 名 : getConnection
     ** 功能描述： 获取数据库连接(非推荐)
     ** 输入参数: String serverInfo 数据库地址,
     **          String serviceInfo 数据库名
     **          String user 用户名
     **          String password 密码
     **
     ** 返 回 值:Connection 连接
     **
     ** 异 常：Exception 连接失败异常
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static Connection connectTo(String serverInfo, String serviceInfo,
            String user, String password) throws Exception {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@"
                    + serverInfo + ":" + serviceInfo, user, password);
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new Exception(e.toString() + e.getMessage(), e);
        }
        return conn;
    }
    /**
     ** 函 数 名 : commitSession
     ** 功能描述： 数据提交
     ** 输入参数: Connection conn 数据库连接对象
     **
     ** 返 回 值:void
     **
     ** 异 常：
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static void commitSession(Connection conn) {
//        if (conn != null) {
//            try {
//            	//数据提交
//                conn.commit();
//            } catch (SQLException e) {
//            }
//        }
    }
    /**
     ** 函 数 名 : rollBackSession
     ** 功能描述： 数据回滚
     ** 输入参数: Connection conn 数据库连接对象
     **
     ** 返 回 值:void
     **
     ** 异 常：
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static void rollBackSession(Connection conn) {
//        if (conn != null) {
//            try {
//            	//数据回滚
//                conn.rollback();
//            } catch (SQLException e) {
//            }
//        }
    }
    /**
     ** 函 数 名 : closeOpenedResultSet
     ** 功能描述： 关闭数据库结果集对象
     ** 输入参数: ResultSet rs 结果集对象
     **
     ** 返 回 值:void
     **
     ** 异 常：
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static void closeOpenedResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
            }
        }
    }
    /**
     ** 函 数 名 : closePreparedStatement
     ** 功能描述： 关闭SQL预处理对象
     ** 输入参数: PreparedStatement ps 预处理SQL对象
     **
     ** 返 回 值:void
     **
     ** 异 常：Exception
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static void closePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     ** 函 数 名 : connectionPoolBack
     ** 功能描述： 连接放回连接池操作
     ** 由于是初始化的连接池，这里调用<code>java.sql.Connection.close()</code>方法<br>
     ** 是一个让连接回池的实现，并不会真正关闭连接。
     ** 输入参数: Connection conn 数据库连接对象
     **
     ** 返 回 值:void
     **
     ** 异 常：
     ** 程序开发者: 郭鹏
     ** 编写日期: 2016 . 05 . 10
     ** Modifications:
     **=============================================
     **日期          开发人员    主题
     ** ----          ----------       --------
     */
    public static void connectionPoolBack(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
            	//System.out.println(e.getMessage());
            }
        }
    }
}
