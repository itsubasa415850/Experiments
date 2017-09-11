package cn.com.nttdata.ftp.download;

import java.io.IOException;
import java.io.InputStream;
//import java.math.BigDecimal;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import cn.com.nttdata.ftp.IDispatch;

public class Download extends AbstractDownload {
    private FTPClient ftpClient;

    public Download(IDispatch iDispatch, int startPos, int totalSize, int no, String remote) {
        super(iDispatch, startPos, totalSize, no, remote);
        System.out.println("线程" + no + "从" + startPos + "开始一共下载" + totalSize + "字节的数据。");
    }
    
    private InputStream setRestartOffset() {
        InputStream is = null;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setRestartOffset(startPos);
            is = ftpClient.retrieveFileStream(remote);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }
    

    /**
     * 写文件时必须互斥写。
     * @param b
     * @param off
     * @param len
     */
    public void writeTo(byte[] b, int length) {
        try {
            synchronized (targetStream) {
                targetStream.seek(startPos);
                targetStream.write(b, 0, length);
            }
        } catch (Exception e) {
        }
    }
    
    protected void down() {
        System.out.println("线程" + no + "现在开始下载。");
        //先找到本线程对应的起始位置
        InputStream is = setRestartOffset();
        byte[] b = new byte[BUFFER_SIZE];
        //从文件中读入的字节数！！！
        int ret = 0;
//        double perc = 0d;
//        double prev = 0d;
        final int total = startPos + totalSize;
//        int start = 0;
        try {
            while((ret = is.read(b, 0, BUFFER_SIZE)) != -1) {
                if(startPos >= total) {
                    break;
                }
                Thread.sleep(2);
                writeTo(b, ret);
//                start += ret;
                //取新的起始点
                startPos += ret;
                //通知上层对象新的下载状态。
                iDispatch.recordProgress(no - 1, "INPROGRESS:".concat(Integer.toString(startPos)).concat("-").concat(Integer.toString(totalSize)));
                //统计本线程下载量的百分比
//                perc = new BigDecimal((double) start / total).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//                if(perc != 0d && perc > prev) {
//                    System.out.println("线程" + no + "现在已下载" + perc * 100 + "%的数据。");
//                    prev = perc;
//                }
            }
            System.out.println("线程" + no + "的下载已完成。");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //通知上层对象下载已完成。
            iDispatch.recordProgress(no - 1, "OK:".concat(Integer.toString(startPos)).concat("-").concat(Integer.toString(totalSize)));
            b = null;
            try {
                if(is != null) {
                    is.close();
                    ftpClient.completePendingCommand();
                }
                if(ftpClient != null) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
            }
        }
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
