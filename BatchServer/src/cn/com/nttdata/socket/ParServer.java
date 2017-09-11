package cn.com.nttdata.socket;

import java.io.File;

import cn.com.nttdata.batchserver.ServerStart;

public class ParServer extends PoolServer {
    public void run() {
        while(true) {
            if(over()) {
                con.modify(uuid, "NA");
                con.commitSession();
                loadOver(false);
            }
            String uuid = con.whichToInsert(num);
            if(uuid != null && !"".equals(uuid)) {
                //找到可以入库的记录了。
                insert(uuid);
            } else {
                //没有找到可以入库的记录，所以只接收文件。
                serve();
            }
        }
    }

    private void insert(String uuid) {
        logger.info("找到可以入库的记录了。");
        File csvDir = new File(csvOut + File.separator + uuid);
        String[] files = csvDir.list();
        File workDir = new File(work + File.separator + uuid);
        if(!workDir.exists()) {
            workDir.mkdirs();
        }
        ServerStart ss = new ServerStart(csvDir.getAbsolutePath(), 
                ruleSetOrTable,
                valOutOrCtlOut + File.separator + uuid,
                workDir.getAbsolutePath(),
                String.valueOf(files.length / 3), "10000", num);
        ss.setServer(this);
        ss.start();
        con.modify(uuid, "NA");
        con.commitSession();
        uuid = null;
    }

    public ParServer(int port, String valOut, String func, String ruleSet,
            String config, String csvOut, String dbsvr, String open, String work, String num) {
        super(port, valOut, func, ruleSet, config, csvOut, dbsvr, open, work, num);
    }

}
