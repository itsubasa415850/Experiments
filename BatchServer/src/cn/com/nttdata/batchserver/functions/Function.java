package cn.com.nttdata.batchserver.functions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import cn.com.nttdata.batchserver.collection.MasterBean;
import cn.com.nttdata.batchserver.db.Controller;
import cn.com.nttdata.batchserver.db.GetData;
import cn.com.nttdata.batchserver.errors.ServiceError;
import cn.com.nttdata.batchserver.errors.UnzipOperationError;
import cn.com.nttdata.batchserver.errors.XBRLComponentExecutionError;
import cn.com.nttdata.xbrl.common.XbrlProperties;
import cn.com.nttdata.xbrl.info.XbrlException;
import cn.com.nttdata.xbrl.mapping.ContextDataBean;
import cn.com.nttdata.xbrl.mapping.ContextDataCollection;
import cn.com.nttdata.xbrl.mapping.FactDataBean;
import cn.com.nttdata.xbrl.mapping.FactDataCollection;
import cn.com.nttdata.xbrl.mapping.FactMappingBean;
import cn.com.nttdata.xbrl.mapping.FootnoteDataBean;
import cn.com.nttdata.xbrl.mapping.LabelDataBean;
import cn.com.nttdata.xbrl.mapping.NodeDataBean;
import cn.com.nttdata.xbrl.mapping.UnitDataBean;
import cn.com.nttdata.xbrl.mapping.UnitDataCollection;

public final class Function {
    public static final MyILog xbrlLog = new MyILog("d:\\log.txt");

    public static long randomIt() {
        Random random = new Random();
        long value = random.nextLong();
        return value >= 0 ? value : -value;
    }
    
