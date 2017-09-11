package cn.com.nttdata.arelleperf.threads;

public class RunProc extends Thread {
//    private SharePot res;
    private String fileName;

    public RunProc(String fileName) {
        this.fileName = fileName;
//        this.res = res;
    }

    public void run() {
        try {
//            res.borrowOne();
            Runtime.getRuntime().exec("cmd /k start sqlldr userid=open/open control=" + fileName + " direct=true");
        } catch (Exception e) {
        } finally {
//            res.returnOne();
        }
    }
}
