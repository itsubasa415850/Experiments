package cn.com.nttdata.ftp.upload;

import java.io.File;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;

import cn.com.nttdata.ftp.IDispatch;

abstract class AbstractUpload implements Runnable {
    //上层回调接口，用于向上层报告文件的下载情况。
    protected IDispatch iDispatch;
    //每一个线程只需维护每次写入文件之后剩下的起始位置、并记住自己的结束位置即可。
    protected int startPos;
    protected int totalSize;
    protected int no;
    protected String remote;
    //每缓冲这个数就写一次磁盘。
    protected static final int BUFFER_SIZE = 1024;
    protected volatile OutputStream targetStream = null;
    protected FTPClient ftpClient;
    protected File source;
    
    public void setSource(File source) {
        this.source = source;
    }

    protected abstract void up();
    /**
     * @param targetStream the targetStream to set
     */
    public void setTargetStream(OutputStream targetStream) {
        this.targetStream = targetStream;
    }

    /**
     * 由上传线程自己去控制<code>FTPClient</code>对象。
     * @param ftpClient 下载线程需要用到的FTPClient对象。
     */
    protected abstract void setFtpClient(FTPClient ftpClient);
    
    //上传文件，具体方法由线程实现。
    public void run() {
        up();
    }

    public AbstractUpload(IDispatch iDispatch, int startPos, int totalSize,
            int no, String remote) {
        this.iDispatch = iDispatch;
        this.startPos = startPos;
        this.totalSize = totalSize;
        this.no = no;
        this.remote = remote;
    }
}
