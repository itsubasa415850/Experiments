package cn.com.nttdata.socket;

public class ServerMain {

    public static void main(String[] args) {
        PoolServer server = null;
        Thread thread = null;
//      //args0:valout
//      //args1:1:val;2:db
//      //args2:rulesets
//      //args3:config
//      //args4:csvout
//      //args5:port
//      //args6:dbsvr
//      //args7:open
        //args8 work
        //args9: num
        try {
            //按要求启动服务器线程，
            //1为接收校验；2为接收解析。
            if("1".equals(args[1])) {
                server = new ValServer(Integer.parseInt(args[5]), args[0], args[1], args[2], args[3], args[4], args[6], args[7], args[8], args[9]);
                thread = new Thread(server);
                thread.start();
            } else {
                server = new ParServer(Integer.parseInt(args[5]), args[0], args[1], args[2], args[3], args[4], args[6], args[7], args[8], args[9]);
                thread = new Thread(server);
                thread.start();
            }
        } catch (Exception e) {
        }
        
    }

}
