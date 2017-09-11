package test.kafkacluster.dispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import test.kafkacluster.concurrent.ReceiveDispatcher;
import test.kafkacluster.concurrent.SendDispatcher;
import test.kafkacluster.functions.Functions;

public class Dispatcher {
    private static final String[] types = {"splitandsend", "receive"};
    private static List<String> fixNodeLst = new ArrayList<String>();
    private String type;
    private int quantity;
    private String file;
    private Producer<String, String> producer;
    
    public Dispatcher(String type, String strQty, String file) {
        this.type = type;
        this.quantity = Integer.parseInt(strQty);
        this.file = file;
    }

    public void dispatch() throws Exception {
        if(types[0].equals(type)) {
            final long start = System.currentTimeMillis();
            initKafka();
            split();
            System.out.println("total send elapsed:======" + (System.currentTimeMillis() - start));
        } else if(types[1].equals(type)) {
//            ReceiveDispatcher rd = new ReceiveDispatcher(quantity, file);
//            rd.start();
        } else {
            throw new Exception("bbb");
        }
    }
    
    private void initKafka() {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(Functions.getRoot() + "/properties/config.properties"));
            Properties props = new Properties();
            props.load(is);
            props.put("metadata.broker.list", props.get("kafka.broker.list"));
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            props.put("key.serializer.class", "kafka.serializer.StringEncoder");
            props.put("request.required.acks", "-1");
            if(producer != null) {
                return;
            } else {
                producer = new Producer<String, String>(new ProducerConfig(props));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void split() {
        String topic = "topic";
//        int t = 1;
        final int slideSize = 512000;
        String line;
        BufferedReader br = null;
        boolean factStart = false;
        boolean isDeclare = true;
        StringBuilder declarePart = new StringBuilder();
        StringBuilder factPart = new StringBuilder();
        String firstTupleFact = "";
        StringBuilder result = null;
        String csvFactStartLine ="";
        String csvFactEndLine ="";
        String elementLine ="";
        boolean isCsvFact = false;
        int slideQty = 0;
        int count = 0;
        int startRecNo = 0;
        SendDispatcher sd = new SendDispatcher(quantity, producer);
        try {
            br = new BufferedReader(new FileReader(new File(file)));
            while((line = br.readLine()) != null) {
                String temp = line.trim();
                if(!"".equals(temp)) {
                    isDeclare = false;
                    if(!factStart) {
                        int size = fixNodeLst.size();
                        for(int j = 0; j < size; j++) {
                            if(temp.startsWith(fixNodeLst.get(j))) {
                                isDeclare = true;
                                break;
                            }
                        }
                    }
                }
                if(isDeclare) {
                    declarePart.append(temp).append("\n");
                } else {
                    factStart =true;
                    if(temp.startsWith("<wemax:InvestorsInformationRegistrationCSVDataExplanatory") || 
                            temp.startsWith("<wemax:BusinessInformationRegistrationCSVDataExplanatory")) {
                        isCsvFact =true;
                        count =0;
                        slideQty=0;
                        startRecNo = 0;
                        csvFactStartLine = temp;
                        if(temp.startsWith("<wemax:InvestorsInformationRegistrationCSVDataExplanatory")) {
                            csvFactEndLine = "</wemax:InvestorsInformationRegistrationCSVDataExplanatory>";
                        } else {
                            csvFactEndLine ="</wemax:BusinessInformationRegistrationCSVDataExplanatory>";
                        }
                        factPart.setLength(0);
                    }
                    //CSV数据拆分
                    if(isCsvFact) {
                        if(!"<![CDATA[".equals(temp) && temp.contains("<![CDATA[")) {
                            factPart.append("<![CDATA[").append("\n");
                            elementLine = temp.substring(9);
                            factPart.append(elementLine).append("\n");
                        } else if(!"]]>".equals(temp) && temp.endsWith("]]>")) {
                            factPart.append(temp.substring(0,temp.length()-3)).append("\n");
                            factPart.append("]]>").append("\n");
                        } else {
                            factPart.append(temp).append("\n");
                        }
                        if("<![CDATA[".equals(temp)) {
                            count --;
                            line = br.readLine();
                            temp = line.trim();
                            factPart.append(temp).append("\n");
                            elementLine = temp;
                            
                        } else  if(!temp.startsWith("<![CDATA[") && 
                                !"]]>".equals(temp) && 
                                !temp.equals(csvFactStartLine) && 
                                !temp.equals(csvFactEndLine)) {
                            count ++;
                        }
                        if(declarePart.length() + factPart.length() > slideSize || temp.contains("]]>") || csvFactEndLine.equals(temp)) {
                            slideQty ++;
                            if(temp.contains("]]>")) {
                                factPart.append(csvFactEndLine).append("\n");
                            }
                            result = new StringBuilder(768000);
                            result.append(declarePart);
                            result.append(factPart);
                            result.append("</xbrli:xbrl>\n");
                            sd.setMessage(result.toString());
//                            if(t >= quantity) {
//                                t = 1;
//                            }
                            sd.dispatch(topic);
//                            t++;
                            factPart.setLength(0);
                            startRecNo=count;
                            result = null;
                            if(temp.contains("]]>")) {
                                break;
                            } else {
                                factPart.append(csvFactStartLine).append("\n");
                                factPart.append("<![CDATA[").append("\n");
                                factPart.append(elementLine).append("\n");
                            }
                        }
                    //正常XBRL数据拆分
                    } else {
                        factPart.append(temp).append("\n");
                        if("".equals(firstTupleFact) && temp.startsWith("<") && !temp.startsWith("</")) {
                            firstTupleFact = "</".concat(temp.substring(1,temp.length()-1)).concat(">");
                        } else {
                            if(!"".equals(firstTupleFact) && temp.equals(firstTupleFact)) {
                                count ++;
                                firstTupleFact = "";
                                if(declarePart.length() + factPart.length() > slideSize) {
                                    slideQty ++;
                                    result = new StringBuilder(768000);
                                    result.append(declarePart);
                                    result.append(factPart);
                                    result.append("</xbrli:xbrl>\n");
                                    sd.setMessage(result.toString());
//                                    if(t > quantity) {
//                                        t = 1;
//                                    }
                                    sd.dispatch(topic);
//                                    t++;
                                    factPart.setLength(0);
                                    startRecNo=count;
                                    result = null;
                                }
                            }
                        }
                    }
                }
            }
            if(factPart.length() > 0) {
                slideQty ++;
                result = new StringBuilder(768000);
                result.append(declarePart);
                result.append(factPart);
                sd.setMessage(result.toString());
//                if(t >= quantity) {
//                    t = 1;
//                }
                sd.dispatch(topic);
//                t++;
                factPart.setLength(0);
                System.out.println("slideQty=====>" + slideQty);
                System.out.println("slideSize=====>" + slideSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (Exception e2) {
                }
            }
        }
    }
    
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
    
    
    
    
    
    
    
    
}
