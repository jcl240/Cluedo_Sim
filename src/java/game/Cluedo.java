import java.util.Arrays;

public class Cluedo {

    private Card[] deck;
    private Card[] envelope;
    private Card[] faceUpCards;
    private boolean useGUI = true;
    private BoardGUI boardGUI;
    private Board board;

    public Cluedo() {
        initializeCards();
        initializePlayers();
        dealCards();
        board = new Board();
        if(useGUI)
            boardGUI = new BoardGUI(board.getTiles());
        play();
    }

    private void play() {

    }

    private void dealCards() {

    }

    private void initializePlayers() {

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
