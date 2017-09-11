package cn.com.nttdata.ftp.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import cn.com.nttdata.ftp.FTPConnection;
import cn.com.nttdata.ftp.IDispatch;
import cn.com.nttdata.ftp.ProgressTable;

//分发器要决定在哪个时点写入控制文件！！！！！！！！
public class DownDispatcher implements IDispatch {
    private final ExecutorService es = Executors.newFixedThreadPool(10);
    private ProgressTable progressTable;
    private int threadQty;
    private File target;
    private String ctrlFileName;
    private File control;
    private final long started = System.currentTimeMillis();
    public DownDispatcher(int threadQty, String ctrlFileName, String targetFile) {
        this.ctrlFileName = ctrlFileName;
        try {
            target = new File(targetFile.concat(".download"));
            control = new File(ctrlFileName);
            //当控制文件与下载文件都不存在时
            //新下载的情况
            if(!target.exists() && !new File(ctrlFileName).exists()) {
                //在开始分配任务之前先建立一个空的文件。
                target.createNewFile();
            }
            this.threadQty = threadQty;
            progressTable = new ProgressTable(threadQty, ctrlFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    //回调方法，由上层接口持有者调用。
    public void recordProgress(int threadNo, String x) {
        progressTable.recordProgress(threadNo, x);
        progressTable.updateCtrlFile();
    }
    
    public void dispatch(FTPClient ftpClient, String remote, String[] ftpCredentials) {
        AbstractDownload down = null;
        String val = null;
        String[] startSize = null;
        RandomAccessFile raf = null;
        boolean failed = false;
        //在分配任务之前应先确定各个线程下载的起始点和总量。
        //当控制文件不存在时
        if(!control.exists()) {
            assign(ftpClient, remote);
            //分配好的任务更新到控制文件。
            progressTable.updateCtrlFile();
        } else {
            //分配任务（即更新控制表）
            assign();
        }
        try {
            raf = new RandomAccessFile(target, "rw");
            raf.setLength(target.length());
            for(int idx = 1; idx <= threadQty; idx++) {
                //format:OK/INPROGRESS:XX-XX
                val = progressTable.getVal(idx - 1);
                String[] vals = val.split(":");
                //每个线程都只做自己的下载工作，不会交叉。
                if("INPROGRESS".equalsIgnoreCase(vals[0])) {
                    startSize = vals[1].split("-");
                    down = new Download(this, Integer.parseInt(startSize[0]), Integer.parseInt(startSize[1]), idx, remote);
                    down.setTargetStream(raf);
                    down.setFtpClient(new FTPConnection(ftpCredentials[0], ftpCredentials[1], ftpCredentials[2]).getFtpClient());
                    es.execute(down);
                }
            }
        } catch (Exception e) {
            failed = true;
            if(raf != null) {
                try {
                    raf.close();
                } catch (Exception e1) {
                }
            }
        } finally {
            if(!failed) {
                //如果下载过程中无任何异常，则在调度完下载线程之后循环监视状态表，直至所有下载过程全部完成。
                postProc(raf);
            }
        }
    }
    
    private void postProc(RandomAccessFile raf) {
        while(true) {
            if(progressTable.allOver()) {
                break;
            }
        }
        //关闭文件
        if(raf != null) {
            try {
                raf.close();
            } catch (Exception e) {
            }
        }
        //重命名已下载文件
        String originalFile = target.getAbsolutePath();
        target.renameTo(new File(originalFile.substring(0, originalFile.lastIndexOf('.'))));
        //删除控制文件
        new File(ctrlFileName).delete();
        System.out.println("下载完成，共用时：" + (System.currentTimeMillis() - started) + "毫秒。");
    }
    
    private void assign(FTPClient ftpClient, String remote) {
        long total = 0l;
        long size = 0;
        int idx = 0;
        String mission = null;
        final String fromTo = "-";
        long from = 0;
        long quantity;
        try {
            FTPFile[] files = ftpClient.listFiles();
            int length = files.length;
            //找到要下文件的大小，以字节记。
            for(; idx < length; idx++) {
                if(remote.equalsIgnoreCase(files[idx].getName())) {
                    size = files[idx].getSize();
                    total = size;
                    break;
                }
            }
            //为下载线程分配任务
            for(idx = 1; idx <= threadQty; idx++) {
                mission = "INPROGRESS:";
                //5/5,4/4,3/3,2/2,1/1
                int div = threadQty - (idx - 1);
                if(div == 1) {
                    quantity = total - from;
                } else {
                    quantity = size / div;
                }
                mission = mission.concat(Long.toString(from)).concat(fromTo).concat(Long.toString(quantity));
                recordProgress(idx - 1, mission);
                from = from + quantity + 1;
                size = size - quantity;
            }
        } catch (Exception e) {
        }
    }
}