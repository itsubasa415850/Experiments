package cn.com.nttdata.ftp;

import java.io.FileWriter;

public class ProgressTable {
    private volatile String[] table;
    private String fileName;
    
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    public String getVal(int threadNo) {
        return table[threadNo];
    }
    
    public boolean allOver() {
        boolean ret = true;
        int length = table.length;
        for (int i = 0; i < length; i++) {
            if(table[i].contains("INPROGRESS")) {
                ret = false;
                break;
            } else {
                continue;
            }
        }
        return ret;
    }
    
    /**
     * 初始化一个进度表。
     * @param totalCapacity 要访问本进度表的总线程数
     * @param fileName 该进度表对应的下载文件名
     */
    public ProgressTable(int totalCapacity, String fileName) {
        this.fileName = fileName;
        table = new String[totalCapacity];
        for(int idx = 0; idx < totalCapacity; idx++) {
            table[idx] = "INPROGRESS:00-00";
        }
    }
    
    /**
     * 需要实时维护该表的线程将互斥地使用该方法。
     * @param threadNo 线程编号
     * @param x 本线程下载文件对应的起始位置及总大小，格式为：OK/INPROGRESS:xx-xx
     */
    public void recordProgress(int threadNo, String x) {
        synchronized(table) {
            table[threadNo] = x;
        }
    }
    
    /**
     * 会频繁写控制文件。
     */
    public synchronized void updateCtrlFile() {
        FileWriter writer = null;
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("Control file name:");
            sb.append("\r\n");
            sb.append(fileName);
            sb.append("\r\n");
            sb.append("Accomplishment:");
            sb.append("\r\n");
            int idx = table.length;
            for (int i = 0; i < idx; i++) {
                sb.append("Thread");
                sb.append(i + 1);
                sb.append(":");
                sb.append(table[i]);
                sb.append("\r\n");
            }
            writer = new FileWriter(fileName, false);
            writer.write(sb.toString());
        } catch (Exception e) {
        } finally {
            if(writer != null) {
                try {
                    writer.flush();
                } catch (Exception e2) {
                }
                try {
                    writer.close();
                } catch (Exception e2) {
                }
            }
        }
    }
}
