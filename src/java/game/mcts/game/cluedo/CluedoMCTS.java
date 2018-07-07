package mcts.game.cluedo;

import main.Board;
import mcts.game.Game;
import mcts.tree.node.TreeNode;
import mcts.utils.Options;

import java.util.ArrayList;


public class CluedoMCTS implements Game, GameStateConstants {

    /* 0: Playing or winner's index, 1: current player's index
       2: current player accused, 3: current player justMoved
       4: current player hasSuggested, 5: current player's room
       6: current roll
    */
    private int[] state;


    public static long breadth = 0;
    public static long depth = 0;
    private Board board;

    public static final int PLAYING = 0;

    public CluedoMCTS(int[] state) {
        this.state = state;
    }

    public CluedoMCTS() {
        initGame();
    }

    private void initGame() {
    }

    @Override
    public int[] getState() {
        return this.state;
    }

    @Override
    public int getWinner() {
        return state[0];
    }

    @Override
    public boolean isTerminal() {
        if(state[0] == PLAYING)
            return false;
        return true;
    }

    @Override
    public int getCurrentPlayer() {
        return state[1];
    }

    @Override
    public void performAction(int[] a, boolean sample) {

        switch(a[0]){
            case MOVE:
                break;
            case SECRET_PASSAGE:
                break;
            case SUGGEST:
                break;
            case FALSIFY:
                break;
            case ACCUSE:
                break;
        }

    }

    @Override
    public Options listPossiblities(boolean sample) {
        Options options = new Options();
        if(state[2] != 0)
            return options;
        if(state[3] == 0) {
            listMovePossibilities(options, state[6]);
            listAccusePossibilities(options);
            if (inRoomWithSecretPassage(state[5]))
                listSecretPassagePossibility(options);
        }
        if(state[5] != 0 && state[4] == 0)
            listSuggestionPossibilities(options, state[5]);
        return options;
    }

    private void listMovePossibilities(Options options, int i) {

    }

    private void listAccusePossibilities(Options options) {

    }

    private boolean inRoomWithSecretPassage(int i) {

        return false;
    }

    private void listSecretPassagePossibility(Options options) {

    }

    private void listSuggestionPossibilities(Options options, int i) {
    }

    @Override
    public Game copy() {
        return null;
    }

    @Override
    public int[] sampleNextAction() {
        return new int[0];
    }

    @Override
    public int sampleNextActionIndex() {
        return 0;
    }

    @Override
    public TreeNode generateNode() {
        return null;
    }

    @Override
    public void gameTick() {

    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
