package cn.com.nttdata.socket;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private String server;
    private int port;
    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }
    //测试代码先保留。。。
    public static void main(String args[]) throws Exception {
        Client c = new Client("172.16.209.22", 33210);
        c.send(new FileInputStream(new File("d:\\2949996245460070.csv")), "2949996245460070.csv");
    }
    public void send(InputStream is, String fileName) {
        int length = 0;
        byte[] sendByte = null;
        Socket socket = null;
        DataOutputStream dout = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(server, port), 50 * 1000);
            dout = new DataOutputStream(socket.getOutputStream());
            sendByte = new byte[1024];
            dout.writeUTF(fileName);
            while((length = is.read(sendByte, 0, sendByte.length)) > 0) {
                dout.write(sendByte,0,length);
                dout.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dout != null) {
                try {
                    dout.close();
                } catch (Exception e2) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e2) {
                }
            }
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
          }
    }
}
