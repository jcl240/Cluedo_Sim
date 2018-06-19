package simulation;

import com.mongodb.*;

import java.util.LinkedList;

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

    private void storeSimulation(LinkedList<String[]> log){
        BasicDBObject simulation = createDBObject(log);
        simulationCollection.insert(simulation);
    }

    private BasicDBObject createDBObject(LinkedList<String[]> log) {
        BasicDBObject document = new BasicDBObject();
        for(String[] fieldValue: log){
            document.put(fieldValue[0], fieldValue[1]);
        }
        return document;
    }

    private void storeGame(LinkedList<String[]> log){
        BasicDBObject game = createDBObject(log);
        simulationCollection.insert(game);
    }

}
