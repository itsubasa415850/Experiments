package cn.com.nttdata.socket;

import cn.com.nttdata.batchserver.db.Controller;

public interface CallBack extends Runnable {
    public void tell(boolean isValid);
    public boolean others();
    public void loadOver(boolean over);
    public boolean over();
    Controller getCon();
    public String getUuid();
    public void setUuid(String uuid);
    public String getNum();
}
