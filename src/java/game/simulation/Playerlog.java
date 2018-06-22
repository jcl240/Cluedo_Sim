package simulation;

import agents.Agent;
import com.mongodb.BasicDBObject;
import main.Card;

import java.util.LinkedList;

public class Playerlog {

    public int playerIndex;
    LinkedList<String> hand = new LinkedList<>();
    LinkedList<LinkedList<String>> suggestions = new LinkedList<>();
    int numRoomsVisited = 0;
    int numSuggestions = 1;

    public Playerlog(Agent agent){
        playerIndex = agent.playerIndex;
        agent.setLog(this);
        setHand(agent);
    }

    private void setHand(Agent agent) {
        hand.add("player"+playerIndex+"Hand");
        hand.add("String[]");
        int i = 2;
        for(String card: agent.getHand()){
            hand.add(card);
            i++;
        }
    }

    public void logSuggestion(int numKnown, LinkedList<String> suggestionList){
        suggestionList.addFirst("LinkedList<String>");
        suggestionList.addFirst("Suggestion"+numSuggestions);
        suggestions.add(suggestionList);
        numSuggestions++;
    }

    public BasicDBObject makeLog() {

    }
}
