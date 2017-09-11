package cn.com.nttdata.rsa;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class RASA {

    public static void main(String[] args) {

//        BigDecimal p = new BigDecimal("999997");
//        BigDecimal q = new BigDecimal("99999997");
//        BigDecimal r = new BigDecimal(String.valueOf(p.longValue() * q.longValue()));
//        long l = r.longValue();
//        System.out.print(Math.abs(new Random(System.currentTimeMillis()).nextLong()));
//        System.out.print(Math.pow(13, 67) % 85);
        StringBuffer sb = new StringBuffer();
        RSA rsa = new RSA();
        byte[] b = args[0].getBytes();
        for (byte c : b) {
//            sb.append(rsa.code((int) c));
            System.out.println("原始数位：" + c + "(D)" + Integer.toHexString(c) + "(H)");
            BigInteger tr = rsa.code(c);
            String target = Long.toHexString(tr.longValue());
            int len = target.length();
            for(int idx = 1; idx <= 8 - len; idx++) {
                target = "0" + target;
            }
            System.out.println("转换后数位：" + tr.longValue() + "(D)" + target + "(H)");
            System.out.println("还原后数位：" + Integer.toHexString(rsa.decode(tr.intValue()).intValue()) + "(H)");
        }
        
//        Random r = new Random(System.currentTimeMillis());
//        long random = Math.abs(r.nextLong());
        
    }
}
