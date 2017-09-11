package cn.com.nttdata.ftp.download;

import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTPClient;

import cn.com.nttdata.ftp.IDispatch;

abstract class AbstractDownload implements Runnable {
    //上层回调接口，用于向上层报告文件的下载情况。
    protected IDispatch iDispatch;
    //每一个线程只需维护每次写入文件之后剩下的起始位置、并记住自己的结束位置即可。
    protected int startPos;
    protected int totalSize;
    protected int no;
    protected String remote;
    //每缓冲这个数就写一次磁盘。
    protected static final int BUFFER_SIZE = 1024;
    protected volatile RandomAccessFile targetStream = null;
    protected FTPClient ftpClient;
    protected void setTargetStream(RandomAccessFile targetStream) {
        this.targetStream = targetStream;
    }

    /**
     * 由下载线程自己去控制<code>FTPClient</code>对象。
     * @param ftpClient 下载线程需要用到的FTPClient对象。
     */
    protected abstract void setFtpClient(FTPClient ftpClient);
    
    /**
     * 每个线程在初始化时必需要知道自己的上层对象是谁，并且要知道自己需要从何处开始下载，下载总量是多少。
     * @param iDispatch 上层回调
     * @param startPos 下载的起始位置
     * @param totalSize 下载总量
     * @param no 线程号
     * @see IDispatch
     */
    protected AbstractDownload(IDispatch iDispatch, int startPos, int totalSize, int no, String remote) {
        this.iDispatch = iDispatch;
        this.startPos = startPos;
        this.totalSize = totalSize;
        this.no = no;
        this.remote = remote;
    }

    public void run() {
        down();
    }
    
    //下载文件，具体方法由线程实现。
    protected abstract void down();
}
