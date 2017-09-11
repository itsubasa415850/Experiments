package cn.com.nttdata.arelleperf;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.nttdata.arelleperf.threads.SharePot;
import cn.com.nttdata.arelleperf.threads.Test2000;

public class Run2000 {
    private static volatile SharePot res = null;
    private static final String tax = "D:\\atax\\A-100000000032057-20151231.xsd";
    private static final String ruleSets = "D:\\$BATCHSERVER$\\";
    private static final String out = "D:\\out\\";
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS");
        System.out.println("测试已经于" + sdf.format(new Date(startTime)) + "开始。");
        try {
            res = new SharePot(20, 20);
            int cnt = 0;
            if(args.length != 1) {
                throw new IllegalArgumentException("请输入测试文件所在目录及文件总数。");
            }
            File dir = new File(args[0]);
            String[] files = dir.list(new FilenameFilter() {
                   public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            });
            for (String file : files) {
                cnt++;
                res.borrowOne();
//                Test2000 test = new Test2000(startTime, args[0] + File.separator + file, res, tax, ruleSets, out);
//                test.setFileName(args[0] + File.separator + file + "@" + cnt);
//                test.start();
//                System.out.println("第" + cnt + "号线程已经启动，测试的文件为：" + file + "，开始时间为：" + sdf.format(new Date(startTime)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("测试已结束，总共用时：" + (System.currentTimeMillis() - startTime) + "。");
    }

}
