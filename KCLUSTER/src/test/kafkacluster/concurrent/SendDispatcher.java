package test.kafkacluster.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.javaapi.producer.Producer;

public class SendDispatcher {
    private ExecutorService es = null;
    private String message;
    private Producer<String, String> producer;
    
    
    public SendDispatcher(int thdQty, Producer<String, String> producer) {
        es = Executors.newFixedThreadPool(thdQty);
        this.producer = producer;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public void dispatch(String topic) {
        SendThread sr = new SendThread(producer, message, topic);
        es.execute(sr);
    }
    
}
