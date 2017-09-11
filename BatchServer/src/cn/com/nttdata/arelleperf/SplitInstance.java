package cn.com.nttdata.arelleperf;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SplitInstance {

	private ArrayList<String> fixNodeLst = new ArrayList<String>();
	public  static void main(String[] args) {
		long start = System.currentTimeMillis();
		SplitInstance is = new SplitInstance(args[0], args[1], Integer.parseInt(args[2]), args[3]);
		long stop = System.currentTimeMillis();
		System.out.println("usedTime " + (stop - start) + " Millisecond");
	}
	
	public SplitInstance(String instanceFilePath,String outputFolder,int fileSize, String xsdFilePath) {
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
		Split(instanceFilePath,outputFolder,fileSize, xsdFilePath);
	}

	private void Split(String instanceFilePath,String outputFolder,int fileSize, String xsdFilePath) {
    	String fileName = instanceFilePath.substring(instanceFilePath.lastIndexOf("\\") + 1, instanceFilePath.length());
    	StringBuffer declarePart = new StringBuffer();
    	StringBuffer factPart = new StringBuffer();
    	String line;
    	String firstTupleFact = "";
    	int fileIndex = 1;
    	boolean factStart = false;
    	String res = String.format("%5s", String.valueOf(fileIndex));
    	
    	String tmpFilename = fileName.substring(0,fileName.indexOf('.')) + "_" + res.replaceAll("\\s", "0") + ".xml";
    	try {
        	BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFolder + "/" + tmpFilename));

	    	BufferedReader br = new BufferedReader(new FileReader(instanceFilePath));
	
	    	while ((line = br.readLine()) != null) {
	    		//line = line.replace("<", "\r\n<");
	    		//String[] lineSplit = line.split("\r\n");
	    		//for(int i = 0;i<lineSplit.length;i++) {
	    			String temp = line.trim();//lineSplit[i].trim();
	    			if(!"".equals(temp)) {
	    				boolean isDeclare = false;
	    				if(!factStart) {	    					
		    				for(int j = 0;j<fixNodeLst.size();j++) {
		    					if(temp.startsWith(fixNodeLst.get(j))){
		    						isDeclare = true;
		    						break;
		    					}
		    				}
	    				}
	    				if(isDeclare){
	                        if(temp.startsWith("<link:schemaRef"))
	                            temp = "<link:schemaRef xlink:type=\"simple\" xlink:href=\"" + xsdFilePath +"\" />"; 
	    					declarePart.append(temp).append("\n");
	    				} else {
	    					factStart =true;
	    					factPart.append(temp).append("\n");
	    					if("".equals(firstTupleFact) && temp.startsWith("<") && !temp.startsWith("</")){
	    						firstTupleFact = "</" + temp.substring(1,temp.length()-1)+ ">";
	    					} else {
		    					if(!"".equals(firstTupleFact) && temp.equals(firstTupleFact)){
		    						firstTupleFact = "";
		    						if(declarePart.length() + factPart.length()>fileSize){
		    							StringBuffer result = new StringBuffer();
		    							result.append(declarePart);
		    							result.append(factPart);
		    							result.append("</xbrli:xbrl>"+"\n");
		    							out.write(result.toString().getBytes("UTF-8"));
		    							out.close();
		    							factPart.setLength(0);
		    							fileIndex = fileIndex + 1;
		    							res = String.format("%5s", String.valueOf(fileIndex));
		    							tmpFilename = fileName.substring(0,fileName.indexOf('.')) + "_" + res.replaceAll("\\s", "0") + ".xml";
		    							out = new BufferedOutputStream(new FileOutputStream(outputFolder + "/" + tmpFilename));
		    						}
		    					}
	    					}
	    				}
	    			}
	    		//}
	    	}
	    	if(factPart.length()>0)
	    	{
	    		StringBuffer result = new StringBuffer();
				result.append(declarePart);
				result.append(factPart);
				result.append("</xbrli:xbrl>"+"\n");
				out.write(result.toString().getBytes("UTF-8"));
				out.close();
	    	}
	    	br.close();
	    } catch (IOException e) {
	        System.out.println(e);
	    }


    }

}
