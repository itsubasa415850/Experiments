package cn.com.nttdata.ftp.upload;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import cn.com.nttdata.ftp.IDispatch;

public class Upload extends AbstractUpload {
    private FTPClient ftpClient;
    private boolean error = false;
    public Upload(IDispatch iDispatch, int startPos, int totalSize, int no,
            String remote) {
        super(iDispatch, startPos, totalSize, no, remote);
        System.out.println("线程" + no + "从" + startPos + "开始一共上传" + totalSize + "字节的数据。");
    }

    /**
     * 写文件时必须互斥写。
     * @param b
     * @param off
     * @param len
     * @throws IOException 
     */
    public void writeTo(byte[] b, int length) throws IOException {
        synchronized (targetStream) {
            try {
                targetStream.write(b, 0, length);
            } catch (IOException e) {
                System.err.println("上传文件时发生错误：" + e.toString());
                throw e;
            }
        }
    }
    
    private synchronized OutputStream setRestartOffset() {
        OutputStream os = null;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setRestartOffset(startPos);
            os = ftpClient.appendFileStream(remote);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os;
    }
    
    protected void up() {
        System.out.println("线程" + no + "现在开始上传。");
        //源文件只读
        RandomAccessFile raf = null;
        byte[] b = new byte[BUFFER_SIZE];
        //从文件中读入的字节数！！！
        int ret = 0;
        final int total = startPos + totalSize;
//        OutputStream os = null;
        try {
            setTargetStream(setRestartOffset());
            //初始化找到上传位置
            raf = new RandomAccessFile(source, "rw");
            raf.setLength(source.length());
            raf.seek(startPos);
            while((ret = raf.read(b)) != -1) {
                if(startPos >= total) {
                    break;
                }
                writeTo(b, ret);
                Thread.sleep(2);
                //取新的起始点
                startPos += ret;
                //通知上层对象新的下载状态。
                iDispatch.recordProgress(no - 1, "INPROGRESS:".concat(Integer.toString(startPos)).concat("-").concat(Integer.toString(totalSize)));
            }
            System.out.println("线程" + no + "的上传已完成。");
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        } finally {
            b = null;
            //当上传正常完成时
            if(!error) {
                //通知上层对象下载已完成。
                iDispatch.recordProgress(no - 1, "OK:".concat(Integer.toString(startPos)).concat("-").concat(Integer.toString(totalSize)));
                try {
                    if(raf != null) {
                        raf.close();
                    }
                    if(ftpClient != null) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (Exception e) {}
            } else {
                //当上传发生错误（如网络断开等）时
                System.exit(-1);
            }
        }
    }

    protected void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

}
