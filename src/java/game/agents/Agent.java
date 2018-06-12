package agents;
import main.Card;

public class Agent {

    public boolean justMoved = false;

    protected Card[] hand;
    protected Notebook notebook;
    public Boolean accused = false;

    public Agent(Card[] hand, Card[] faceUp) {
        this.hand = hand;
        this.notebook = new Notebook(hand);
        for(Card card: faceUp){
            notebook.checkOffCard(card);
        }
    }

}
