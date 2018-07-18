package mcts.game.cluedo;

import main.Board;
import mcts.tree.node.StandardNode;
import mcts.utils.Options;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class HeuristicMCTS implements GameStateConstants {

    private int playerIdx;
    private int[] state;

    private CluedoBelief belief;
    private Board board;
    private CluedoConfig config;
    private LinkedList<Integer> actionTypes;

    public HeuristicMCTS(int[] state, CluedoConfig config, CluedoBelief belief, Board board, int playerIdx) {
        this.state = state;
        this.belief = (CluedoBelief)belief;
        this.config = config;
        this.playerIdx = playerIdx;
        this.board = board;
    }


    public Options listPossiblities(boolean sample) {
        Options options = new Options();
        actionTypes = new LinkedList<>();
        if(state[CHECKING_WIN_POSSIBILITY] == 1) {
            listWinGamePossibility(options);
            actionTypes.add(CONTINUE_GAME);
            actionTypes.add(GAME_WON);
            return options;
        }
        if(state[FALSIFYING] == 1){
            listFalsifyPossibilities(options);
            return options;
        }
        if(state[CURRENT_ROLL] == -1) {
            listDiceResultPossibilities(options);
            actionTypes.add(CHOOSE_DICE);
            return options;
        }
        if(state[JUST_MOVED] == 0) {
            listMovePossibilities(options);
            if(state[ENTROPY] == 0) {
                listAccusePossibilities(options);
                actionTypes.add(ACCUSE);
            }
            if (inRoomWithSecretPassage(state[CURRENT_ROOM])) {
                listSecretPassagePossibility(options);
                actionTypes.add(SECRET_PASSAGE);
            }
        }
        if(state[CURRENT_ROOM] != 0 && state[HAS_SUGGESTED] == 0) {
            listSuggestionPossibilities(options);
            actionTypes.add(SUGGEST);
        }
        return options;
    }

    private void listFalsifyPossibilities(Options options) {

    }

    private void listDiceResultPossibilities(Options options) {

    }

    private void listMovePossibilities(Options options) {

    }

    private void listAccusePossibilities(Options options) {

    }

    private boolean inRoomWithSecretPassage(int i) {

        return false;
    }

    private void listSecretPassagePossibility(Options options) {

    }

    private void listSuggestionPossibilities(Options options) {

    }

    private void listWinGamePossibility(Options options) {

    }


    public int[] sampleNextAction() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Options options = listPossiblities(true);
        return sampleActionByType(options,rnd);
    }


    private int[] sampleActionByType(Options options, ThreadLocalRandom rnd)  {
        Options optionsCopy = options.getCopy();
        if(actionTypes.size() == 1)
            return optionsCopy.getOptions().get(rnd.nextInt(optionsCopy.size()));
        else {
            int actionType = actionTypes.get(rnd.nextInt(actionTypes.size()));
            optionsCopy.removeAllExceptType(actionType);
            return optionsCopy.getOptions().get(rnd.nextInt(optionsCopy.size()));
        }
    }


    public int sampleNextActionIndex() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Options options = listPossiblities(true);
        int[] action = sampleActionByType(options,rnd);
        return options.indexOfAction(action);
    }
}
