package agents;

import GUI.BoardGUI;
import main.Card;

import java.util.LinkedList;

public class HumanAgent extends  Agent implements Player{

    private BoardGUI boardGUI;

    public HumanAgent(Card[] hand, Card[] faceUp, int index) {
        super(hand, faceUp, index);
    }

    public HumanAgent(Card[] hand, BoardGUI gui, Card[] faceUp, int index) {
        super(hand, faceUp, index);
        this.boardGUI = gui;
    }

    @Override
    public void endTurn(){

    }

    public Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation){

        return new Action("");
    }


    @Override
    public Card falsifySuggestion(Card[] suggestion) {

        return null;
    }

    @Override
    public void showCard(Card cardToShow) {

    }

    @Override
    public String[] getHand() {
        String[] stringHand = new String[4];
        int i = 0;
        for(Card card: hand) {
            stringHand[i] = card.cardName;
            i++;
        }
        return stringHand;
    }
}
