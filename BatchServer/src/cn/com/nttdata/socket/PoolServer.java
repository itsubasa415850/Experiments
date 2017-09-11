package cn.com.nttdata.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import cn.com.nttdata.arelleperf.threads.Test2000;
import cn.com.nttdata.arelleperf.threads.TestParse;
import cn.com.nttdata.batchserver.db.Controller;
import cn.com.nttdata.batchserver.functions.Function;

abstract class PoolServer implements CallBack {
    protected volatile String uuid = null; 
    protected static Logger logger = Logger.getLogger(PoolServer.class);
    protected int port;
    protected boolean ended = false;
    //表示该实例文档中所有切片都是有效的。
    protected List<Boolean> list = new ArrayList<Boolean>();
    private String config;
    protected String num;
    protected String work;
    protected String csvOut;
    protected String ruleSetOrTable;
    protected String valOutOrCtlOut;
    private boolean loadOver = false;
    private String func;
    protected Controller con = new Controller(logger);
    private ExecutorService es = Executors.newFixedThreadPool(5);
    
    /**
     * @return the num
     */
    public String getNum() {
        return num;
    }
    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }
    public void tell(boolean isValid) {
        list.add(isValid);
    }
    public void loadOver(boolean over) {
        loadOver = over;
    }

    public boolean over() {
        return loadOver;
    }
    /**
     * @return the list
     */
    protected List<Boolean> getList() {
        return list;
    }

    /**
     * @return the con
     */
    public Controller getCon() {
        return con;
    }

    public boolean others() {
        return list.size() !=0 && list.contains(false);
    }

    protected PoolServer(int port, String valOut, String func, String ruleSet, String config, String csvOut, String dbsvr, String open, String work, String num) {
        this.work = work;
        this.port = port;
        this.csvOut = csvOut;
        this.ruleSetOrTable = ruleSet;
        this.func = func;
        this.config = config;
        this.valOutOrCtlOut = valOut;
        this.csvOut = csvOut;
        this.num = num;
        con.makeConnection(dbsvr + ":1521", open, open, open);
    }
    
    protected void serve() {
        ServerSocket server = null;
        Socket socket = null;
        try {
            server = new ServerSocket(port, 50, InetAddress.getLocalHost());
            logger.info("开始监听。。。");
            socket = server.accept();
            receiveFile(socket);
        } catch (Exception e) {
            e.printStackTrace();
            if(con != null) {
                con.rollbackSession();
            }
        } finally {
            if(server != null) {
                try {
                    server.close();
                } catch (Exception e2) {
                }
            }
        }
    }
    
    protected void receiveFile(Socket socket) throws IOException {
        byte[] inputByte = null;
        int length = 0;
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = null;
        DataInputStream din = null;
        boolean start = true;
        String eof = null;
        try {
            bos = new ByteArrayOutputStream();
            din = new DataInputStream(socket.getInputStream());
            inputByte = new byte[1024];
            logger.info("有链接，开始接收数据并执行相应过程。");
            while (true) {
                if (din != null) {
                    length = din.read(inputByte, 0, inputByte.length);
                }
                if (length == -1) {
                    break;
                }
                if(start) {
                    bos.write(inputByte, 41, length - 41);
                    uuid = new String(inputByte, 2, 36);
                    eof = new String(inputByte, 38, 3);
                    if("EOF".equalsIgnoreCase(eof)) {
                        ended = true;
                    }
                    start = false;
                } else {
                    bos.write(inputByte, 0, length);
                }
                bos.flush();
            }
            //把实例文档切片做成字节输入流进行下一步的操作。
            bis = new ByteArrayInputStream(bos.toByteArray());
            logger.info("完成接收了。");
            //做校验
            if("1".equals(func)) {
                logger.info("开始校验过程。");
                Test2000 val = new Test2000(0l, "", "", ruleSetOrTable, valOutOrCtlOut, bis);
                if(ended) {
                    //高度依赖平台，所以起不了大作用。
                    val.setPriority(Thread.MIN_PRIORITY);
                    val.setEnded(true);
                }
                val.setFileName(String.valueOf(Function.randomIt()));
                val.setConfig(config);
                val.setServer(this);
                es.execute(val);
            } else {
                //做解析
                logger.info("开始解析过程。");
                TestParse par = new TestParse(0l, "", logger, csvOut, bis, "", "", uuid, num);
                es.execute(par);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                }
            }
            if (din != null) {
                try {
                    din.close();
                } catch (Exception e) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
