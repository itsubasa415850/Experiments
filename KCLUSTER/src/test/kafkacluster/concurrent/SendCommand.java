package test.kafkacluster.concurrent;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public class SendCommand implements Runnable {
    private Producer<String, String> producer;
    private String sql;
    private String topic;
    public SendCommand(Producer<String, String> producer, String sql,
            String topic) {
        this.producer = producer;
        this.sql = sql;
        this.topic = topic;
    }
    public void run() {
        producer.send(new KeyedMessage<String, String>(topic, sql));
//        producer.send(new ProducerRecord<String, String>(topic, partition, key, value));
    }

}
