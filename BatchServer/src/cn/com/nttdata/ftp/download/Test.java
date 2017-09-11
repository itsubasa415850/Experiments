package cn.com.nttdata.ftp.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import cn.com.nttdata.ftp.FTPConnection;

public class Test {
    public static void main(String[] args) throws Exception {
//        File f = new File("d:\\alphabet.txt");
//        f.createNewFile();
//        OutputStream os = new FileOutputStream(f);
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0; i < 80; i++) {
//            for(int j = 0; j < 1000; j++) {
//                sb.append((char) (i + 48));
//            }
//            sb.append("\r\n");
//        }
//        os.write(sb.toString().getBytes());
//        os.close();
        FTPConnection ftp = new FTPConnection("172.16.209.50", "ftp01", "(Nttdata)");
        ftp.initFtp();
        System.out.println();
        
    }
}
