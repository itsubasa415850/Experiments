package cn.com.nttdata.batchserver.concurrentrun;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.nttdata.batchserver.errors.ServiceError;
import cn.com.nttdata.batchserver.functions.Function;

public class ServerProcedure {
    private final String baseDir = "/work/test/server/";
    private final String ftpBase = "/work/test/open/";
//    private File processFile = null;
    private Logger logger;

    public ServerProcedure(Logger logger) {
        this.logger = logger;
    }

    protected void processFile(String mailList, String zip, File processFile, List<String> fileList) {
    	long start = 0l;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS");
        try {
            String target = zip;
            target = (target.split("-"))[0];
            logger.info("Now processing:" + URLDecoder.decode(target, "UTF-8"));
            start = System.currentTimeMillis();
            logger.info("文件解压已于" + sdf.format(new Date(start)) + "开始。");
            String taxEntry = Function.unRar(ftpBase + File.separator + zip, baseDir + File.separator + Function.randomIt() + File.separator);
            logger.info("文件解压完成，已用时" + ((System.currentTimeMillis() - start) / 1000) + "秒钟。");
            String fileName = taxEntry.substring(taxEntry.lastIndexOf(File.separator) + 1, taxEntry.length());
            taxEntry = taxEntry.substring(0, taxEntry.lastIndexOf(File.separator));
            File fD = new File(taxEntry);
            rename(fD);
            File realDir = new File(URLDecoder.decode(fD.getAbsolutePath(), "UTF-8"));
            fD.renameTo(realDir);
            String[] reportNos = zip.substring(0, zip.lastIndexOf(".")).split("-");
            String outPath = URLDecoder.decode(fD.getAbsolutePath(), "UTF-8");
            if(Function.canInsert(reportNos, logger)) {
                Function.generateNodeData(URLDecoder.decode(taxEntry + File.separator + fileName, "UTF-8"), outPath, logger, URLDecoder.decode(target, "UTF-8"));
                start = System.currentTimeMillis();
            	logger.info("读取实例文档已于" + sdf.format(new Date(start)) + "开始。");
//                Function.parseInstance(taxEntry + File.separator + findInstance(realDir), outPath, logger, URLDecoder.decode(target, "UTF-8"));
                logger.info("读取实例文档完成，已用时" + ((System.currentTimeMillis() - start) / 1000) + "秒钟。");
//                Function.saveData(outPath, reportNos, logger);
            }
//            logger.info("Now sending mail...");
//            sendMail(mailList, reportNos, logger);
            logger.info("Done:" + URLDecoder.decode(target, "UTF-8"));
        } catch (Exception error) {
            logger.error("処理中に失敗しました："+ error.getMessage());
            throw new ServiceError(error.getMessage(), error);
        } catch (Error error) {
            logger.error("処理中に失敗しました："+ error.getMessage());
            throw new ServiceError(error.getMessage(), error);
        } finally {
//            if(processFile != null) {
//                fileList.remove(processFile.getAbsolutePath());
//                processFile.delete();
//            }
            logger.info("end:" + System.currentTimeMillis());
        }
    }

    private void rename(File targetDir) throws UnsupportedEncodingException {
        String[] files = targetDir.list();
        for(int idx = 0; idx < files.length; idx ++) {
            String s = targetDir.getAbsoluteFile() + File.separator + files[idx];
            File f = new File(s);
            f.renameTo(new File(targetDir + File.separator + URLDecoder.decode(files[idx], "UTF-8")));
        }
    }

    private String findInstance(File realDir) {
//        String[] files = realDir.list();
//        for(int idx = 0; idx < files.length; idx ++) {
//            if(!files[idx].contains("_") && files[idx].endsWith(".xml") && files[idx].contains("-")) {
//                return realDir.getAbsolutePath() + File.separator + files[idx];
//            }
//        }
        return "中央结算公司-100000000032057-20151231.xml";
    }

    private void sendMail(String toAddress, String[] reportNos, Logger logger) throws UnsupportedEncodingException {
        String subject = "[INFORM]A new financial report is now available for check.[DO NOT REPLY]";
        String content = new StringBuffer()
            .append("To whom it may concern:")
            .append("\r\n")
            .append("The financial report data from ")
            .append(URLDecoder.decode(reportNos[0], "UTF-8") + " ")
            .append("company with the year ")
            .append(reportNos[2].substring(0, 4))
            .append(" is now ready for inquiring,")
            .append("\r\n")
            .append("please refer to the address as provided below ")
            .append("for more information:")
            .append("\r\n")
            .append("http://172.16.132.104:8080/financialreport/page/index.jsp")
            .append("\r\n")
            .append("and you may use these words as the key words:")
            .append("\r\n")
            .append("1.Company Name:" + URLDecoder.decode(reportNos[0], "UTF-8"))
            .append("\r\n")
            .append("2.Company Id:" + reportNos[1])
            .append("\r\n")
            .append("3.Report Date(YYYYMMDD):" + reportNos[2])
            .append("\r\n")
            .append("4.Taxonomy Type:" + reportNos[3])
            .append("\r\n")
            .append("=========================================")
            .append("\r\n")
            .append("Attention:You may not reply this mail as it is sent by the DB operator.")
            .append("\r\n")
            .append("=========================================")
            .append("\r\n")
            .append("                                                                               Sincerely")
            .append("\r\n")
            .append("Yours")
            .append("\r\n")
            .append("                                                            NTT XBRL DB Operator")
            .append("\r\n")
            .append("                                                                           Present Day")
            .append("\r\n")
            .append("\r\n")
            .toString();
        Function.sendMail(toAddress, subject, content, logger);
    }
}
