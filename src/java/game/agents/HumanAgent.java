package agents;

import GUI.BoardGUI;
import main.Card;

import java.util.LinkedList;

public class HumanAgent extends  Agent implements Player{

    private BoardGUI boardGUI;

    public HumanAgent(Card[] hand, Card[] faceUp) {
        super(hand, faceUp);
    }

    public HumanAgent(Card[] hand, BoardGUI gui, Card[] faceUp) {
        super(hand, faceUp);
        this.boardGUI = gui;
    }

    @Override
    public void endTurn(){

    }

    public Action takeTurn(LinkedList<Action> possibleActions){

        return new Action("");
    }


    @Override
    public Card falsifySuggestion(Card[] suggestion) {

        return null;
    }

    @Override
    public void showCard(Card cardToShow) {

    }
}
