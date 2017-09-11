package cn.com.nttdata.ftp;

import java.net.InetAddress;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPConnection {
    private final String ENC = "UTF-8";
    private Object[] credentials;
    private FTPClient ftpClient;
//    FTPConnectionFacade(String hostIp, String user, String pw) {
//        provideCredentials(hostIp, user, pw);
//    }
    
    public FTPConnection(String hostIp, String user, String pw) {
        provideCredentials(hostIp, user, pw);
        initFtp();
    }
    
    
    public boolean isActive() {
        return ftpClient != null && ftpClient.isConnected();
    }

    public FTPFile[] listFiles() throws Exception {
        return ftpClient.listFiles();
    }
    
    /**
     * @return the ftpClient
     */
    public FTPClient getFtpClient() {
        return ftpClient;
    }

    private void provideCredentials(String hostIp, String user, String pw) {
        credentials = new Object[4];
        credentials[0] = hostIp;
        credentials[2] = user;
        credentials[3] = pw;
        String[] ips = hostIp.split("\\.");
        byte[] ipByte = new byte[4];
        int length = ipByte.length;
        for(int idx = 0; idx< length; idx++) {
            ipByte[idx] = (byte) Integer.parseInt(ips[idx]);
        }
        credentials[1] = ipByte;
    }
    
    public void initFtp() {
        int reply;
        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding(ENC);
            //被动模式
            ftpClient.enterLocalPassiveMode();
            //读取数据的超时时间
            ftpClient.setDataTimeout(500);
            //连接
            ftpClient.connect(InetAddress.getByAddress((String) credentials[0], (byte[]) credentials[1]));
            //登录
            ftpClient.login((String) credentials[2], (String) credentials[3]);
//            ftpClient.changeWorkingDirectory("/big");
            //获得回应码
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new Exception("FTP连接以代码" + reply + "异常终止，请联系您的系统管理员。");
            }
            //将数据接收模式设为二进制
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void flushFtp() {
        if(ftpClient != null) {
            try {
                ftpClient.logout();
            } catch (Exception e) {
            }
            try {
                ftpClient.disconnect();
            } catch (Exception e) {
            }
        }
    }
}
