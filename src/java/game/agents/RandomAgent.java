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
        if(possibleActions.contains(new Action("accuse")) && notebook.unknownCardCount() == 3)
            return(new Action("accuse"));
        else
            return null;
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
        notebook.checkOffCard(cardToShow);
    }
}
