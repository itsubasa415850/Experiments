package cn.com.nttdata.ftp.upload;

import java.io.File;

import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.WriteMode;

public class FtpTool {
    private FileTransferClient ftp;
    private static final long started = System.currentTimeMillis();
    private String ip;
    private String user;
    private String pass;
    public FtpTool(String ip, String user, String pass) {
        this.ip = ip;
        this.user = user;
        this.pass = pass;
    }
    
    public void connect() throws Exception {
        ftp = new FileTransferClient();
        ftp.setRemoteHost(ip);
        ftp.setRemotePort(21);
        ftp.setUserName(user);
        ftp.setPassword(pass);
        
        ftp.setContentType(FTPTransferType.BINARY);
        ftp.connect();
    }

    public void resumeUpload(String localFile, String remoteFilePath) throws Exception {
        File local = new File(localFile);
        String remote = remoteFilePath + local.getName();
        ftp.uploadFile(localFile, remote, WriteMode.RESUME);
    }
    
    public void close() throws Exception {
        ftp.disconnect();
    }
    //args1:FTP连接信息
    //args2:ftp上文件名
    //args3:本地文件全路径
    public static void main(String[] args) {
        String[] ftpCredentials = args[0].split(",");
        
        FtpTool t = new FtpTool(ftpCredentials[0], ftpCredentials[1], ftpCredentials[2]);
        
        try {
            t.connect();
            t.resumeUpload(args[2], "/");
            System.out.println(System.currentTimeMillis() - started);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                t.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
