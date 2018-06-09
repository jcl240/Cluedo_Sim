import java.util.Arrays;

public class Cluedo {

    public Cluedo() {
        Card[] deck = Card.makeCards();
        Card[] envelope = Card.makeEnvelope(deck);
        deck = Card.removeCards(envelope,deck);
        deck = Card.shuffle(deck);
        Card[] faceUp = new Card[]{deck[0], deck[1]};
        deck = Arrays.copyOfRange(deck,2,deck.length);
        Board board = new Board();
        boardGUI boardGUI = new boardGUI(board.getTiles());
    }

    public static void main(String[] args) {
        Cluedo game = new Cluedo();
    }
}
