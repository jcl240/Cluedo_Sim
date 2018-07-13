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

    public CluedoMCTS(int[] state, CluedoConfig config, CluedoBelief belief) {
        this.state = state.clone();
        this.belief = belief;
        this.config = config;
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
        actionTaken = a.clone();
        switch(a[0]){
            case MOVE:
                board.movePlayer(a,playerIndex);
                state[JUST_MOVED] = 1;
                if(board.inRoom(playerIndex))
                    state[CURRENT_ROOM] = board.getRoom(playerIndex);
                else
                    getNextPlayer();
                break;
            case SECRET_PASSAGE:
                board.useSecretPassage(a,playerIndex);
                state[JUST_MOVED] = 1;
                state[CURRENT_ROOM] = board.getRoom(playerIndex);
                break;
            case SUGGEST:
                setFalsifyState(a);
                state[HAS_SUGGESTED] = 1;
                getNextPlayer();
                break;
            case FALSIFY:
                doFalsification(a);
                getNextPlayer();
                resetStateFromFalsification();
                break;
            case NO_FALSIFY:
                noCardToShow(a);
                getNextPlayer();
                if(getCurrentPlayer()==state[SUGGESTER_IDX]) {
                    getNextPlayer();
                    resetStateFromFalsification();
                }
                break;
            case ACCUSE:
                setAccused();
                boolean gameOver = checkAccusation(a);
                if(!gameOver) {
                    getNextPlayer();
                    resetStateFromAccusation();
                }
                break;
            case CHOOSE_DICE:
                state[CURRENT_ROLL] = a[1];
                break;
        }
        state[ENTROPY] = belief.getCurrentEntropy();
        state[TURN]++;
    }

    private void getNextPlayer() {
        int i;
        if(state[CURRENT_PLAYER] > 1 && getAccused() == 0)
            i = 1;
        state[CURRENT_PLAYER] = (state[CURRENT_PLAYER]+1)%4;
        state[CURRENT_ROLL] = -1;
        state[HAS_SUGGESTED] = 0;
        state[JUST_MOVED] = 0;
        state[CURRENT_ROOM] = board.getRoom(getCurrentPlayer());
        if(getAccused() == 1 && state[FALSIFYING] == 0 && !isTerminal())
            getNextPlayer();
    }

    private void resetStateFromAccusation() {
        int i = 0;
    }

    private void resetStateFromFalsification() {
        state[FALSIFYING] = 0;
        for(int i = 7; i <= 10; i++) {
            state[i] = 0;
        }
    }

    private boolean checkAccusation(int[] a) {
        double roomProb = belief.getCardProb(ROOM,a[1],0);
        double suspectProb = belief.getCardProb(SUSPECT,a[2],0);
        double weaponProb = belief.getCardProb(WEAPON,a[3],0);
        double jointProb = roomProb*suspectProb*weaponProb;
        double sample = Math.random();
        if(jointProb >= sample){
            state[WINNER] = getCurrentPlayer();
            state[GAME_STATE] = 0;
            return true;
        }
        return false;
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
        for(int i = 1; i < 4; i++){
            belief.setProbabilityZero(a[i],i, getCurrentPlayer()+1);
        }
    }

    private void doFalsification(int[] a) {
        for(int i = 1; i < 4; i++){
            double cardProb = belief.getCardProb(i,a[i],getCurrentPlayer()+1);
            double sample = Math.random();
            if(cardProb >= sample){
                belief.checkOffCard(a[i],i,getCurrentPlayer()+1);
            }
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
            listAccusePossibilities(options);
            if (inRoomWithSecretPassage(state[CURRENT_ROOM]))
                listSecretPassagePossibility(options);
        }
        if(state[CURRENT_ROOM] != 0 && state[HAS_SUGGESTED] == 0)
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
            double prob = belief.getCardProb(cardType, state[idx], state[SUGGESTER_IDX]+1);
            double sample = Math.random();
            if(prob >= sample){
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
        int i;
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
        this.node = new StandardNode(getState(), belief.getRepresentation(), isTerminal(), getCurrentPlayer());
        return node;
    }

    @Override
    public void gameTick() {
        int[] action = sampleNextAction();
        performAction(action, true);
    }

    public void setBoard(Board board) {
        this.board = new Board(board);
    }

    public void setTuples(LinkedList<Tuple<Player,Gamepiece>> playerPieceTuples) {
        board.setTuples(playerPieceTuples);
    }
}
