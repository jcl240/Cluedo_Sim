package simulation;

import agents.Agent;
import com.mongodb.BasicDBObject;

import java.util.LinkedList;

public class Simlog {

    public String simName;
    public int i = 1;
    private LinkedList<LinkedList<String>> gameResults = new LinkedList<>();

    public Simlog(String s) {
        simName = s;
    }

    public BasicDBObject batchLog() {
        return Logger.createDBObject(gameResults);
    }

    public void addGameResults(String s, Agent winner){
        LinkedList<String> result = new LinkedList<>();
        result.add("Game"+i);
        result.add("String");
        result.add(winner.playerType);
        gameResults.add(result);
        i++;
    }
}
