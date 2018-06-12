package agents;

import main.Card;
import main.Room;

public class Action {

    public String actionType;
    public Card[] accusation;
    public Card[] suggestion;
    public int[] towards;
    public int roll;
    public Room currentRoom;

    public Action(String actionType) {
        this.actionType = actionType;
    }

    public Action(String move, int roll) {
        this.actionType = move;
        this.roll = roll;
    }

    public Action(String accuse, Card[] accusation) {
        this.actionType = accuse;
        this.accusation = accusation;
    }

    public Action(String suggest, Room room) {
        this.actionType = suggest;
        this.currentRoom = room;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final Action other = (Action)obj;
        return (this.actionType.equals(other.actionType));
    }
}
