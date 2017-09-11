package cn.com.nttdata.ftp.upload;

import java.io.File;

import cn.com.nttdata.ftp.FTPConnection;

public class UpMain {
    //args1:FTP连接信息
    //args2:ftp上文件名
    //args3:本地文件全路径
    public static void main(String[] args) throws Exception {
        File srcFile = new File(args[2]);
        if(args.length != 3) {
            throw new IllegalArgumentException("传入参数有误。");
        } else if(!srcFile.exists()) {
            throw new IllegalStateException("要上传的文件不存在。");
        }
        String[] ftpCredentials = null;
        FTPConnection connection = null;
        UpDispatcher dispatcher = null;
//        OutputStream os = null;
        long size = 0l;
        int threadQty= 0;
        final String ctrlFile = args[2].concat(".control");
        try {
            ftpCredentials = args[0].split(",");
            connection = new FTPConnection(ftpCredentials[0], ftpCredentials[1], ftpCredentials[2]);
//            os = connection.getFtpClient().appendFileStream("bigone.zip");
            //连接正常时
            if(connection.isActive()) {
                //获得要上传文件的大小
                size = srcFile.length();
                //自动判断适合启动几个线程。
                threadQty = (int) Math.sqrt(size / 102400);
                if(threadQty == 0) {
                    threadQty = 1;
                }
                dispatcher = new UpDispatcher(threadQty, ctrlFile, srcFile);
                dispatcher.dispatch(connection.getFtpClient(), args[1], ftpCredentials, size);
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
