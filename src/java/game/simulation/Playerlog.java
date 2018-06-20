package simulation;

import agents.Agent;
import main.Card;

import java.util.LinkedList;

public class Playerlog {

    private int playerIndex;
    LinkedList<String> hand = new LinkedList<>();
    int numRoomsVisited = 0;
    int numSuggestions = 0;

    public Playerlog(Agent agent){
        playerIndex = agent.playerIndex;
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

}
