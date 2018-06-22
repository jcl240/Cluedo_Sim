package simulation;

import agents.Agent;
import com.mongodb.BasicDBObject;
import main.Card;

import java.util.LinkedList;

public class Playerlog {

    public int playerIndex;
    LinkedList<String> hand = new LinkedList<>();
    LinkedList<LinkedList<String>> suggestions = new LinkedList<>();
    public int numRoomsVisited = 0;
    int numSuggestions = 1;

    public Playerlog(Agent agent){
        playerIndex = agent.playerIndex;
        agent.setLog(this);
        setHand(agent);
    }

    private void setHand(Agent agent) {
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
        BasicDBObject doc = new BasicDBObject();
        doc.put("Player"+playerIndex+"_Hand", hand);
        doc.put("Suggestions", Logger.createDBObject(suggestions));
        doc.put("Rooms_Visited", numRoomsVisited);
        return doc;
    }
}
