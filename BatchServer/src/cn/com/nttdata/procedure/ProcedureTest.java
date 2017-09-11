package cn.com.nttdata.procedure;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;

public class ProcedureTest {

    public static void main(String[] args) {
        Connection conn = null;
        final String serverInfo = "172.25.59.115:1521";
        final String serviceInfo = "open";
        final String user = "open";
        final String password = "open";
        CallableStatement statement = null;
        long start = 0l;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@"
                    + serverInfo + ":" + serviceInfo, user, password);
            conn.setAutoCommit(false);
            statement = conn.prepareCall("{ call proc(?) }");
//            statement.setString(1, "产品A_123456");
            String str;
            String str1;
            start = System.currentTimeMillis();
            for (int i = 1; i < 100000; i++) {
                str = String.valueOf(i);
                str1 = String.valueOf(100000 - i);
                statement.setString(1, str.concat("_").concat(str1));
                statement.addBatch();
            }
            statement.executeBatch();
            conn.commit();
            System.out.println(System.currentTimeMillis() - start);
        } catch (Exception e) {
//            throw new ConnectionFailedException(e.toString() + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (Exception e2) {
                    // TODO: handle exception
                }
            }
            if(statement != null) {
                try {
                    statement.close();
                } catch (Exception e2) {
                }
            }
        }
    }
}
