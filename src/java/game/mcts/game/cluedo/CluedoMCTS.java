package mcts.game.cluedo;

import agents.Action;
import main.Board;
import mcts.game.Game;
import mcts.tree.node.TreeNode;
import mcts.utils.Options;

import java.util.ArrayList;


public class CluedoMCTS implements Game, GameStateConstants {

    /* 0: Playing or winner's index, 1: current player's index
       2: current player accused, 3: current player justMoved
       4: current player hasSuggested, 5: current player's room
       6: current roll, 7: current player is falsifying card
    */
    private int[] state;

    private CluedoBelief belief;
    public static long breadth = 0;
    public static long depth = 0;
    private Board board;
    private CluedoConfig config;

    public static final int PLAYING = 0;

    public CluedoMCTS(int[] state, CluedoConfig config, CluedoBelief belief) {
        this.state = state;
        this.belief = belief;
        this.config = config;
    }

    public CluedoMCTS() {
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
        if(state[GAME_STATE] == PLAYING)
            return false;
        return true;
    }

    @Override
    public int getCurrentPlayer() {
        return state[CURRENT_PLAYER];
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
        if(state[HAS_ACCUSED] != 0)
            return options;
        if(state[JUST_MOVED] == 0) {
            listMovePossibilities(options);
            listAccusePossibilities(options);
            if (inRoomWithSecretPassage(state[CURRENT_ROOM]))
                listSecretPassagePossibility(options);
        }
        if(state[CURRENT_ROOM] != 0 && state[HAS_SUGGESTED] == 0)
            listSuggestionPossibilities(options, state[CURRENT_ROOM]);
        return options;
    }

    private void listMovePossibilities(Options options) {
        for(int i = 1; i < 10; i++) {
            if(state[CURRENT_ROOM] != i) {
                options.put(new int[]{MOVE, i, state[CURRENT_ROLL]}, 1.0);
            }
        }
    }

    private void listAccusePossibilities(Options options) {
        for(int room = 1; room < 10; room++){
            for(int suspect = 1; suspect < 7; suspect++){
                for(int weapon = 1; weapon < 7; weapon++){
                    options.put(new int[]{ACCUSE, room, suspect, weapon}, 1.0);
                }
            }
        }
    }

    private boolean inRoomWithSecretPassage(int i) {
        if(i == STUDY || i == LOUNGE || i == KITCHEN || i == CONSERVATORY){
            return true;
        }
        else
            return false;
    }

    private void listSecretPassagePossibility(Options options) {
        options.put(new int[]{SECRET_PASSAGE, state[CURRENT_ROOM]}, 1.0);
    }

    private void listSuggestionPossibilities(Options options, int i) {
        for(int suspect = 1; suspect < 7; suspect++){
            for(int weapon = 1; weapon < 7; weapon++){
                options.put(new int[]{SUGGEST, state[CURRENT_ROOM], suspect, weapon}, 1.0);
            }
        }
    }

    @Override
    public Game copy() {
        CluedoBelief bel = null;
        if(belief != null)
            bel = (CluedoBelief)belief.copy();
        CluedoMCTS ret = new CluedoMCTS(this.getState(), (CluedoConfig) this.config.copy(), bel);
        return ret;
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
        int[] action = sampleNextAction();
        performAction(action, true);
    }

    public void setBoard(Board board) {
        this.board = new Board(board);
    }
}
