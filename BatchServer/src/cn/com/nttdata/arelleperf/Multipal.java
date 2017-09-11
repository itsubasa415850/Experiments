package cn.com.nttdata.arelleperf;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.nttdata.arelleperf.threads.SharePot;
import cn.com.nttdata.arelleperf.threads.TestSDK;

public final class Multipal {
    private static final String host = "http://localhost:8080/rest/xbrl/";
    private static final String dir = "d:\\arelleperf\\";
    private static final String action = "/validation/xbrl?media=text";
    private static volatile SharePot res = null;
    public static void main(String[] args) {
        try {
            res = new SharePot(5, 5);
            int cnt = 1;
            if(args.length != 2) {
                throw new IllegalArgumentException("请输入测试文件名及乱入线程数。");
            }
            do {
                long startTime = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS");
                TestSDK c = new TestSDK(startTime, host + dir + args[0] + action, res);
                c.setFileName(dir + args[0] + "@" + cnt);
                c.start();
                System.out.println("第" + cnt + "号线程已经启动，开始时间为：" + sdf.format(new Date(startTime)));
                cnt ++;
            } while (cnt <= Integer.parseInt(args[1]));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
