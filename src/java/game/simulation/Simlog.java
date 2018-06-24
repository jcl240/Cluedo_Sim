package simulation;

import agents.Agent;
import com.mongodb.BasicDBObject;

import java.util.LinkedList;

public class Simlog {

    public String simName;
    public int i = 1;
    public String playerOneType;
    public String playerTwoType;
    private LinkedList<LinkedList<String>> gameResults = new LinkedList<>();

    public Simlog(String s, String playerOneType, String playerTwoType) {
        simName = s;
        this.playerOneType = playerOneType;
        this.playerTwoType = playerTwoType;
    }
    int playerOneWinCount = 0;
    int playerTwoWinCount = 0;

    public BasicDBObject batchLog() {
        BasicDBObject doc = new BasicDBObject();
        doc.put("Game_results",Logger.createDBObject(gameResults));
        doc.put(playerOneType+"_Wins", playerOneWinCount);
        doc.put(playerTwoType+"_Wins", playerTwoWinCount);
        return doc;
    }

    public void addGameResults(String s, Agent winner){
        LinkedList<String> result = new LinkedList<>();
        result.add("Game"+i);
        result.add("String");
        result.add(winner.playerType);
        gameResults.add(result);
        i++;
        if(winner.playerType.equals(playerOneType)){
            playerOneWinCount++;
        }
        else
            playerTwoWinCount++;
    }
}
