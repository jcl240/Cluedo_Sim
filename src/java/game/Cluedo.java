public class Cluedo {

    public Cluedo() {
        Board board = new Board();
        boardGUI boardGUI = new boardGUI(board.getTiles());
    }

    public static void main(String[] args) {
        Cluedo game = new Cluedo();
    }
}
