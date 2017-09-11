package cn.com.nttdata.arelleperf.threads;

public class SharePot {
    private int resources;
    private int limit;
    public SharePot(int resources, int limit) {
        this.resources = resources;
        this.limit = limit;
    }
    
    public int getResources() {
        return resources;
    }
    public synchronized void borrowOne() {
         while(resources < 1) {
             try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         }
         resources --;
         notify();
         System.out.println("现在使用一个资源，还剩" + resources + "个资源。");
     }
     
     public synchronized void returnOne() {
         while(resources >= limit) {
             try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         }
         resources ++;
         notify();
         System.out.println("现在归还一个资源，总数为" + resources + "个资源。");
     }
}
