package test.kafkacluster.concurrent;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import test.kafkacluster.functions.Functions;

public class ReceiveDispatcher extends Thread {
    private ExecutorService es = null;
    private ConsumerConnector consumer;
    private Connection[] conn = null;
    private int thdQty;
    private String sql;
    private int connQty;
    private String[] dbCredentials;
    private String desc;
    private static final String topic = "topic";
    public ReceiveDispatcher(int thdQty, String sql, int connQty, String[] dbCredentials, String desc) {
        this.thdQty = thdQty;
        this.sql = sql;
        this.dbCredentials = dbCredentials;
        this.connQty = connQty;
        this.desc = desc;
    }

    private void connect(int connQty) {
        conn = new Connection[connQty];
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            for (int i = 0; i < conn.length; i++) {
                conn[i] = DriverManager.getConnection(dbCredentials[0], dbCredentials[1], dbCredentials[2]);
                conn[i].setAutoCommit(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void init() {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(Functions.getRoot() + "/properties/config.properties"));
            Properties props = new Properties();
            props.load(is);
            //设置KAFKA需要的属性值
            props.put("metadata.broker.list", props.get("kafka.broker.list"));
            props.put("zookeeper.connect", props.get("kafka.zookeeper.list"));
            props.put("group.id", "jd-group" + 2);
            props.put("zookeeper.session.timeout.ms", "4000");
            props.put("zookeeper.sync.time.ms", "200");
            props.put("auto.commit.interval.ms", "1000");
            props.put("auto.offset.reset", "smallest");
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            //初始化KAFKA接收者
            ConsumerConfig config = new ConsumerConfig(props);
            consumer = Consumer.createJavaConsumerConnector(config);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e2) {
                }
            }
        }
    }
    public void run() {
        init();
        ReceiveCommand rc = null;
        int vno = 0;
        int i = 0;
        es = Executors.newFixedThreadPool(thdQty);
        connect(connQty);
        String msg = null;
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic/* + 1*/, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic/* + 1*/).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while(it.hasNext()) {
            i++;
            msg = new String(it.next().message());
            vno = i % 16;
            rc = new ReceiveCommand(conn[i % connQty], msg + (vno == 0 ? 16 : vno) + "%' and rownum<=10000", i, desc);
            es.execute(rc);
        }
    }
}
