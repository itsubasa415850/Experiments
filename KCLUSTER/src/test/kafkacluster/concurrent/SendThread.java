package test.kafkacluster.concurrent;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

public class SendThread implements Runnable {
    private Producer<String, String> producer;
    private String data;
    private String topic;
    public SendThread(Producer<String, String> producer, String data, String topic) {
        this.producer = producer;
        this.data = data;
        this.topic = topic;
    }

    public void run() {
//        System.out.println("send to:" + topic);
        producer.send(new KeyedMessage<String, String>(topic, data));
    }

}