    @SuppressWarnings("unchecked")
	public static String unZip(String sourceFileName, String desDir) {
        ZipFile zf = null;
        String returnFile = "";
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            zf = new ZipFile(new File(sourceFileName));
            Enumeration<ZipEntry> en = zf.getEntries();
            int length = 0;
            byte[] b = new byte[2048];
            while (en.hasMoreElements()) {
                ZipEntry ze = en.nextElement();
                String unZipFile = desDir + ze.getName();
                if(unZipFile.endsWith(".xsd"))
                    returnFile = unZipFile;
                File f = new File(unZipFile);
                if (ze.isDirectory()) {
                    f.mkdirs();
                } else {
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    outputStream = new FileOutputStream(f);
                    inputStream = zf.getInputStream(ze);
                    while ((length = inputStream.read(b)) > 0)
                        outputStream.write(b, 0, length);
                    if(inputStream != null) {
                        try {
                            inputStream.close();
                        } catch(Exception e) {}
                    }
                    if(outputStream != null) {
                        try {
                            outputStream.close();
                        } catch(Exception e) {}
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new UnzipOperationError(e.toString() + e.getMessage(), e);
        } finally {
            if(zf != null) {
                try {
                    zf.close();
                } catch(Exception e) {}
            }
        }
        return returnFile;
    }
    
    public static String composeRuleSet(String entryPoint,
            String valDir, String instance) {
        StringBuffer sb = new StringBuffer();
        OutputStream os = null;
        String returnFile = null;
        File v = new File(valDir);
        if(!v.exists()) {
            v.mkdir();
        }
        try {
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<validationRule>\n");
            sb.append("<vRuleSetId>VS00001</vRuleSetId>\n");
            sb.append("<vRuleSetDesc>Rule Set For CAS user</vRuleSetDesc>\n");
            sb.append("<vTaxonomyPath></vTaxonomyPath>\n");
            sb.append("<vInstancePath></vInstancePath>\n");
//            sb.append("<vTaxonomyPath>" + entryPoint + "</vTaxonomyPath>\n");
//            sb.append("<vInstancePath>" + instance.split("@")[0] + "</vInstancePath>\n");
            sb.append("<vCompanyCode>123456789012345</vCompanyCode>\n");
            sb.append("<VRule><vRuleId>VA1029</vRuleId><vConfigId>VC00003_1</vConfigId><vExecute>ON</vExecute><vType>INS</vType></VRule>\n");
//            if (instance != null && !"".equals(instance)) {
//            } else {
//                sb.append("<VRule><vRuleId>VA1029</vRuleId><vConfigId>VC00003_1</vConfigId><vExecute>ON</vExecute><vType>TAX</vType></VRule>\n");
//            }
            sb.append("</validationRule>\n");
            returnFile = valDir + "/RuleSet.xml";
            os = new FileOutputStream(new File(returnFile));
            byte[] bb = sb.toString().getBytes("UTF-8");
            os.write(bb);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }
        return returnFile;
    }
    
    public static void saveData(String dataPath, Logger logger, Map m, Controller controller, String t) throws UnsupportedEncodingException {
 
        logger.info("Now saving data ...");
        List<NodeDataBean> nList = null;
        List<LabelDataBean> lList = null;
        List<FootnoteDataBean> fnList = null;
        List<ContextDataBean> cdList = null;
        List<UnitDataBean> uList = null;
        List<FactDataBean> fList = null;
        List<FactMappingBean> fmList = null;
        try {

            Set<String> sets = m.keySet();
            for (String set : sets) {
                if("FactData".equalsIgnoreCase(set)) {
                    fList = ((FactDataCollection) m.get(set)).getMappings();
                }
                /* else if("ContextData".equalsIgnoreCase(set)) {
                    cdList = ((ContextDataCollection) m.get(set)).getMappings();
                } else if("UnitData".equalsIgnoreCase(set)) {
                    uList = ((UnitDataCollection) m.get(set)).getMappings();
                }*/
            }
            controller.performMasterSaveProcess(Function.randomIt());
//            controller.performUnitSaveProcess(uList);
//            controller.performContextSaveProcess(cdList);
            controller.performFactSaveProcess(fList, t);
//            controller.performFactMappingSaveProcess(fmList);
//            controller.performFootnoteSaveProcess(fnList);
//            controller.performNodeDataSaveProcess(nList);
//                controller.performAxisDataSaveProcess(xList);
//            controller.performLabelSaveProcess(lList);
//                controller.performRtSaveProcess(rList);
//            controller.commitSession();
//            controller.flushConnection();
            logger.info("Completed.");
        } catch(ServiceError error) {
//            controller.rollbackSession();
//            controller.flushConnection();
            logger.error(error.getMessage());
            throw error;
        } finally {
            if(nList != null)
                nList.clear();
            if(lList != null)
                lList.clear();
            if(fnList != null)
                fnList.clear();
            if(cdList != null)
                cdList.clear();
            if(uList != null)
                uList.clear();
            if(fList != null)
                fList.clear();
            if(fmList != null)
                fmList.clear();
            logger.info("終了。");
        }
    }

    public static boolean canInsert(String[] reportNos, Logger logger) throws UnsupportedEncodingException {
        Controller controller = null;
        try {
            controller = new Controller(logger);
            controller.makeConnection("172.25.59.115:1521", "open", "open", "open");
            logger.info("Now searching for" + URLDecoder.decode(reportNos[0], "UTF-8") + "...");
            MasterBean mb = new MasterBean();
            mb.setCompanyName(URLDecoder.decode(reportNos[0], "UTF-8"));
            mb.setCompanyId("test");
            mb.setFinancialReportDate(reportNos[1]);
            mb.setTaxonomyType(reportNos[2]);
            controller.initializeBean(mb);
            return controller.canInsert();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void generateNodeData(String entryPoint, String outPath, Logger logger, String target) throws XbrlException {
        try {
            logger.info("Now generating nodedata for" + target + "...");
            XbrlProperties.loadProperties();
            cn.com.nttdata.xbrl.generateInstance.IXbrlController controller = new cn.com.nttdata.xbrl.generateControl.XbrlController();
            String pid = controller.start(xbrlLog, "C:\\xbrl_test\\TESTDB\\input\\common\\license\\hardware.key");
            controller.readTaxonomyFile(pid, entryPoint);
            controller.getNodeData(pid, outPath + File.separator + "NodeData.xml", outPath + File.separator + "AxisData.xml",
                    outPath + File.separator + "RoleTypeData.xml", outPath + File.separator + "LabelData.xml");
            controller.end(pid);
            logger.info("Completed:" + target);
        } catch (XbrlException e) {
            System.err.println(e.getMessage());
            throw new XBRLComponentExecutionError(e.toString() + e.getMessage(), e);
        }
    }

    public static List<String> parseInstance(InputStream is, String outPath, Logger logger, String target) throws XbrlException {
        List<String> factDataList = null;
        try {
            logger.info("Now parsing instance for" + target + "...");
            XbrlProperties.loadProperties();
            cn.com.nttdata.xbrl.parseInstance.IXbrlController controller = new cn.com.nttdata.xbrl.parseControl.XbrlController();
            String pid = controller.start(xbrlLog, "C:\\xbrl_test\\TESTDB\\input\\common\\license\\hardware.key");
            controller.loadInstance(pid, is);
            controller.getInstanceData(pid, outPath);
            factDataList = controller.getFactList();
            controller.cancel(pid);
            logger.info("Completed:" + target);
        } catch (XbrlException e) {
            System.err.println(e.getMessage());
            throw new XBRLComponentExecutionError(e.toString() + e.getMessage(), e);
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (Exception e2) {
            }
        }
        return factDataList;
    }

    public static void sendMail(String toAddress, String subject, String content, Logger logger) {
        MailService.setContent(content);
        MailService.setSubject(subject);
        MailService.setToAddresses(toAddress);
        MailService.sendMessage(logger);
    }
    
    public static String unRar(String srcRarPath, String dstDirectoryPath) {
        String returnFile = "";
//        if (!srcRarPath.toLowerCase().endsWith(".rar")) {
//            System.out.println("非rar文件！");
//            return;
//        }
        File dstDiretory = new File(dstDirectoryPath);
        if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
            dstDiretory.mkdirs();
        }
        Archive archive = null;
        try {
            archive = new Archive(new File(srcRarPath));
            if (archive != null) {
                archive.getMainHeader().print(); // 打印文件信息.
                FileHeader fh = archive.nextFileHeader();
                while (fh != null) {
                    if (fh.isDirectory()) { // 文件夹 
                        File fol = new File(dstDirectoryPath + File.separator
                                + fh.getFileNameString());
                        fol.mkdirs();
                    } else { // 文件
                    	String str = new String(fh.getFileNameString().trim());
                    	if(fh.getFileNameString().contains(".xsd")) {
                    		returnFile = dstDirectoryPath + File.separator + str;
                    	}
                        File out = new File(dstDirectoryPath + File.separator
                                + str);
                        System.out.println(out.getAbsolutePath());
                        try {// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压. 
                            if (!out.exists()) {
                                if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录. 
                                    out.getParentFile().mkdirs();
                                }
                                out.createNewFile();
                            }
                            FileOutputStream os = new FileOutputStream(out);
                            archive.extractFile(fh, os);
                            os.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    fh = archive.nextFileHeader();
                }
                archive.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnFile;
    }
}
