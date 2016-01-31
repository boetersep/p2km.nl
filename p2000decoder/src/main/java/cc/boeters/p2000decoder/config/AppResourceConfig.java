package cc.boeters.p2000decoder.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public class AppResourceConfig extends ResourceConfig {
    public static final String MESSAGES_COLLECTION = "messagesCollection";

    public AppResourceConfig() {
        register(new MyBinder());
        packages(true, "cc.boeters");
    }

    public class MyBinder extends AbstractBinder {

        @Override
        protected void configure() {
        	
        	
//            DB helloworld;
//            try {
//                helloworld = new MongoClient("localhost").getDB("helloworld");
//            } catch (UnknownHostException e) {
//                throw new RuntimeException(e);
//            }
//            Jongo jongo = new Jongo(helloworld);
//
//            bind(jongo.getCollection("messages")).to(MongoCollection.class).named(MESSAGES_COLLECTION);
//
//            bind(HelloService.class).to(HelloService.class);
        }
    }
}