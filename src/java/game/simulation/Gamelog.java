package simulation;

import agents.Agent;
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

    public void logAction(){

    }

    public void nextTurn(){
        turnsTaken++;
    }

    public LinkedList<LinkedList<String>> batchLog() {

        return new LinkedList<>();
    }
}
