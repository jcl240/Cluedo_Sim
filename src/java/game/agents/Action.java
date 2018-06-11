package agents;

import main.Card;

public class Action {

    public String actionType;
    public Card[] accusation;
    public Card[] suggestion;
    public int[] start;
    public int[] end;

    public Action(String actionType) {
        this.actionType = actionType;
    }

}
