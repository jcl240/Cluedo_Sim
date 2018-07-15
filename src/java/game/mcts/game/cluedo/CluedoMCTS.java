package mcts.game.cluedo;

import agents.Action;
import agents.Player;
import main.Board;
import main.Card;
import main.Gamepiece;
import main.Tuple;
import mcts.game.Game;
import mcts.game.catan.Actions;
import mcts.tree.node.ChanceNode;
import mcts.tree.node.StandardNode;
import mcts.tree.node.TreeNode;
import mcts.utils.Options;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class CluedoMCTS implements Game, GameStateConstants {

    /* 0: Playing or winner's index, 1: current player's index,
        2: current player justMoved, 3: current player's room,
        4: current roll, 5: current player is falsifying card,
       6: current suggested room, 7: current suggested suspect,
       8: current suggested weapon, 9: suggester index,
       10: player one accused, 11: player two accused,
       12: player three accused, 13: player four accused
    */
    private int[] state;

    private CluedoBelief belief;
    public static long breadth = 0;
    public static long depth = 0;
    private Board board;
    private CluedoConfig config;
    private int[] actionTaken;
    private StandardNode node;
    private int myIdx;
    private LinkedList<Integer> actionTypes;

    public CluedoMCTS(int[] state, CluedoConfig config, CluedoBelief belief, Board board) {
        this.state = state.clone();
        this.belief = (CluedoBelief)belief.copy();
        this.config = config;
        setBoard(board);
        state[ENTROPY] = belief.getCurrentEntropy();
    }

    public CluedoMCTS(CluedoConfig gameConfig, CluedoBelief belief) {
        this.config = gameConfig;
        this.belief = (CluedoBelief)belief.copy();
    }

    public void setState(int[] newState){
        state = newState.clone();
        state[ENTROPY] = belief.getCurrentEntropy();
    }

    @Override
    public int[] getState() {
        return this.state.clone();
    }

    @Override
    public int getWinner() {
        return state[WINNER];
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
        int playerIndex = getCurrentPlayer()+1;
        switch(a[0]){
            case MOVE:
                board.movePlayer(a,playerIndex);
                break;
            case SECRET_PASSAGE:
                board.useSecretPassage(a,playerIndex);
                break;
            case SUGGEST:
                break;
            case FALSIFY:
                doFalsification(a);
                break;
            case NO_FALSIFY:
                noCardToShow(a);
                break;
            case ACCUSE:
                break;
            case CHOOSE_DICE:
                break;
            case CONTINUE_GAME:
                break;
            case GAME_WON:
                break;
        }
        stateTransition(a);
    }

    private void stateTransition(int[] a) {
        int playerIndex = getCurrentPlayer()+1;
        switch(a[0]){
            case MOVE:
                updatePlayerLocation();
                if(board.inRoom(playerIndex)) {
                    state[CURRENT_ROOM] = board.getRoom(playerIndex);
                }
                else
                    getNextPlayer();
                break;
            case SECRET_PASSAGE:
                updatePlayerLocation();
                state[JUST_MOVED] = 1;
                state[HAS_SUGGESTED] = 0;
                state[CURRENT_ROOM] = board.getRoom(playerIndex);
                break;
            case SUGGEST:
                setFalsifyState(a);
                state[HAS_SUGGESTED] = 1;
                getNextPlayer();
                break;
            case FALSIFY:
                state[ENTROPY] = belief.getCurrentEntropy();
                getNextPlayer();
                resetStateFromFalsification();
                break;
            case NO_FALSIFY:
                state[ENTROPY] = belief.getCurrentEntropy();
                getNextPlayer();
                if(getCurrentPlayer()==state[SUGGESTER_IDX]) {
                    getNextPlayer();
                    resetStateFromFalsification();
                }
                break;
            case ACCUSE:
                setAccused();
                state[ACCUSED_ROOM] = a[1];
                state[ACCUSED_SUSPECT] = a[2];
                state[ACCUSED_WEAPON] = a[3];
                state[CHECKING_WIN_POSSIBILITY] = 1;
                break;
            case CHOOSE_DICE:
                state[CURRENT_ROLL] = a[1];
                break;
            case CONTINUE_GAME:
                state[CHECKING_WIN_POSSIBILITY] = 0;
                state[ACCUSED_ROOM] = 0;
                state[ACCUSED_SUSPECT] = 0;
                state[ACCUSED_WEAPON] = 0;
                getNextPlayer();
                break;
            case GAME_WON:
                state[CHECKING_WIN_POSSIBILITY] = 0;
                state[WINNER] = getCurrentPlayer();
                state[GAME_STATE] = 0;
                break;
        }
        state[ENTROPY] = belief.getCurrentEntropy();
    }

    private void updatePlayerLocation() {
        int[][] playerLocations = board.getPlayerLocations();
        switch(getCurrentPlayer()){
            case 0:
                state[PLAYER_ONE_X] = playerLocations[0][0];
                state[PLAYER_ONE_Y] = playerLocations[0][1];
                break;
            case 1:
                state[PLAYER_TWO_X] = playerLocations[1][0];
                state[PLAYER_TWO_Y] = playerLocations[1][1];
                break;
            case 2:
                state[PLAYER_THREE_X] = playerLocations[2][0];
                state[PLAYER_THREE_Y] = playerLocations[2][1];
                break;
            case 3:
                state[PLAYER_FOUR_X] = playerLocations[3][0];
                state[PLAYER_FOUR_Y] = playerLocations[3][1];
                break;
        }
    }

    private void getNextPlayer() {
        state[CURRENT_PLAYER] = (state[CURRENT_PLAYER]+1)%4;
        state[CURRENT_ROLL] = -1;
        state[HAS_SUGGESTED] = 0;
        state[JUST_MOVED] = 0;
        state[CURRENT_ROOM] = board.getRoom(getCurrentPlayer()+1);
        if(getAccused() == 1 && state[FALSIFYING] == 0 && !isTerminal())
            getNextPlayer();
    }

    private void resetStateFromFalsification() {
        state[FALSIFYING] = 0;
        for(int i = SUGGESTED_ROOM; i <= SUGGESTER_IDX; i++) {
            state[i] = 0;
        }
    }


    private void setAccused() {
        state[getCurrentPlayer() + ACCUSED_OFFSET] = 1;
        checkAccusedCount();
    }

    private void checkAccusedCount() {
        LinkedList<Integer> accused = new LinkedList<>();
        for(int i = PLAYER_ONE_ACCUSED; i <= PLAYER_FOUR_ACCUSED; i++){
            if(state[i] == 1)
                accused.add(i-ACCUSED_OFFSET);
        }
        if(accused.size() == 3){
            for(int i = 1; i <= 4; i++){
                if(!accused.contains(i)) {
                    state[WINNER] = i - 1;
                    state[GAME_STATE] = 0;
                }
            }
        }
    }

    private int getAccused(){
        return state[getCurrentPlayer()+ACCUSED_OFFSET];
    }

    private void noCardToShow(int[] a) {
        if(getCurrentPlayer() != 0) {
            for (int i = SUGGESTED_ROOM; i <= SUGGESTED_WEAPON; i++) {
                double prob = belief.getCardProb( state[i], i-6,getCurrentPlayer() + 1);
                if(prob == 1)
                    prob = prob;
                belief.setProbabilityZero(state[i], i-6, getCurrentPlayer() + 1);
            }
        }
    }

    private void doFalsification(int[] a) {
        if(state[SUGGESTER_IDX] == 0) {
            belief.checkOffCard(a[1], a[2], getCurrentPlayer() + 1);
        }
        else {
            int[] suggestion = new int[]{state[SUGGESTED_ROOM], state[SUGGESTED_SUSPECT], state[SUGGESTED_WEAPON]};
            belief.updateProbabilities(suggestion, getCurrentPlayer()+1);
        }
    }

    private void setFalsifyState(int[] a) {
        /* 5: current player is falsifying card,
   6: current suggested room, 7: current suggested suspect,
   8: current suggested weapon, 9: suggester index,*/
        state[FALSIFYING] = 1;
        state[SUGGESTED_ROOM] = a[1];
        state[SUGGESTED_SUSPECT] = a[2];
        state[SUGGESTED_WEAPON] = a[3];
        state[SUGGESTER_IDX] = getCurrentPlayer();
    }

    @Override
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
            actionTypes.add(MOVE);
            if(state[ENTROPY] < 2) {
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

    public ArrayList<Integer> listActionTypes(){
        ArrayList<Integer> actionTypes = new ArrayList<>();

        return actionTypes;
    }

    private void listWinGamePossibility(Options options) {
        double roomProb = belief.getCardProb(state[ACCUSED_ROOM],ROOM,0);
        double suspectProb = belief.getCardProb(state[ACCUSED_SUSPECT],SUSPECT,0);
        double weaponProb = belief.getCardProb(state[ACCUSED_WEAPON],WEAPON,0);
        double jointProb = belief.getJointProbabilityInEnvelope(roomProb,suspectProb,weaponProb);
        options.put(Actions.newAction(GAME_WON),jointProb);
        options.put(Actions.newAction(CONTINUE_GAME),1-jointProb);
    }

    private void listDiceResultPossibilities(Options options){
        for(int i = 1; i <= 6; i++){
            options.put(Actions.newAction(CHOOSE_DICE, i),1.0);
        }
    }

    private void listFalsifyPossibilities(Options options) {
        //1 room, 2 suspect, 3 weapon
        int[] cardTypes = new int[]{ROOM,SUSPECT,WEAPON};
        int i = 0;
        double jointProb = 1;
        for(int idx = SUGGESTED_ROOM; idx <= SUGGESTED_WEAPON; idx++){
            int cardType = cardTypes[i];
            double prob = belief.getCardProb( state[idx],cardType, getCurrentPlayer()+1);
            jointProb *= (1-prob);
            options.put(Actions.newAction(FALSIFY,state[idx],cardType), prob);
            i++;
        }
        actionTypes.add(FALSIFY);
        if(jointProb > 0) {
            options.put(Actions.newAction(NO_FALSIFY), jointProb);

            actionTypes.add(NO_FALSIFY);
        }

    }

    private void listMovePossibilities(Options options) {
        for (int i = 0; i < 9; i++) {
            if (state[CURRENT_ROOM] != i) {
                options.put(Actions.newAction(MOVE, i, state[CURRENT_ROLL]), 1.0);
            }
        }
    }

    private void listAccusePossibilities(Options options) {
        for(int room = 0; room < 9; room++){
            for(int suspect = 0; suspect < 6; suspect++){
                for(int weapon = 0; weapon < 6; weapon++){
                    options.put(Actions.newAction(ACCUSE, room, suspect, weapon), 1.0);
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
        options.put(Actions.newAction(SECRET_PASSAGE, state[CURRENT_ROOM]), 1.0);
    }

    private void listSuggestionPossibilities(Options options) {
        for(int suspect = 0; suspect < 6; suspect++){
            for(int weapon = 0; weapon < 6; weapon++){
                options.put(Actions.newAction(SUGGEST, state[CURRENT_ROOM], suspect, weapon), 1.0);
            }
        }
    }

    @Override
    public Game copy() {
        CluedoBelief bel = null;
        if(belief != null)
            bel = (CluedoBelief)belief.copy();
        CluedoMCTS ret = new CluedoMCTS(this.getState(), (CluedoConfig) this.config.copy(), bel, board);
        return ret;
    }

    @Override
    public int[] sampleNextAction() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Options options = listPossiblities(true);
        return sampleActionByType(options,rnd);
    }

    private int[] sampleActionByType(Options options, ThreadLocalRandom rnd) {
        if(actionTypes.size() == 1)
            return options.getOptions().get(rnd.nextInt(options.size()));
        else {
            int actionType = actionTypes.get(rnd.nextInt(actionTypes.size()));
            int l = 0;
            options.removeAllExceptType(actionType);
            return options.getOptions().get(rnd.nextInt(options.size()));
        }
    }

    @Override
    public int sampleNextActionIndex() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Options options = listPossiblities(true);
        int[] action = sampleActionByType(options,rnd);
        return options.indexOfAction(action);
    }

    @Override
    public TreeNode generateNode() {
        if(state[CHECKING_WIN_POSSIBILITY] == 1 || (state[FALSIFYING] == 0 && state[CURRENT_ROLL] == -1 ))
            return new ChanceNode(getState(), belief.getRepresentation(), isTerminal(), getCurrentPlayer());
        else
            return new StandardNode(getState(), belief.getRepresentation(), isTerminal(), getCurrentPlayer());
    }

    @Override
    public void gameTick() {
        int[] action = sampleNextAction();
        performAction(action, true);
    }

    public void setBoard(Board board) {
        this.board = new Board(board.getPlayerLocations());
        if(state != null) {
            board.setTuples(new int[]{state[PLAYER_ONE_X], state[PLAYER_ONE_Y],
                    state[PLAYER_TWO_X], state[PLAYER_TWO_Y],
                    state[PLAYER_THREE_X], state[PLAYER_THREE_Y],
                    state[PLAYER_FOUR_X], state[PLAYER_FOUR_Y],});
        }
    }

}
