package cn.com.nttdata.arelleperf.threads;

interface IPerf {
    long getStartTime();
    void setStartTime(long startTime);
    
    long totalElapsed(long endTime);
}
