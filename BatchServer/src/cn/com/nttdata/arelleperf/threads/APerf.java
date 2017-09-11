package cn.com.nttdata.arelleperf.threads;

abstract class APerf extends Thread implements IPerf {
    protected long startTime;
    protected String uri;
    protected String getUri() {
        return uri;
    }

    protected APerf(long startTime, String uri) {
        this.startTime = startTime;
        this.uri = uri;
    }

    protected void setUri(String uri) {
        this.uri = uri;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long totalElapsed(long endTime) {
        return endTime - this.startTime;
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }
}
