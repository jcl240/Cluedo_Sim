package simulation;

import agents.Agent;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Logger {

    MongoClient mongoClient;
    DB database;
    private DBCollection simulationCollection;
    private DBCollection gameCollection;
    private String simulationName;

    public Logger(String simulationName){
        this.simulationName = simulationName;
        initializeMongo();
    }

    private void initializeMongo() {
        mongoClient = new MongoClient();
        database = mongoClient.getDB("testdb");
        simulationCollection = database.getCollection("simulationCollection");
        gameCollection = database.getCollection("gameCollection");
        clearCollections();


        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                mongoClient.close();
            }
        }));
    }

    private void clearCollections() {
        BasicDBObject document = new BasicDBObject();

        // Delete All documents from collection Using blank BasicDBObject
        simulationCollection.remove(document);
        gameCollection.remove(document);

    }

    private void storeSimulation(Simlog simlog){
        BasicDBObject simDoc = simlog.batchLog();
        BasicDBObject document = new BasicDBObject();
        document.put("Simlog",simDoc);
        simulationCollection.insert(document);
    }

    public static BasicDBObject createDBObject(LinkedList<LinkedList<String>> log) {
        BasicDBObject document = new BasicDBObject();
        for(LinkedList<String> fieldList: log){
            String fieldName = fieldList.removeFirst();
            String fieldType = fieldList.removeFirst();
            switch (fieldType) {
                case "LinkedList<String>":
                    document.put(fieldName, fieldList);
                    break;
                case "int": {
                    int fieldValue = Integer.getInteger(fieldList.removeFirst());
                    document.put(fieldName, fieldValue);
                    break;
                }
                case "String": {
                    String fieldValue = fieldList.removeFirst();
                    document.put(fieldName, fieldValue);
                    break;
                }
            }
        }
        return document;
    }

    public void storeGame(Gamelog gamelog, Agent winner){
        BasicDBObject gameDoc = gamelog.batchLog();
        UUID uniqueID = UUID.randomUUID();
        BasicDBObject document = new BasicDBObject();
        document.put("Gamelog",gameDoc);
        document.put("Game_ID", uniqueID.toString());
        storeSimulation(new Simlog(uniqueID.toString(),winner));
        gameCollection.insert(document);
    }

}
