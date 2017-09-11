package test.kafkacluster.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.utils.threadsafe;
import test.kafkacluster.concurrent.ReceiveDispatcher;
import test.kafkacluster.concurrent.SendCommand;
import test.kafkacluster.functions.Functions;

public class NewDispatcher {
    private static final String[] types = {"send", "receive"};
    private Producer<String, String> producer;
    private static final String sql = "select * from zkbulk where field1 like '";
    private static final String topic = "topic";
    private String[] args;
    
    public void dispatch() throws Exception {
        int index;
        if(types[0].equals(args[0])) {
            final long start = System.currentTimeMillis();
            initKafka();
            System.out.println("向队列发送-----> STARTED：" + new SimpleDateFormat("yyyymmdd hh:mm:ss").format(new Date(System.currentTimeMillis())));
            for (index = 0; index < Integer.parseInt(args[1]); index++) {
                //发送SQL文到队列上
                new Thread(new SendCommand(producer, sql, topic /*+ (index % 2)*/)).start();
            }
            System.out.println("total send elapsed:======" + (System.currentTimeMillis() - start));
        } else if(types[1].equals(args[0])) {
            String[] dbCredentials = new String[] {args[2], args[3], args[4]};
            ReceiveDispatcher rd = new ReceiveDispatcher(
                    Integer.parseInt(args[1]), sql, Integer.parseInt(args[5]), dbCredentials, args[6]);
            rd.start();
        } else {
            throw new Exception("bbb");
        }
    }
    
    public NewDispatcher(String[] args) {
        this.args = args;
    }

    private void initKafka() {
        InputStream is = null;
        try {
//            Thread.sleep(5000);
            is = new FileInputStream(new File(Functions.getRoot() + "/properties/config.properties"));
            System.out.println(Functions.getRoot() + "/properties/config.properties");
            Properties props = new Properties();
            props.load(is);
//            props.put("metadata.broker.list", props.get("kafka.broker.list"));
            props.put("metadata.broker.list", "localhost:9092");
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            props.put("key.serializer.class", "kafka.serializer.StringEncoder");
            props.put("request.required.acks", "1");
            if(producer != null) {
                return;
            } else {
                producer = new Producer<String, String>(new ProducerConfig(props));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
