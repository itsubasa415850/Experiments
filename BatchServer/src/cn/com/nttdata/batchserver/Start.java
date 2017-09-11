package cn.com.nttdata.batchserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cn.com.nttdata.batchserver.functions.Function;

public class Start {

    public static void main(String[] args) throws IOException {
        //args0:d:\ftp\1
        //args1:1
        //args2:d:\ctrl\
        //args3:d:\work\1
        File ftpDir = new File(args[0]);
        try {
            String tar = process(ftpDir, args[1]);
//            Runtime.getRuntime().exec("cmd /k start sqlldr userid=open/open control=" + tar);
        } catch (Exception e) {
        }
    }
    
    private static String process(File ftpDir, String ctrl) {
        OutputStream os = null;
        String str = ctrl + File.separator +  Function.randomIt() + ".ctl";
        File ctrlFile = new File(str);
        String[] tars = ftpDir.list();
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("LOAD DATA ");
            sb.append("\r\n");
            for (String tar : tars) {
                sb.append("INFILE ");
                sb.append("'" + ftpDir.getAbsoluteFile() + File.separator + tar + "'");
                sb.append("\r\n");
            }
            sb.append("append into table factdatabulk");
            sb.append("\r\n");
            sb.append("FIELDS TERMINATED BY ','");
            sb.append("\r\n");
            sb.append("(SIGNATURE,FACTDATAFIELD1 ,FACTDATAFIELD2 ,FACTDATAFIELD3 ,FACTDATAFIELD4 ,FACTDATAFIELD5 ,FACTDATAFIELD6 ,FACTDATAFIELD7 ,FACTDATAFIELD8 ,FACTDATAFIELD9 ,FACTDATAFIELD10,FACTDATAFIELD11,FACTDATAFIELD12,FACTDATAFIELD13,FACTDATAFIELD14,FACTDATAFIELD15,FACTDATAFIELD16,FACTDATAFIELD17,FACTDATAFIELD18,FACTDATAFIELD19,FACTDATAFIELD20,FACTDATAFIELD21,FACTDATAFIELD22,FACTDATAFIELD23,FACTDATAFIELD24,FACTDATAFIELD25,FACTDATAFIELD26,FACTDATAFIELD27,FACTDATAFIELD28,FACTDATAFIELD29,FACTDATAFIELD30)");
            sb.append("\r\n");
            os = new FileOutputStream(ctrlFile);
            os.write(sb.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (Exception e2) {
                }
            }
        }
        return str;
    }
}
