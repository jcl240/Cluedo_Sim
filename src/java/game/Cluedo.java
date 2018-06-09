import java.util.Arrays;

public class Cluedo {

    public Card[] deck;
    public Card[] envelope;
    public Card[] faceUpCards;

    public Cluedo() {
        initializeCards();
        Board board = new Board();
        boardGUI boardGUI = new boardGUI(board.getTiles());
    }

    public void initializeCards(){
        deck = Card.makeCards();
        envelope = Card.makeEnvelope(deck);
        deck = Card.removeCards(envelope,deck);
        deck = Card.shuffle(deck);
        faceUpCards = new Card[]{deck[0], deck[1]};
        deck = Arrays.copyOfRange(deck,2,deck.length);
    }

    public static void main(String[] args) {
        Cluedo game = new Cluedo();
    }
}
