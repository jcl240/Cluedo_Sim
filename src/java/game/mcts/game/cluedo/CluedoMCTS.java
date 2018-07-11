package mcts.game.cluedo;

import agents.Action;
import main.Board;
import mcts.game.Game;
import mcts.game.catan.Actions;
import mcts.tree.node.StandardNode;
import mcts.tree.node.TreeNode;
import mcts.utils.Options;

import java.util.ArrayList;
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

    public CluedoMCTS(int[] state, CluedoConfig config, CluedoBelief belief) {
        this.state = state;
        this.belief = belief;
        this.config = config;
    }

    public CluedoMCTS(CluedoConfig gameConfig, CluedoBelief belief) {
        this.config = gameConfig;
        this.belief = belief;
    }

    public void setState(int[] newState){
        state = newState;
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
        int playerIndex = state[CURRENT_PLAYER]+1;
        switch(a[0]){
            case MOVE:
                board.movePlayer(a,playerIndex);
                state[JUST_MOVED] = 1;
                if(board.inRoom(playerIndex))
                    state[CURRENT_ROOM] = board.getRoom(playerIndex);
                break;
            case SECRET_PASSAGE:
                board.useSecretPassage(a,playerIndex);
                state[JUST_MOVED] = 1;
                state[CURRENT_ROOM] = board.getRoom(playerIndex);
                break;
            case SUGGEST:
                setFalsifyState(a);
                state[HAS_SUGGESTED] = 1;
                state[CURRENT_PLAYER] = (state[CURRENT_PLAYER]+1)%4;
                break;
            case FALSIFY:
                doFalsification(a);
                state[CURRENT_PLAYER] = (state[SUGGESTER_IDX]+1)%4;
                resetStateFromFalsification();
                break;
            case NO_FALSIFY:
                noCardToShow(a);
                state[CURRENT_PLAYER] = (state[CURRENT_PLAYER]+1)%4;
                if(state[CURRENT_PLAYER]==state[SUGGESTER_IDX]) {
                    state[CURRENT_PLAYER] = (state[SUGGESTER_IDX]+1)%4;
                    resetStateFromFalsification();
                }
                break;
            case ACCUSE:
                setAccused();
                checkAccusation(a);
                state[CURRENT_PLAYER] = (state[CURRENT_PLAYER]+1)%4;
                break;
            case CHOOSE_DICE:
                state[CURRENT_ROLL] = a[1];
                break;
        }

    }

    private void checkAccusation(int[] a) {
        double roomProb = belief.getCardProb(ROOM,a[1],0);
        double suspectProb = belief.getCardProb(SUSPECT,a[2],0);
        double weaponProb = belief.getCardProb(WEAPON,a[3],0);
        double jointProb = roomProb*suspectProb*weaponProb;
        double sample = Math.random();
        if(jointProb <= sample){
            state[GAME_STATE] = state[CURRENT_PLAYER];
        }

    }

    private void resetStateFromFalsification() {

    }

    private void setAccused() {
        state[state[CURRENT_PLAYER] + ACCUSED_OFFSET] = 1;
    }

    private int getAccused(){
        return state[state[CURRENT_PLAYER]+ACCUSED_OFFSET];
    }

    private void noCardToShow(int[] a) {
    }

    private void doFalsification(int[] a) {
        
    }

    private void setFalsifyState(int[] a) {
        /* 5: current player is falsifying card,
   6: current suggested room, 7: current suggested suspect,
   8: current suggested weapon, 9: suggester index,*/
        state[FALSIFYING] = 1;
        state[SUGGESTED_ROOM] = a[1];
        state[SUGGESTED_SUSPECT] = a[2];
        state[SUGGESTED_WEAPON] = a[3];
        state[SUGGESTER_IDX] = state[CURRENT_PLAYER];
    }

    @Override
    public Options listPossiblities(boolean sample) {
        Options options = new Options();
        if(state[FALSIFY] == 1){
            listFalsifyPossibilities(options);
            return options;
        }
        if(getAccused() == 1)
            return options;
        if(state[CURRENT_ROLL] == -1) {
            listDiceResultPossibilities(options);
            return options;
        }
        if(state[JUST_MOVED] == 0) {
            listMovePossibilities(options);
            listAccusePossibilities(options);
            if (inRoomWithSecretPassage(state[CURRENT_ROOM]))
                listSecretPassagePossibility(options);
        }
        if(state[CURRENT_ROOM] == 1 && state[HAS_SUGGESTED] == 0)
            listSuggestionPossibilities(options, state[CURRENT_ROOM]);
        return options;
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
        for(int idx = 8; idx < 11; idx++){
            int cardType = cardTypes[i];
            double prob = belief.getCardProb(cardType, state[idx], state[11]);
            double sample = Math.random();
            if(prob <= sample){
             options.put(Actions.newAction(FALSIFY,state[idx],cardType), 1.0);
            }
            i++;
        }
        if(options.isEmpty()){
            options.put(Actions.newAction(NO_FALSIFY),1.0);
        }
    }

    private void listMovePossibilities(Options options) {
        for(int i = 1; i < 10; i++) {
            if(state[CURRENT_ROOM] != i) {
                options.put(Actions.newAction(MOVE, i, state[CURRENT_ROLL]), 1.0);
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
        CluedoMCTS ret = new CluedoMCTS(this.getState(), (CluedoConfig) this.config.copy(), bel);
        ret.setBoard(board);
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
        this.board = new Board(board);
    }
}
