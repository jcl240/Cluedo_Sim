package simulation;

import agents.Action;
import agents.Agent;
import agents.Player;
import com.mongodb.BasicDBObject;
import main.Card;

import java.util.LinkedList;

public class Gamelog {

    private LinkedList<LinkedList<String>> actionLog = new LinkedList<>();
    private LinkedList<Playerlog> playerLogs = new LinkedList<>();
    private LinkedList<LinkedList<String>> startState = new LinkedList<>();
    private int turnsTaken = 0;

    public Gamelog(Agent[] playerList, Card[] envelope,Card[] faceUpCards){
        initializePlayerLogs(playerList);
        setStartState(playerList, envelope, faceUpCards);
    }

    private void initializePlayerLogs(Agent[] playerList) {
        for(Agent agent: playerList){
            playerLogs.add(new Playerlog(agent));
        }
    }

    private void setStartState(Agent[] playerList,Card[] envelope,Card[] faceUpCards){
        startState.add(getCardNames("envelope",envelope));
        startState.add(getCardNames("faceUpCards",faceUpCards));
    }

    private LinkedList<String> getCardNames(String fieldName, Card[] cards){
        LinkedList<String> cardNames = new LinkedList<>();
        cardNames.add(fieldName);
        cardNames.add("LinkedList<String>");
        for(Card card: cards){
            cardNames.add(card.cardName);
        }
        return cardNames;
    }

    public void logAction(Action action, Player player){
        LinkedList<String> actionList = new LinkedList<>();
        actionList.add("Turn"+turnsTaken);
        actionList.add("LinkedList<String>");
        actionList.add("Player"+((Agent)player).playerIndex);
        actionList.add(action.actionType);
        actionLog.add(actionList);
    }

    public void nextTurn(){
        turnsTaken++;
    }

    public BasicDBObject batchLog() {
        BasicDBObject startStateLog = getStartStateLog();
        BasicDBObject actionLog = getActionLog();
        BasicDBObject[] playerLogList = getPlayerLogs();
        BasicDBObject document = new BasicDBObject();
        document.put("Start_State", startStateLog);
        document.put("Action_Log", actionLog);
        document.put("Turns_taken", turnsTaken);
        int i = 1;
        for(BasicDBObject playerLog: playerLogList) {
            document.put("Player_Log"+i, playerLog);
            i++;
        }
        return document;
    }

    private BasicDBObject[] getPlayerLogs() {
        BasicDBObject[] playerLogList = new BasicDBObject[4];
        for(Playerlog playerLog: playerLogs){
            playerLogList[playerLog.playerIndex-1] = playerLog.makeLog();
        }
        return playerLogList;
    }

    private BasicDBObject getActionLog() {
        return Logger.createDBObject(actionLog);
    }

    private BasicDBObject getStartStateLog() {
        return Logger.createDBObject(startState);
    }

    public int getTurnsTaken() {
        return turnsTaken;
    }
}
