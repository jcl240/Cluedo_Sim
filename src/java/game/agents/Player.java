package agents;

import java.util.LinkedList;

public interface Player {

    Action takeTurn(LinkedList<Action> possibleActions);

    boolean inRoomWithSecretPassage();

    void endTurn();

    boolean inRoom();
}
