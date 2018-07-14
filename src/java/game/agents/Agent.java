package agents;
import main.Board;
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

    public Agent(Agent x) {
        this.playerIndex = x.playerIndex;
        this.hasSuggested = x.hasSuggested;
        this.hand = x.hand.clone();
        this.playerType = x.playerType;
        this.notebook = x.notebook;
        this.justMoved = x.justMoved;
        this.accused = x.accused;
    }

    public Agent(int index) {
        this.playerIndex = index;
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

    public void setBoard(Board board){};

    public Card[] getHandArray() {
        return hand;
    }
}
