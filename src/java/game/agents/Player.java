package agents;

import main.Card;

import java.util.LinkedList;

public interface Player {

    Action takeTurn(LinkedList<Action> possibleActions);

    void endTurn();

    Card falsifySuggestion(Card[] suggestion);

    void showCard(Card cardToShow);
}
