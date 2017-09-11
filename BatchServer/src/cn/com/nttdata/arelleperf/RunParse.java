package cn.com.nttdata.arelleperf;

import org.apache.log4j.Logger;

import cn.com.nttdata.batchserver.functions.SplitInstance;

public class RunParse {
    private static Logger logger = Logger.getLogger(RunParse.class);
    public static void main(String[] args) {
        try {
            if(args.length != 12) {
//                logger.info("正确的参数应该是：");
//                logger.info("D:\\a\\unzipped\\A-100000000032057-20151231.xsd");
//                logger.info("2048000");
//                logger.info("dbserver");
//                logger.info("dbport1");
//                logger.info("server1");
//                logger.info("port1");
//              logger.info("server2");
//              logger.info("port2");
//              logger.info("server3");
//              logger.info("port3");
//              logger.info("server4");
//              logger.info("port4");
                throw new IllegalArgumentException("参数不对。");
            }
            SplitInstance s = new SplitInstance();
            s.setLogger(logger);
            s.split(args[0].replace(".xsd", ".xml"),
                    Integer.parseInt(args[1]), args[0], args[2], args[3], args[4], args[5],
                    args[6], args[7],args[8], args[9],args[10], args[11]);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
