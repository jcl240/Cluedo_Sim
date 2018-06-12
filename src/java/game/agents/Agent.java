package agents;
import main.Card;

public class Agent {

    public boolean justMoved = false;

    protected Card[] hand;

    public Agent(Card[] hand) {
        this.hand = hand;
    }

}
