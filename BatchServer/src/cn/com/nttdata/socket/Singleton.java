package cn.com.nttdata.socket;

public class Singleton {
    private static Singleton instance;
    private final int i = 0;
    private Singleton() {}
    
    public static synchronized Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
    
    public static void main(String[] args) {
        Singleton single = Singleton.getInstance();
        System.out.println();
    }
}
