package simulation;

import agents.Agent;
import main.Card;

import java.util.LinkedList;

public class Playerlog {

    private int playerIndex;
    String[] hand = new String[6];
    int numRoomsVisited = 0;
    int numSuggestions = 0;

    public Playerlog(Agent agent){
        playerIndex = agent.playerIndex;
        setHand(agent);
    }

    private void setHand(Agent agent) {
        hand[0] = "player"+playerIndex+"Hand";
        hand[1] = "String[]";
        int i = 2;
        for(String card: agent.getHand()){
            hand[i] = card;
            i++;
        }
    }

}
