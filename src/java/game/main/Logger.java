package main;

import com.mongodb.*;

public class Logger {

    MongoClient mongoClient;
    DB database;
    DBCollection collection;

    public Logger(){
        initializeMongo();
    }

    private void initializeMongo() {
        mongoClient = new MongoClient();
        database = mongoClient.getDB("testdb");
        collection = database.getCollection("testCollection");
        DBObject query = new BasicDBObject("first_name","Joe");
        DBCursor cursor = collection.find(query);
        DBObject joe = cursor.one();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                mongoClient.close();
            }
        }));
    }
}
