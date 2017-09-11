package cn.com.nttdata.batchserver.functions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import cn.com.nttdata.socket.Client;

public class SplitInstance {
    private Logger logger = null;
    private static List<String> fixNodeLst = new ArrayList<String>();
    private String uuid = UUID.randomUUID().toString();
    static {
        fixNodeLst.add("<?xml");
        fixNodeLst.add("<xbrli:xbrl");
        fixNodeLst.add("</xbrli:xbrl>");
        fixNodeLst.add("<link:schemaRef");
        fixNodeLst.add("<link:linkbaseRef");
        fixNodeLst.add("<link:arcroleRef");
        fixNodeLst.add("<link:roleRef");
        fixNodeLst.add("<xbrli:context");
        fixNodeLst.add("</xbrli:context>");
        fixNodeLst.add("<xbrli:entity>");
        fixNodeLst.add("</xbrli:entity>");
        fixNodeLst.add("<xbrli:identifier");
        fixNodeLst.add("</xbrli:identifier>");
        fixNodeLst.add("<xbrli:segment>");
        fixNodeLst.add("</xbrli:segment>");
        fixNodeLst.add("<xbrli:period>");
        fixNodeLst.add("</xbrli:period>");
        fixNodeLst.add("<xbrli:startDate>");
        fixNodeLst.add("</xbrli:startDate>");
        fixNodeLst.add("<xbrli:endDate>");
        fixNodeLst.add("</xbrli:endDate>");
        fixNodeLst.add("<xbrli:instant>");
        fixNodeLst.add("</xbrli:instant>");
        fixNodeLst.add("<xbrli:forever>");
        fixNodeLst.add("</xbrli:forever>");
        fixNodeLst.add("<xbrli:scenario>");
        fixNodeLst.add("</xbrli:scenario>");
        fixNodeLst.add("<xbrldi:explicitMember");
        fixNodeLst.add("<xbrli:unit");
        fixNodeLst.add("</xbrli:unit>");
        fixNodeLst.add("<xbrli:unit");
        fixNodeLst.add("</xbrli:unit>");
        fixNodeLst.add("<xbrli:measure>");
        fixNodeLst.add("</xbrli:measure>");
        fixNodeLst.add("<xbrli:divide>");
        fixNodeLst.add("</xbrli:divide>");
        fixNodeLst.add("<xbrli:unitNumerator>");
        fixNodeLst.add("</xbrli:unitNumerator>");
        fixNodeLst.add("<xbrli:unitDenominator>");
        fixNodeLst.add("</xbrli:unitDenominator>");
        fixNodeLst.add("<xbrl");
        fixNodeLst.add("</xbrl>");
        fixNodeLst.add("<context");
        fixNodeLst.add("</context>");
        fixNodeLst.add("<entity>");
        fixNodeLst.add("</entity>");
        fixNodeLst.add("<identifier");
        fixNodeLst.add("</identifier>");
        fixNodeLst.add("<segment>");
        fixNodeLst.add("</segment>");
        fixNodeLst.add("<period>");
        fixNodeLst.add("</period>");
        fixNodeLst.add("<startDate>");
        fixNodeLst.add("</startDate>");
        fixNodeLst.add("<endDate>");
        fixNodeLst.add("</endDate>");
        fixNodeLst.add("<instant>");
        fixNodeLst.add("</instant>");
        fixNodeLst.add("<forever>");
        fixNodeLst.add("</forever>");
        fixNodeLst.add("<scenario>");
        fixNodeLst.add("</scenario>");
        fixNodeLst.add("<unit");
        fixNodeLst.add("</unit>");
        fixNodeLst.add("<unit");
        fixNodeLst.add("</unit>");
        fixNodeLst.add("<measure>");
        fixNodeLst.add("</measure>");
        fixNodeLst.add("<divide>");
        fixNodeLst.add("</divide>");
        fixNodeLst.add("<unitNumerator>");
        fixNodeLst.add("</unitNumerator>");
        fixNodeLst.add("<unitDenominator>");
        fixNodeLst.add("</unitDenominator>");
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void split(String instanceFilePath, int fileSize, String xsdFilePath, 
            String dbsvr, String dbport, 
            String svr1, String port1, 
            String svr2, String port2, 
            String svr3, String port3, 
            String svr4, String port4) {
        StringBuffer declarePart = new StringBuffer();
        StringBuffer factPart = new StringBuffer();
        String line;
        String firstTupleFact = "";
        boolean factStart = false;
        InputStream is = null;
        StringBuilder result = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(instanceFilePath));
            //模拟校验服务器1的连接
            Client c1 = new Client(svr1, Integer.parseInt(port1));
            //模拟校验服务器2的连接
            Client c2 = new Client(svr2, Integer.parseInt(port2));
            //模拟校验服务器3的连接
//            Client c3 = new Client(svr3, Integer.parseInt(port3));
            //模拟校验服务器4的连接
//            Client c4 = new Client(svr4, Integer.parseInt(port4));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS");
//            int dispatch = 0;
            while ((line = br.readLine()) != null) {
                String temp = line.trim();
                if(!"".equals(temp)) {
                    boolean isDeclare = false;
                    if(!factStart) {
                        for(int j = 0; j < fixNodeLst.size(); j++) {
                            if(temp.startsWith(fixNodeLst.get(j))){
                                isDeclare = true;
                                break;
                            }
                        }
                    }
                    if(isDeclare) {
                        if(temp.startsWith("<link:schemaRef"))
                            temp = "<link:schemaRef xlink:type=\"simple\" xlink:href=\"".concat(xsdFilePath).concat("\" />");
                        declarePart.append(temp).append("\n");
                    } else {
                        factStart =true;
                        factPart.append(temp).append("\n");
                        if("".equals(firstTupleFact) && temp.startsWith("<") && !temp.startsWith("</")) {
                            firstTupleFact = "</".concat(temp.substring(1,temp.length()-1)).concat(">");
                        } else {
                            if(!"".equals(firstTupleFact) && temp.equals(firstTupleFact)) {
                                firstTupleFact = "";
                                if(declarePart.length() + factPart.length() > fileSize) {
                                    logger.info("已经于".concat(sdf.format(new Date(System.currentTimeMillis()))).concat("发送一个切片流。"));
//                                    dispatch++;
                                    result = new StringBuilder();
                                    result.append(declarePart);
                                    result.append(factPart);
                                    result.append("</xbrli:xbrl>\n");
                                    is = new ByteArrayInputStream(result.toString().getBytes("UTF-8"));
//                                    OutputStream os = new FileOutputStream(new File("d:\\512k.xml"));
//                                    os.write(result.toString().getBytes("UTF-8"));
//                                    os.close();
                                    c1.send(is, uuid.concat("VAL"));
                                    is.reset();
                                    c2.send(is, uuid.concat("VAL"));
                                    if(is != null) {
                                        try {
                                            is.close();
                                        } catch (Exception e) {
                                        }
                                    }
                                    //每个包发给不同的校验或解析服务器。
//                                    switch (dispatch) {
//                                    case 1:
//                                        c1.send(is, uuid.concat("VAL"));
//                                        break;
//                                    case 2:
//                                        c2.send(is, uuid.concat("VAL"));
//                                        break;
//                                    case 3:
//                                        c3.send(is, uuid.concat("VAL"));
//                                        break;
//                                    case 4:
//                                        c4.send(is, uuid.concat("VAL"));
//                                        dispatch = 0;
//                                        break;
//                                    default:
//                                        break;
//                                    }
                                    factPart.setLength(0);
                                    Thread.sleep(550);
                                }
                            }
                        }
                    }
                }
            }
            if(factPart.length() > 0) {
                result = new StringBuilder();
                result.append(declarePart);
                result.append(factPart);
                is = new ByteArrayInputStream(result.toString().getBytes("UTF-8"));
                c1.send(is, uuid.concat("EOF"));
                is.reset();
                c2.send(is, uuid.concat("EOF"));
                if(is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                    }
                }
                factPart.setLength(0);
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}