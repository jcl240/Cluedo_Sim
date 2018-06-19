package main;

import com.mongodb.*;

public class Logger {

    MongoClient mongoClient;
    DB database;
    private DBCollection simulationCollection;
    private DBCollection gameCollection;

    public Logger(){
        initializeMongo();
    }

    private void initializeMongo() {
        mongoClient = new MongoClient();
        database = mongoClient.getDB("testdb");
        simulationCollection = database.getCollection("simulationCollection");
        gameCollection = database.getCollection("gameCollection");


        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                mongoClient.close();
            }
        }));
    }

    private void storeSimulation(BasicDBObject simulation){
        simulationCollection.insert(simulation);
    }

    private void storeGame(BasicDBObject game){
        simulationCollection.insert(game);
    }

}
