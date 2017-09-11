package cn.com.nttdata.socket;
public class ValServer extends PoolServer {

    public void run() {
        try {
            while(true) {
                if(ended) {
                    //本次上传均已完成，将清空必要的变量以准备下次连接。
                    Thread.sleep(1000);
                    con.record(uuid.concat("_").concat(num), "NG");
                    ended = false;
                    con.commitSession();
                } else {
                    serve();
                }
            }
        } catch (Exception e) {
        }
    }
    
    public ValServer(int port, String valOut, String func, String ruleSet,
            String config, String csvOut, String dbsvr, String open, String work, String num) {
        super(port, valOut, func, ruleSet, config, csvOut, dbsvr, open, work, num);
    }
}
