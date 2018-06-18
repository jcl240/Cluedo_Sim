package agents;
import main.Card;

public class Agent {

    public boolean justMoved = false;
    public int playerIndex;
    public boolean hasSuggested = false;

    protected Card[] hand;
    protected Notebook notebook;
    public Boolean accused = false;

    public Agent(Card[] hand, Card[] faceUp, int index) {
        this.hand = hand;
        this.notebook = new Notebook(hand);
        this.playerIndex = index+1;
        for(Card card: faceUp){
            notebook.checkOffCard(card);
        }
    }

}
