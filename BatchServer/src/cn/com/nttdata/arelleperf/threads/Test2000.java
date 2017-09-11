package cn.com.nttdata.arelleperf.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.com.nttdata.batchserver.db.Controller;
import cn.com.nttdata.batchserver.functions.Function;
import cn.com.nttdata.batchserver.functions.MyILog;
import cn.com.nttdata.socket.CallBack;
import cn.com.nttdata.xbrl.common.XbrlProperties;
import cn.com.nttdata.xbrl.validateInstance.IXbrlController;
import cn.com.nttdata.xbrl.validationControl.XbrlController;

public class Test2000 extends APerf {
    private CallBack server = null;
    private String fileName;
    private String tax;
    private String ruleSets;
    private String out;
    private InputStream is;
    private String config;
    private String log;
    private boolean ended;
    protected Test2000(long startTime, String uri) {
        super(startTime, uri);
    }

    
    /**
     * @param ended the ended to set
     */
    public void setEnded(boolean ended) {
        this.ended = ended;
    }


    /**
     * @param server the server to set
     */
    public void setServer(CallBack server) {
        this.server = server;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Test2000(long startTime, String uri, String tax, String ruleSets, String out, InputStream is) {
        super(startTime, uri);
        this.is = is;
        this.tax = tax;
        this.ruleSets = ruleSets;
        this.out = out;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void run() {
        OutputStream os = null;
        StringBuffer sb = new StringBuffer();
        //有校验出错的切片
        if(server.others()) {
            return; 
       }
        try {
            String ruleset = Function.composeRuleSet(tax, ruleSets.concat(File.separator)
                    .concat(Long.toString(Function.randomIt())).concat(File.separator), fileName);
            MyILog xbrlLog = new MyILog(log);
            IXbrlController controller = new XbrlController();
            String pid = "";
            XbrlProperties.loadProperties();
            pid = controller.start(xbrlLog, "");
            controller.readValidationConfig(pid, ruleset, config, is);
            controller.validate(pid);
            controller.cancel(pid);
            List<String> lst = controller.getErrList();
            String returnFile = out + "/RESULT_" + fileName + ".txt";
            int length = lst.size();
            for (int i = 0; i < length; i++) {
                sb.append(lst.get(i).concat("\r\n"));
            }
            Controller con = server.getCon();
            if(sb.length() != 0) {
                os = new FileOutputStream(new File(returnFile));
                byte[] bb = sb.toString().getBytes("UTF-8");
                os.write(bb);
                //回调，告诉接收器本次校验是有错误的。
                server.tell(false);
            } else {
                //回调，告诉接收器本次校验没有错误。
                server.tell(true);
            }
            if(ended && !server.others()) {
                //由于执行最后一个切片流的线程不一定是最后一个执行的线程，因此尽量等待其他线程执行完成。
                Thread.sleep(4000);
                con.modify(server.getUuid() + "_" + server.getNum(), "OK");
                con.commitSession();
                server.setUuid(null);
            }
            System.out.println("已经完成于：" + new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS").format(new Date(System.currentTimeMillis())));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e2) {
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e2) {
            }
        }
    }
}
