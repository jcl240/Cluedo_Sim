package agents;

import main.Card;

import java.util.LinkedList;

public class RandomAgent extends  Agent implements Player {

    public RandomAgent(Card[] hand) {
        super(hand);
    }

    @Override
    public void endTurn(){
        this.justMoved = false;
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
