package cn.com.nttdata.rsa;

import java.math.BigInteger;
import java.util.Random;

public class RSA {
    private BigInteger P;
    private BigInteger Q;
    private BigInteger R;
    private BigInteger E;
    private BigInteger D;
    private BigInteger P1Q1;
    
    
    
    /**
     * @return the r
     */
    public BigInteger getR() {
        return R;
    }

    /**
     * @return the e
     */
    public BigInteger getE() {
        return E;
    }

    /**
     * @return the d
     */
    public BigInteger getD() {
        return D;
    }

    public RSA() {
        E = new BigInteger("0");
        D = new BigInteger("0");
        while(D.compareTo(E) == 0) {
            init();
        }
    }
    
    private void init() {
        //先按照当前时间的毫秒数生成一个随机数，并取绝对值
        Random r = new Random(System.currentTimeMillis());
        long random = Math.abs(r.nextInt());
        //按顺序生成PQRE及D。
        //PQ为两质数，并且Q>P
        //取该数的基数为随机数的前5位所代表的数值。
        P = new BigInteger(gen(Long.parseLong(String.valueOf(random).substring(0, 5))));
        Q = new BigInteger(gen(P.longValue() + 1));
        P1Q1 = P.subtract(new BigInteger("1")).multiply(Q.subtract(new BigInteger("1")));
        //R=PQ
        R = P.multiply(Q);
        //E是一个大于(P-1)*(Q-1)的质数
        E = new BigInteger(gen(P1Q1.longValue()));
        //D的值应满足(D*E)%(P-1)*(Q-1)=1
        D = forD();
    }
    
    private String gen(long limit) {
        while(!isPN(limit)) {
            limit ++;
        }
        return String.valueOf(limit);
    }

    private BigInteger forD() {
        long d = 3L;
        long e = E.longValue();
        long p1q1 = P1Q1.longValue();
        while(d * e % p1q1 != 1) {
            d++;
        }
        return new BigInteger(String.valueOf(d));
    }
    
    private boolean isPN(long target) {
        long half = (long) Math.sqrt(target);
        if(target == 1)
            return false;
        for(long idx = 2; idx < half; idx++) {
            if(target % idx == 0) {
                return false;
            }
        }
        return true;
    }
    
    public BigInteger code(int value) {
        return new BigInteger("" + value).modPow(E, R);
    }
    
    public BigInteger decode(int value) {
        return new BigInteger("" + value).modPow(D, R);
    }
}
