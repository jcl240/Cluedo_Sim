package agents;

import main.Card;

public class Action {

    public String actionType;
    public Card[] accusation;
    public Card[] suggestion;
    public int[] start;
    public int[] end;
    public int roll;

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
}
