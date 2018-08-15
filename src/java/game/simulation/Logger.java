package simulation;

import agents.Agent;
import com.mongodb.*;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Logger {

    MongoClient mongoClient;
    DB database;
    private DBCollection simulationCollection;
    private DBCollection gameCollection;
    public Simlog simlog;

    public Logger(String simulationName, String playerOneType, String playerTwoType){
        simlog = new Simlog(simulationName,playerOneType,playerTwoType);
        //initializeMongo();
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

    public void storeSimulation(){
        BasicDBObject simDoc = simlog.batchLog();
        BasicDBObject document = new BasicDBObject();
        document.put(simlog.simName ,simDoc);
        JsonWriterSettings settings = JsonWriterSettings.builder().indent(true).build();
        String docString = document.toJson(settings);
        File file = new File("Cluedo_Sim/out/artifacts/Simulator/"+simlog.simName+"/simlogs/simlogb");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(docString);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //simulationCollection.insert(document);
    }

    public static BasicDBObject createDBObject(LinkedList<LinkedList<String>> log) {
        BasicDBObject document = new BasicDBObject();
        for(LinkedList<String> fieldList: log){
            String fieldName = fieldList.get(0);
            String fieldType = fieldList.get(1);
            switch (fieldType) {
                case "LinkedList<String>":
                    LinkedList<String> subList = getSubList(fieldList);
                    document.put(fieldName, subList);
                    break;
                case "int": {
                    int fieldValue = Integer.getInteger(fieldList.get(2));
                    document.put(fieldName, fieldValue);
                    break;
                }
                case "String": {
                    String fieldValue = fieldList.get(2);
                    document.put(fieldName, fieldValue);
                    break;
                }
            }
        }
        return document;
    }

    private static LinkedList<String> getSubList(LinkedList<String> fieldList) {
        LinkedList<String> subList = (LinkedList<String>)fieldList.clone();
        subList.removeFirst();
        subList.removeFirst();
        return subList;
    }

    public void storeGame(Gamelog gamelog, Agent winner){
        BasicDBObject gameDoc = gamelog.batchLog();
        UUID uniqueID = UUID.randomUUID();
        BasicDBObject document = new BasicDBObject();
        document.put("Gamelog",gameDoc);
        document.put("Game_ID", uniqueID.toString());
        simlog.addGameResults(gamelog.getTurnsTaken(),winner,gamelog.playerLogs);
        JsonWriterSettings settings = JsonWriterSettings.builder().indent(true).build();
        String docString = document.toJson(settings);
        File file = new File("Cluedo_Sim/out/artifacts/Simulator/"+simlog.simName+"/gamelogs/gamelogb"+simlog.i);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(docString);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //gameCollection.insert(document);
    }

}
