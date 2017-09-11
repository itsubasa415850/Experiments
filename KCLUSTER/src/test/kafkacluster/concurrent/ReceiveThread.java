package test.kafkacluster.concurrent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.com.nttdata.xbrl.common.XbrlProperties;
import cn.com.nttdata.xbrl.validateInstance.IXbrlController;
import cn.com.nttdata.xbrl.validationControl.XbrlController;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import test.kafkacluster.functions.Functions;
import test.kafkacluster.functions.MyILog;

public class ReceiveThread implements Runnable {
    private ConsumerConnector consumer;
    private static final String config = "F:\\zktest\\config\\";
    private static final String desc = "F:\\zktest\\desc\\received\\";
    private static final String entryPoint = "F:\\zktest\\reference\\A-100000000032057-20151231.xsd";
    private static final String val = "F:\\zktest\\desc\\rulesets\\";
    private static final String result = "F:\\zktest\\desc\\result\\";
    private String topic;
    private String dir;

    public ReceiveThread(String topic, String dir) {
        this.topic = topic;
        this.dir = dir;
    }

    static {
        File f = new File(desc);
        if(!f.exists()) {
            f.mkdir();
        }
        f = new File(val);
        if(!f.exists()) {
            f.mkdir();
        }
        f = new File(result);
        if(!f.exists()) {
            f.mkdir();
        }
    }
    
    @Override
    public void run() {
        init(dir);
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        long start = System.currentTimeMillis();
        while(it.hasNext()) {
            proc(it.next().message());
        }
        System.out.println("total receive elapsed:======" + topic + "=========>" + (System.currentTimeMillis() - start));
    }
    
    
    private void proc(byte[] b) {
        final long fileName = Functions.randomIt();
        Functions.makeZipFile(b, desc + fileName + ".xml");
        String ruleSet = val + "RuleSet_" + fileName + ".xml";
        Functions.composeRuleSet(entryPoint, ruleSet);
        MyILog xbrlLog = new MyILog("f:\\log.txt");
        IXbrlController controller = new XbrlController();
        String pid = "";
        InputStream is = null;
        OutputStream os = null;
        StringBuilder sb = new StringBuilder();
        List<String> lst = null;  
        try {
            XbrlProperties.loadProperties();
            is = new ByteArrayInputStream(b);
            pid = controller.start(xbrlLog, "f:\\zktest\\fake.lic");
            controller.readValidationConfig(pid, ruleSet, config, is);
            controller.validate(pid);
            controller.cancel(pid);
            lst = controller.getErrList();
            String returnFile = result + "RESULT_" + fileName + ".txt";
            int length = lst.size();
            for (int i = 0; i < length; i++) {
                sb.append(lst.get(i).concat("\r\n"));
            }
            os = new FileOutputStream(new File(returnFile));
            os.write(sb.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
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
    
    private void init(String dir) {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(Functions.getRoot() + "/properties/config.properties"));
            Properties props = new Properties();
            props.load(is);
            //设置KAFKA需要的属性值
            props.put("zookeeper.connect", props.get("kafka.zookeeper.list"));
            props.put("group.id", "jd-group");
            props.put("zookeeper.session.timeout.ms", "4000");
            props.put("zookeeper.sync.time.ms", "200");
            props.put("auto.commit.interval.ms", "1000");
            props.put("auto.offset.reset", "smallest");
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            //初始化KAFKA接收者
            ConsumerConfig config = new ConsumerConfig(props);
            consumer = Consumer.createJavaConsumerConnector(config);
        } catch (Exception e) {
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e2) {
                }
            }
        }
    }
}
