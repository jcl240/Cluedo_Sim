package simulation;

import agents.Agent;
import com.mongodb.BasicDBObject;

import java.util.LinkedList;

public class Simlog {

    public String simName;
    private int totalTurns = 0;
    private int maxTurns = 0;
    private int minTurns = 1000;
    public int i = 1;
    private double playerOneEntropyDecreaseAverage = 0;
    private double playerTwoEntropyDecreaseAverage = 0;
    private double playerOneNumKnownAverage = 0;
    private double playerTwoNumKnownAverage = 0;
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
        doc.put("Average_turns", totalTurns/(i-1));
        doc.put(playerOneType + " average decrease in entropy", playerOneEntropyDecreaseAverage);
        doc.put(playerTwoType + " average decrease in entropy", playerTwoEntropyDecreaseAverage);
        doc.put("Max turns", maxTurns);
        doc.put("Min turns", minTurns);
        return doc;
    }

    public void addGameResults(int turns, Agent winner, LinkedList<Playerlog> playerLogs){
        LinkedList<String> result = new LinkedList<>();
        if(turns > maxTurns){
            maxTurns=turns;
        }
        else if(turns < minTurns){
            minTurns=turns;
        }
        totalTurns += turns;
        result.add("Game"+i);
        result.add("String");
        result.add(winner.playerType);
        gameResults.add(result);
        if(winner.playerType.equals(playerOneType)){
            playerOneWinCount++;
        }
        else
            playerTwoWinCount++;
        getAverageDecreaseInEntropy(playerLogs);
        i++;
    }

    private void getAverageDecreaseInEntropy(LinkedList<Playerlog> playerLogs) {
        for(Playerlog log: playerLogs){
            if(log.agentType.equals(playerOneType)){
                playerOneEntropyDecreaseAverage = playerOneEntropyDecreaseAverage + ((log.averageEntropyDecrease-playerOneEntropyDecreaseAverage)/i);
                playerOneNumKnownAverage = playerOneNumKnownAverage + ((log.averageNumCardsKnown-playerOneNumKnownAverage)/i);
            }
            else if(log.agentType.equals(playerTwoType)){
                playerTwoEntropyDecreaseAverage = playerTwoEntropyDecreaseAverage + ((log.averageEntropyDecrease-playerTwoEntropyDecreaseAverage)/i);
                playerTwoNumKnownAverage = playerTwoNumKnownAverage + ((log.averageNumCardsKnown-playerTwoNumKnownAverage)/i);
            }
        }
    }
}
