package agents;

import main.Card;
import main.Gamepiece;

import java.util.LinkedList;

public interface Player {

    Action takeTurn(LinkedList<Action> possibleActions);

    void endTurn();

    void falsifySuggestion(Card[] suggestion);
}
