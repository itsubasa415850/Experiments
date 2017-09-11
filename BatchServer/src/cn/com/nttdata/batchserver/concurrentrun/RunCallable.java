package cn.com.nttdata.batchserver.concurrentrun;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class RunCallable implements Callable<String> {

    private String mailList = null;
    private String zip = null;
    private File processFile = null;
    private ServerProcedure proc = null;
    private List<String> fileList = null;
    public RunCallable(ServerProcedure proc,
                            String mailList,
                            String zip,
                            File processFile,
                            List<String> fileList) {
        this.proc = proc;
        this.mailList = mailList;
        this.zip = zip;
        this.processFile = processFile;
        this.fileList = fileList;
    }

    public String call() throws Exception {
        proc.processFile(mailList, zip, processFile, fileList);
        return "call";
    }

}
