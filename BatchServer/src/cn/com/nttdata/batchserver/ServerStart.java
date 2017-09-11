package cn.com.nttdata.batchserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import cn.com.nttdata.batchserver.functions.Function;
import cn.com.nttdata.socket.ParServer;

public class ServerStart extends Thread {
    private static Logger logger = Logger.getLogger(ServerStart.class.getClass());
    private String[] args;
    private  ParServer server;
    public ServerStart(String csvOut, String table, String ctrl, String work,
            String oneThird, String commit, String num) {
        args = new String[7];
        args[0] = csvOut;
        args[1] = table;
        args[2] = ctrl;
        args[3] = work;
        args[4] = oneThird;
        args[5] = commit;
        args[6] = num;
    }

    /**
     * @param server the server to set
     */
    public void setServer(ParServer server) {
        this.server = server;
    }

    public void run() {
        proc();
    }

    public void proc() {
        List<String> fileList = new ArrayList<String>();
        File processFile = null;
        //args0:d:\ftp\1
        //args1:1
        //args2:d:\ctrl\
        //args3:d:\work\1
        //args4:=streams/3
        //args5:commit
        logger.info("入库线程已经启动。");
        File ftpDir = new File(args[0]);
        File workDir = new File(args[3]);
        int cnt = 0;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS");
        String fileName = null;
//        logger.info("开始时间：" + sdf.format(new Date(System.currentTimeMillis())));
        while(true) {
            try {
                //主线程用来调度本地方法启动SQLLDR。
                Thread.sleep(3000);
                String[] zips = ftpDir.list();
                int length = zips.length;
                if(length == 0) {
                    break;
                }
                for(int idx = 0; idx < length; idx++) {
                    cnt++;
                    if(cnt > Integer.parseInt(args[4])) {
                        fileName = process(args[1], args[2], fileList);
                        //每次生成完控制文件之后要把该表清空。
                        //本地方法起CMD或者SHELL
                        //SQL>ALTER   TABLE   RESULTXT   nologging;
                        //使用之前先关闭表的重做日志。。。
                        logger.info("启动SQLLDR。");
                        Runtime.getRuntime().exec("cmd /k start sqlldr userid=open/open@open control=" + fileName +
                                " rows=" + args[5] + " bindsize=40960000 readsize=81920000 parallel=true");
                        cnt = 0;
                        fileList.clear();
                    } else {
                        processFile = new File(ftpDir.getAbsoluteFile() + File.separator + zips[idx]);
                        FileUtils.moveFileToDirectory(processFile, workDir, false);
                        fileList.add(workDir.getAbsoluteFile() + File.separator + zips[idx]);
                    }
                }
                //结尾处理
                if(fileList.size() != 0) {
                    logger.info("启动SQLLDR。");
                    fileName = process(args[1], args[2], fileList);
                    Runtime.getRuntime().exec("cmd /k start sqlldr userid=open/open@open control=" + fileName +
                            " rows=" + args[5] + " bindsize=40960000 readsize=81920000 parallel=true");
                    fileList.clear();
                    cnt = 0;
                    server.loadOver(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String process(String table, String ctrl, List<String> fileList) {
        OutputStream os = null;
        File ctrlF = new File(ctrl);
        if(!ctrlF.exists()) {
            ctrlF.mkdir();
        }
        String str = ctrl + File.separator + Function.randomIt() + ".ctl";
        File ctrlFile = new File(str);
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("LOAD DATA ");
            sb.append("\r\n");
            for (String tar : fileList) {
                sb.append("INFILE ");
                sb.append("'" + tar + "'");
                sb.append("\r\n");
            }
            sb.append("append into table factdata");
            sb.append(table);
            sb.append("\r\n");
            sb.append("FIELDS TERMINATED BY ','");
            sb.append("\r\n");
            sb.append("(SIGNATURE, ");
            for (int cnt = 1; cnt <= 30; cnt++) {
                sb.append("FACTDATAFIELD".concat(Integer.toString(cnt)).concat(", "));
            }
            sb.append("PART)");
            sb.append("\r\n");
            os = new FileOutputStream(ctrlFile);
            os.write(sb.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (Exception e2) {
                }
            }
        }
        return str;
    }
}