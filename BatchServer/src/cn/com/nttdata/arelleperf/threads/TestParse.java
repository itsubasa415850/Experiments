package cn.com.nttdata.arelleperf.threads;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import cn.com.nttdata.batchserver.functions.Function;

public class TestParse extends APerf {
    private Logger logger;
    private String uuid;
    private String out;
    private InputStream instance;
    private String table;
    private String num;
    protected TestParse(long startTime, String uri) {
        super(startTime, uri);
    }

    public void setTable(String table) {
        this.table = table;
    }

    public TestParse(long startTime, String uri, Logger logger, String out, InputStream instance, String ruleSet, String tax, String uuid, String num) {
        super(startTime, uri);
        this.logger = logger;
        this.out = out;
        this.instance = instance;
        this.uuid = uuid;
        this.num = num;
    }

    public void run() {
        OutputStream os = null;
        try {
            String insOut = out.concat(File.separator).concat(uuid).concat("_").concat(num).concat(File.separator)
                    .concat(Long.toString(Function.randomIt())).concat(".csv");
            File uf = new File(out.concat(File.separator).concat(uuid).concat("_").concat(num));
            if(!uf.exists()) {
                uf.mkdirs();
            }
            os = new FileOutputStream(insOut);
            List<String> factDataList = Function.parseInstance(instance, insOut, logger, "A");

            StringBuilder sb = new StringBuilder();
            int idx = 0;
            for (String bean : factDataList) {
                sb.append(UUID.randomUUID());
                sb.append(",");
                sb.append(bean);
                sb.append(++idx);
                sb.append("\r\n");
            }
            os.write(sb.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    private static void upload(StringBuffer sb, FTPClient ftpClient) {
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
            ftpClient.storeFile(Function.randomIt() + ".csv", is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e2) {
                }
            }
        }
    }
    
    private void ftpConn(FTPClient ftpClient) {
        try {
            ftpClient.setControlEncoding("UTF-8");
            String host = "VM59-115";
            String[] ips = "172.25.59.115".split("\\.");
            byte[] ipByte = new byte[4];
            for(int idx = 0; idx< ipByte.length; idx++) {
                ipByte[idx] = (byte) Integer.parseInt(ips[idx]);
            }
            
            ftpClient.connect(InetAddress.getByAddress(host, ipByte));
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.login("ftp01", "(Nttdata)");
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new Exception("FTP连接以代码" + reply + "异常终止，请联系您的系统管理员。");
            }
            ftpClient.changeWorkingDirectory(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
