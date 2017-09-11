package test.kafkacluster.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Random;

import org.xerial.snappy.Snappy;

import test.kafkacluster.concurrent.ReceiveThread;

public final class Functions {

    public static void snappyAsFile(File fi, String outFile) {
        InputStream is = null;
        OutputStream os = null;
        byte[] b = null;
        try {
            b = new byte[(int) fi.length()];
            is = new FileInputStream(fi);
            is.read(b);
            os = new FileOutputStream(new File(outFile));
            os.write(Snappy.compress(b));
        } catch (Exception e) {
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e2) {
                }
            }
            if(os != null) {
                try {
                    os.close();
                } catch (Exception e2) {
                }
            }
        }
    }
    public static File makeZipFile(byte[] fileByte, String zipFileName) {
        File zipFile = new File(zipFileName);
        OutputStream os = null;
        try {
            if (!zipFile.exists())
                zipFile.createNewFile();
            os = new FileOutputStream(zipFile);
            os.write(fileByte);
        } catch (Exception e) {
            // throw new FileUploadException();
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch (Exception e) {
                }
        }
        return zipFile;
    }
    public static void composeRuleSet(String entryPoint,
            String valDir) {
        StringBuffer sb = new StringBuffer();
        OutputStream os = null;
        try {
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            sb.append("<validationRule>\n");
            sb.append("<vRuleSetId>VS00001</vRuleSetId>\n");
            sb.append("<vRuleSetDesc>Rule Set For CAS user</vRuleSetDesc>\n");
            sb.append("<vTaxonomyPath></vTaxonomyPath>\n");
            sb.append("<vInstancePath></vInstancePath>\n");
            sb.append("<vCompanyCode>123456789012345</vCompanyCode>\n");
            sb.append("<VRule><vRuleId>VA1029</vRuleId><vConfigId>VC00003_1</vConfigId><vExecute>ON</vExecute><vType>INS</vType></VRule>\n");
            sb.append("</validationRule>\n");
//            returnFile = valDir + "RuleSet_" + randomIt() + ".xml";
            os = new FileOutputStream(new File(valDir));
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
    }
    
    public static long randomIt() {
        Random random = new Random();
        long value = random.nextLong();
        return value >= 0 ? value : -value;
    }
    
    public static final String getRoot() {
        URL url = ReceiveThread.class.getProtectionDomain().getCodeSource().getLocation();
        String root = null;
        try {
            root = URLDecoder.decode(url.getFile(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        root = root.substring(0, root.lastIndexOf("/"));
        root = root.replace("\\", "/");
        if(root.endsWith("/")) {
            root = root.substring(0, root.length() -1);
            root =  root.replace("//", "/");
        }
        return root;
    }
}
