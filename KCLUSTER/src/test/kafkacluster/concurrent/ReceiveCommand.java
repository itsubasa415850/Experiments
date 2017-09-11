package test.kafkacluster.concurrent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import test.kafkacluster.functions.DBManipulator;
import test.kafkacluster.functions.Functions;


public class ReceiveCommand implements Runnable {
    private Connection conn;
    private String sql;
    private int threadNo;
    private String desc;
    
    
    public ReceiveCommand(Connection conn, String sql, int threadNo,
            String desc) {
        super();
        this.conn = conn;
        this.sql = sql;
        this.threadNo = threadNo;
        this.desc = desc;
    }


    public void run() {
//        final String head = "<data>";
//        final String tail = "</data>";
    	System.out.println("线程" + threadNo + " STARTED：" + new SimpleDateFormat("yyyymmdd hh:mm:ss").format(new Date(System.currentTimeMillis())));
        final long overall = System.currentTimeMillis();
        long started = overall;
        System.out.println("线程" + threadNo + "已于" +
                new SimpleDateFormat().format(new Date(started)) + "开始执行。");
        StringBuilder sb = new StringBuilder();
        PreparedStatement ps = null;
        OutputStream os = null;
        BufferedWriter bw = null;
        ResultSet rs = null;
        int checkPoint = 0;
        File fi = null;
        final String f = desc + "/thread_" + threadNo + ".xml";
        try {
            ps = conn.prepareStatement(sql);
            ps.setFetchSize(100);
            started = System.currentTimeMillis();
            rs = ps.executeQuery();
            System.out.println("线程" + threadNo + "执行SQL的时间：" + (System.currentTimeMillis() - started));
            started = System.currentTimeMillis();
            fi = new File(f);
            os = new FileOutputStream(fi);
            bw = new BufferedWriter(new OutputStreamWriter(os));
//            bw.write(head);
            while(rs.next()) {
                checkPoint++;
                for (int i = 1; i <= 30; i++) {
//                    sb.append("<field" + "i" + ">");
                    sb.append(rs.getString(i));
//                    sb.append("</field" + "i" + ">");
//                    sb.append("\r\n");
                    sb.append(",");
                }
                sb.append("\r\n");
                if(checkPoint % 10000 == 0) {
                    bw.write(sb.toString());
                    sb = new StringBuilder();
                    System.out.println("线程" + threadNo + "现已生成" + checkPoint + "条数据。");
                }
            }
//            bw.write(tail);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManipulator.closeOpenedResultSet(rs);
            DBManipulator.closePreparedStatement(ps);
            if(os != null) {
                try {
                    os.close();
                } catch (Exception e2) {
                }
            }
            if(bw != null) {
                try {
                    bw.close();
                } catch (Exception e2) {
                }
            }
        }
        System.out.println("线程" + threadNo + "生成数据文件所需的时间：" + (System.currentTimeMillis() - started));
        started = System.currentTimeMillis();
        Functions.snappyAsFile(fi, f + ".snappy");
        System.out.println("线程" + threadNo + "生成压缩文件所需的时间：" + (System.currentTimeMillis() - started));
        System.out.println("线程" + threadNo + "的总执行时间：" + (System.currentTimeMillis() - overall));
        System.out.println("线程" + threadNo + " TERMINATED：" + new SimpleDateFormat("yyyymmdd hh:mm:ss").format(new Date(System.currentTimeMillis())));
    }
}
