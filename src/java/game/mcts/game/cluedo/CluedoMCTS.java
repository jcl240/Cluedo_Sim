package mcts.game.cluedo;

import agents.Action;
import agents.Player;
import main.Board;
import main.Card;
import main.Gamepiece;
import main.Tuple;
import mcts.game.Game;
import mcts.game.catan.Actions;
import mcts.tree.node.StandardNode;
import mcts.tree.node.TreeNode;
import mcts.utils.Options;

import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedList;
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

    public CluedoMCTS(int[] state, CluedoConfig config, CluedoBelief belief, Board board) {
        this.state = state.clone();
        this.belief = belief;
        this.config = config;
        setBoard(board);
        state[ENTROPY] = belief.getCurrentEntropy();
    }

    public CluedoMCTS(CluedoConfig gameConfig, CluedoBelief belief) {
        this.config = gameConfig;
        this.belief = belief;
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
                    state[getCurrentPlayer()+MOVEMENT_OFFSET] = 0;
                }
                else
                    getNextPlayer();
                break;
            case SECRET_PASSAGE:
                updatePlayerLocation();
                state[JUST_MOVED] = 1;
                state[getCurrentPlayer()+MOVEMENT_OFFSET] = 0;
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
        state[TURN]++;
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
        for(int i = SUGGESTED_ROOM; i <= SUGGESTED_WEAPON; i++){
            belief.setProbabilityZero(a[i],i, getCurrentPlayer()+1);
        }
    }

    private void doFalsification(int[] a) {
        belief.checkOffCard(a[1],a[2],getCurrentPlayer()+1);
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
        if(state[CHECKING_WIN_POSSIBILITY] == 1) {
            listWinGamePossibility(options);
            return options;
        }
        if(state[FALSIFYING] == 1){
            listFalsifyPossibilities(options);
            return options;
        }
        if(state[CURRENT_ROLL] == -1) {
            listDiceResultPossibilities(options);
            return options;
        }
        if(state[JUST_MOVED] == 0) {
            listMovePossibilities(options);
            if(state[ENTROPY] < 10)
                listAccusePossibilities(options);
            if (inRoomWithSecretPassage(state[CURRENT_ROOM]))
                listSecretPassagePossibility(options);
        }
        if(state[CURRENT_ROOM] != 0 && state[HAS_SUGGESTED] == 0)
            listSuggestionPossibilities(options, state[CURRENT_ROOM]);
        return options;
    }

    private void listWinGamePossibility(Options options) {
        double roomProb = belief.getCardProb(ROOM,state[ACCUSED_ROOM],0);
        double suspectProb = belief.getCardProb(SUSPECT,state[ACCUSED_SUSPECT],0);
        double weaponProb = belief.getCardProb(WEAPON,state[ACCUSED_WEAPON],0);
        double jointProb = roomProb*suspectProb*weaponProb;
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
        double jointProb = 0.0;
        for(int idx = 8; idx < 11; idx++){
            int cardType = cardTypes[i];
            double prob = belief.getCardProb(cardType, state[idx], state[SUGGESTER_IDX]+1);
            //add or multiply?????
            jointProb += prob;
            options.put(Actions.newAction(FALSIFY,state[idx],cardType), prob);
            i++;
        }
        options.put(Actions.newAction(NO_FALSIFY),1-jointProb);
    }

    private void listMovePossibilities(Options options) {
        int movementGoal = state[getCurrentPlayer()+MOVEMENT_OFFSET];
        if(movementGoal != 0)
            options.put(Actions.newAction(MOVE, movementGoal, state[CURRENT_ROLL]), 1.0);
        else {
            for (int i = 1; i < 10; i++) {
                if (state[CURRENT_ROOM] != i) {
                    options.put(Actions.newAction(MOVE, i, state[CURRENT_ROLL]), 1.0);
                }
            }
        }
    }

    private void listAccusePossibilities(Options options) {
        for(int room = 1; room < 10; room++){
            for(int suspect = 1; suspect < 7; suspect++){
                for(int weapon = 1; weapon < 7; weapon++){
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

    private void listSuggestionPossibilities(Options options, int i) {
        for(int suspect = 1; suspect < 7; suspect++){
            for(int weapon = 1; weapon < 7; weapon++){
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
        return options.getOptions().get(rnd.nextInt(options.size()));
    }

    @Override
    public int sampleNextActionIndex() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Options options = listPossiblities(true);
        return rnd.nextInt(options.size());
    }

    @Override
    public TreeNode generateNode() {
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
