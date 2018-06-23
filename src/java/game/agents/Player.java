package agents;

import GUI.BoardGUI;
import main.Card;

import java.util.LinkedList;

public interface Player {

    Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation);

    void endTurn();

    Card falsifySuggestion(Player player, Card[] suggestion);

    void showCard(Player player,Card cardToShow);

    String[] getHand();

    void actionFailed(Action actionTaken);

    void noCardToShow(Action actionTaken, Player player);
}
