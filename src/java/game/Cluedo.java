public class Cluedo {

    public Cluedo() {
        Card[] deck = Card.makeCards();
        Card[] envelope = Card.makeEnvelope(deck);
        deck = Card.removeCards(envelope,deck);
        Board board = new Board();
        boardGUI boardGUI = new boardGUI(board.getTiles());
    }

    public static void main(String[] args) {
        Cluedo game = new Cluedo();
    }
}
