package cn.com.nttdata.arelleperf.threads;

import cn.com.nttdata.arelleperf.dts.DtsWrapper;

public final class TestSDK extends APerf {
    private String fileName;
    private SharePot res;
    protected TestSDK(long startTime, String uri) {
        super(startTime, uri);
    }
    public TestSDK(long startTime, String uri, SharePot res) {
        super(startTime, uri);
        this.res = res;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void run() {
        DtsWrapper wrapper = null;
        String[] strs = fileName.split("@");
        try {
            res.borrowOne();
            //开始运行
            wrapper = DtsWrapper.getInstance(strs[0], 2);
        } catch (Exception e) {
            System.err.println(strs[1] + "号线程已经执行完毕，但发生错误，错误为" + e.toString());
            return;
        } finally {
            //最终处理，先把该关的都关了然后统计运行时间，并通知JVM回收资源。
            try {
                finalize();
                if(wrapper != null) {
                    wrapper.closeDts();
                }
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
