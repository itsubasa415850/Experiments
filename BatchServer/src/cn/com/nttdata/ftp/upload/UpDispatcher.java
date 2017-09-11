package cn.com.nttdata.ftp.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.net.ftp.FTPClient;

import cn.com.nttdata.ftp.FTPConnection;
import cn.com.nttdata.ftp.IDispatch;
import cn.com.nttdata.ftp.ProgressTable;

//分发器要决定在哪个时点写入控制文件！！！！！！！！
public class UpDispatcher implements IDispatch {
    private final ExecutorService es = Executors.newFixedThreadPool(10);
    private ProgressTable progressTable;
    private int threadQty;
    private File control;
    private File source;
    private final long started = System.currentTimeMillis();
    
    //回调方法，由上层接口持有者调用。
    public void recordProgress(int threadQty, String x) {
        progressTable.recordProgress(threadQty, x);
        progressTable.updateCtrlFile();
    }

    public UpDispatcher(int threadQty, String ctrlFileName, File source) {
        //控制文件对象生成
        this.control = new File(ctrlFileName);
        //初始化一张新进度表
        progressTable = new ProgressTable(threadQty, ctrlFileName);
        //源文件对象
        this.source = source;
        //需要的上传线程数量
        this.threadQty = threadQty;
    }

    public void dispatch(FTPClient ftpClient, String remote, String[] ftpCredentials, long size) {
        AbstractUpload up = null;
        String val = null;
        String[] startSize = null;
        boolean failed = false;
//        OutputStream os = null;
        //在分配任务之前应先确定各个线程下载的起始点和总量。
        if(!control.exists()) {
            assign(ftpClient, size);
            //分配好的任务更新到控制文件。
            progressTable.updateCtrlFile();
        } else {
            //分配任务（即更新控制表）
            assign();
        }
        try {
//            os = ftpClient.appendFileStream(remote);
            for(int idx = 1; idx <= threadQty; idx++) {
                //format:OK/INPROGRESS:XX-XX
                val = progressTable.getVal(idx - 1);
                String[] vals = val.split(":");
                //每个线程都只做自己的下载工作，不会交叉。
                if("INPROGRESS".equalsIgnoreCase(vals[0])) {
                    startSize = vals[1].split("-");
                    up = new Upload(this, Integer.parseInt(startSize[0]), Integer.parseInt(startSize[1]), idx, remote);
//                    up.setTargetStream(os);
                    up.setSource(source);
                    up.setFtpClient(new FTPConnection(ftpCredentials[0], ftpCredentials[1], ftpCredentials[2]).getFtpClient());
//                    up.setFtpClient(ftpClient);
                    es.execute(up);
//                    new Thread(up).start();
                }
            }
        } catch (Exception e) {
            failed = true;
            e.printStackTrace();
        } finally {
            if(!failed) {
                //如果上传过程无任何异常，则在调度完上传线程之后循环监视状态表，直至所有上传过程全部完成。
                postProc();
            }
        }
    }
    
    private void postProc() {
        while(true) {
            if(progressTable.allOver()) {
                break;
            }
        }
        //关闭文件
//        if(os != null) {
//            try {
//                os.close();
//            } catch (Exception e) {
//            }
//        }
        //删除控制文件
        control.delete();
        System.out.println("上传完成，共用时：" + (System.currentTimeMillis() - started) + "毫秒。");
    }
    
    private void assign() {
        BufferedReader br = null;
        InputStream is = null;
        String line = null;
        String[] contents = null;
        int threadNo;
        try {
            //读取控制文件，确定启动的线程数及每个线程的处理范围。
            is = new FileInputStream(control);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                if(line.contains("Thread")) {
                    contents = line.split(":");
                    //线程号
                    threadNo = Integer.parseInt(contents[0].substring(6));
                    progressTable.recordProgress(threadNo - 1, contents[1].concat(":").concat(contents[2]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
                if(br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    private void assign(FTPClient ftpClient, long size) {
        long total = size;
        int idx = 0;
        String mission = null;
        final String fromTo = "-";
        long from = 0;
        long quantity;
        int div = 0;
        try {
            //为下载线程分配任务
            for(idx = 1; idx <= threadQty; idx++) {
                mission = "INPROGRESS:";
                //5/5,4/4,3/3,2/2,1/1
                div = threadQty - (idx - 1);
                if(div == 1) {
                    quantity = total - from;
                } else {
                    quantity = size / div;
                }
                mission = mission.concat(Long.toString(from)).concat(fromTo).concat(Long.toString(Math.abs(quantity)));
                recordProgress(idx - 1, mission);
                from = from + quantity + 1;
                size = size - quantity;
            }
        } catch (Exception e) {
        }
    }
}
