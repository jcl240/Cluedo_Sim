package agents;

import GUI.BoardGUI;
import main.Card;

import java.util.LinkedList;

public class HumanAgent extends  Agent implements Player{

    private BoardGUI boardGUI;

    public HumanAgent(Card[] hand) {
        super(hand);
    }

    public HumanAgent(Card[] hand, BoardGUI gui) {
        super(hand);
        this.boardGUI = gui;
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
