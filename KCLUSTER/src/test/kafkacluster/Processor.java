package test.kafkacluster;

import test.kafkacluster.dispatcher.NewDispatcher;

public class Processor {

    public static void main(String[] args) throws Exception {
//        if(args.length != 2) {
//            throw new IllegalArgumentException("aaa");
//        } else {
//            Dispatcher d = new Dispatcher(args[0], args[1], args[2]);
//            d.dispatch();
//        Thread.sleep(5000);
        NewDispatcher d = new NewDispatcher(args);
        d.dispatch();
//        }
    }
}
