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
    public int averageNumCardsKnown = 0;
    int numSuggestions = 0;
    double averageEntropyDecrease = 0;
    double entropyBefore;
    private Agent myAgent;
    public String agentType;

    public Playerlog(Agent agent){
        playerIndex = agent.playerIndex;
        agent.setLog(this);
        setHand(agent);
        this.myAgent = agent;
        this.agentType=agent.playerType;
    }

    private void setHand(Agent agent) {
        int i = 2;
        for(String card: agent.getHand()){
            hand.add(card);
            i++;
        }
    }

    public void logSuggestion(int numKnown, LinkedList<String> suggestionList){
        numSuggestions++;
        suggestionList.addFirst("LinkedList<String>");
        suggestionList.addFirst("Suggestion"+numSuggestions+1);
        suggestions.add(suggestionList);
        averageNumCardsKnown = averageNumCardsKnown + ((numKnown-averageNumCardsKnown)/numSuggestions);
    }

    public void entropyBefore(double currentEntropy){
        entropyBefore=currentEntropy;
    }

    public void entropyAfter(double currentEntropy){
        double diff = entropyBefore - currentEntropy;
        averageEntropyDecrease = averageEntropyDecrease + ((diff-averageEntropyDecrease)/numSuggestions);
        entropyBefore = 0;
    }

    public BasicDBObject makeLog() {
        BasicDBObject doc = new BasicDBObject();
        doc.put("Player"+playerIndex+"_Hand", hand);
        doc.put("Suggestions", Logger.createDBObject(suggestions));
        doc.put("Rooms_Visited", numRoomsVisited);
        return doc;
    }
}
