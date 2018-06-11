package agents;

import main.Card;

import java.util.LinkedList;

public class HumanAgent extends  Agent implements Player{

    public HumanAgent(Card[] hand) {
        super(hand);
    }

    @Override
    public void endTurn(){

    }

    @Override
    public boolean inRoomWithSecretPassage() {
        return false;
    }

    public Action takeTurn(LinkedList<Action> possibleActions){

        return new Action("");
    }

    public boolean inRoom(){

        return false;
    }
}
