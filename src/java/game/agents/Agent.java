package agents;
import main.Card;
import simulation.Playerlog;

public class Agent {

    public boolean justMoved = false;
    public int playerIndex;
    public boolean hasSuggested = false;
    public Playerlog playerLog;
    public String playerType;
    protected Card[] hand;
    protected Notebook notebook;
    public Boolean accused = false;

    public Agent(Card[] hand, Card[] faceUp, int index, String type) {
        this.hand = hand;
        this.notebook = new Notebook(hand);
        this.playerIndex = index+1;
        for(Card card: faceUp){
            notebook.checkOffCard(card);
        }
        this.playerType = type;
    }

    public Agent(Card[] hand, int index, String type) {
        this.hand = hand;
        this.notebook = new Notebook(hand);
        this.playerIndex = index+1;
        this.playerType = type;
    }

    public String[] getHand() {
        String[] stringHand = new String[4];
        int i = 0;
        for(Card card: hand) {
            stringHand[i] = card.cardName;
            i++;
        }
        return stringHand;
    }

    public void setLog(Playerlog playerlog) {
        this.playerLog = playerlog;
    }
}
