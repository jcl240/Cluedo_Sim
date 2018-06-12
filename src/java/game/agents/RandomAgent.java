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

    public Action takeTurn(LinkedList<Action> possibleActions){

        return new Action("");
    }

    @Override
    public Card falsifySuggestion(Card[] suggestion) {
        for(Card suggestedCard: suggestion){
            for(Card myCard: this.hand){
                if(suggestedCard == myCard) {
                    return myCard;
                }
            }
        }
        return null;
    }

    @Override
    public void showCard(Card cardToShow) {

    }
}
