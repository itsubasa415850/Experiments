package cn.com.nttdata.ftp.download;

import java.io.FileNotFoundException;

import org.apache.commons.net.ftp.FTPFile;

import cn.com.nttdata.ftp.FTPConnection;

public class DownMain {

    //args1:FTP连接信息
    //args2:ftp上文件名
    //args3:本地文件全路径
    public static void main(String[] args) throws Exception {
        if(args.length != 3) {
            throw new IllegalArgumentException("传入参数有误。");
        }
        String[] ftpCredentials = null;
        FTPConnection connection = null;
        DownDispatcher dispatcher = null;
        int idx = 0;
        long size = 0l;
        int threadQty= 0;
        final String ctrlFile = args[2].concat(".control");
        try {
            ftpCredentials = args[0].split(",");
            connection = new FTPConnection(ftpCredentials[0], ftpCredentials[1], ftpCredentials[2]);
            //连接正常时
            if(connection.isActive()) {
                FTPFile[] files = connection.listFiles();
                int length = files.length;
                for(; idx < length; idx++) {
                    if(args[1].equalsIgnoreCase(files[idx].getName())) {
                        size = files[idx].getSize();
                        break;
                    }
                }
                if(size == 0) {
                    throw new FileNotFoundException("要下载的文件不存在。");
                }
                //自动判断适合启动几个线程。
                threadQty = (int) Math.sqrt(size / 102400);
                if(threadQty == 0) {
                    threadQty = 1;
                }
//                threadNo = 1;
                dispatcher = new DownDispatcher(threadQty, ctrlFile, args[2]);
                dispatcher.dispatch(connection.getFtpClient(), args[1], ftpCredentials);
            } else {
                throw new IllegalStateException("FTP状态异常。");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            connection.flushFtp();
        }
    }
}
