package cn.com.nttdata.arelleperf.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class TestClass extends APerf {
    private String fileName;
    private SharePot res;
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public TestClass(long startTime, String uri, SharePot res) {
        super(startTime, uri);
        this.res = res;
    }

    public void run() {
        BufferedReader rd = null;
        HttpURLConnection conn = null;
        URL url = null;
        String[] strs = fileName.split("@");
        String line;
        try {
            res.borrowOne();
            //开始运行
            url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() != 200) {
                System.err.println(strs[1] + "号线程已经执行完毕，但发生错误，错误为" + conn.getResponseMessage());
                throw new IOException(conn.getResponseMessage());
            }
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
              while ((line = rd.readLine()) != null) {
                  if(!"".equals(line)) {
                      System.out.println(fileName + "的读取结果为：" + line);
                  }
              }
        } catch (Exception e) {
            System.err.println(strs[1] + "号线程已经执行完毕，但发生错误，错误为" + e.toString());
            return;
        } finally {
            //最终处理，先把该关的都关了然后统计运行时间，并通知JVM回收资源。
            try {
                if(rd != null) {
                    rd.close();
                }
                if(conn != null) {
                    conn.disconnect();
                }
                finalize();
            } catch (Throwable t) {}
        }
        if(strs.length != 1) {
            System.out.println(strs[1] + "号线程已经执行完毕，测试" + strs[0] + "文件所耗费的总时间为：" + totalElapsed(System.currentTimeMillis()));
        } else {
            System.out.println("测试" + fileName + "所耗费的总时间为：" + totalElapsed(System.currentTimeMillis()));
        }
        res.returnOne();
    }
    
}
