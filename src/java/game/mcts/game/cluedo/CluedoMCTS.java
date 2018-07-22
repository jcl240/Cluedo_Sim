package mcts.game.cluedo;

import agents.Action;
import agents.HeuristicAgent;
import agents.HeuristicNotebook;
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
    private Board board;
    private CluedoConfig config;
    private int myIdx;
    private LinkedList<Integer> actionTypes;
    private int[] actionTaken;

    public CluedoMCTS(int[] state, CluedoConfig config, CluedoBelief belief, Board board, int playerIdx) {
        this.state = state.clone();
        this.belief = (CluedoBelief)belief.copy();
        this.config = config;
        this.myIdx = playerIdx;
        setBoard(board, false);
        state[ENTROPY] = belief.getCurrentEntropy();
    }

    public CluedoMCTS(CluedoConfig gameConfig, CluedoBelief belief, Board board, int playerIdx) {
        this.config = gameConfig;
        this.belief = (CluedoBelief)belief.copy();
        this.myIdx = playerIdx;
        setBoard(board, false);
        determinizeEnvelope();
    }

    private void determinizeEnvelope() {
        int[] envelope = belief.getDeterminizedEnvelope();
        Player[] players = board.getPlayers();
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 3; j++) {
                ((HeuristicAgent)players[i]).getNotebook().setProbabilityZero(envelope[j], j+1, i+1, envelope);
            }
        }
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
        return state[GAME_STATE] != PLAYING;
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
                //IF FALSIFY MUST MAKE OWN PROBABILITY FOR FALSIFIED CARD 1
                //ALSO MAKE SURE IF I KNOW I HAVE 4 CARDS WITH PROB 1 THEN ALL OTHERS HAVE PROB 0
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
            case DO_NOTHING:
                break;
        }
        stateTransition(a);
    }

    private void stateTransition(int[] a) {
        int playerIndex = getCurrentPlayer()+1;
        HeuristicAgent agent = ((HeuristicAgent)board.getPlayers()[getCurrentPlayer()]);
        switch(a[0]){
            case MOVE:
                updatePlayerLocation();
                state[JUST_MOVED] = 1;
                state[CURRENT_ROLL] = 0;
                if(board.inRoom(playerIndex)) {
                    int preRoom = state[CURRENT_ROOM];
                    state[CURRENT_ROOM] = board.getRoom(playerIndex);
                    agent.movementGoal = null;
                    if(preRoom == state[CURRENT_ROOM]) {
                        getNextPlayer();
                    }
                    else {
                        state[HAS_SUGGESTED] = 0;
                    }
                }
                else
                    getNextPlayer();
                break;
            case SECRET_PASSAGE:
                updatePlayerLocation();
                agent.movementGoal = null;
                state[JUST_MOVED] = 1;
                state[HAS_SUGGESTED] = 0;
                state[CURRENT_ROLL] = 0;
                state[CURRENT_ROOM] = board.getRoom(playerIndex);
                break;
            case SUGGEST:
                setFalsifyState(a);
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
            case DO_NOTHING:
                getNextPlayer();
        }
        state[ENTROPY] = belief.getCurrentEntropy();
    }

    private void updatePlayerLocation() {
        int[][] playerLocations = board.getPlayerLocations();
        state[PLAYER_ONE_X] = playerLocations[0][0];
        state[PLAYER_ONE_Y] = playerLocations[0][1];

        state[PLAYER_TWO_X] = playerLocations[1][0];
        state[PLAYER_TWO_Y] = playerLocations[1][1];

        state[PLAYER_THREE_X] = playerLocations[2][0];
        state[PLAYER_THREE_Y] = playerLocations[2][1];

        state[PLAYER_FOUR_X] = playerLocations[3][0];
        state[PLAYER_FOUR_Y] = playerLocations[3][1];
    }

    private void getNextPlayer() {
        state[CURRENT_PLAYER] = (state[CURRENT_PLAYER]+1)%4;
        state[CURRENT_ROLL] = 0;
        state[HAS_SUGGESTED] = 0;
        state[JUST_MOVED] = 0;
        state[CURRENT_ROOM] = board.getRoom(getCurrentPlayer()+1);
        if(state[CURRENT_ROOM] != -1)
            state[HAS_SUGGESTED] = 1;
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
        Player[] players = board.getPlayers();
        int[] envelope = belief.getDeterminizedEnvelope();
        for(int idx = 0; idx < 4; idx++){
            if (idx == myIdx) {
                for (int i = SUGGESTED_ROOM; i <= SUGGESTED_WEAPON; i++) {
                    belief.setProbabilityZero(state[i], i - 6, getCurrentPlayer() + 1);

                    //IF THIS CAUSED PLAYER TO KNOW CARD, MUST MAKE SURE OTHER PLAYER W/ PROB 1 KNOWS THIS
                    if (belief.know(state[i], i - 6)) {
                        int playerWithCard = belief.getPlayerIdxWithCard(state[i], i - 6);
                        notifyPlayerHasCard(state[i], i - 6, playerWithCard);
                    }

                }
            }
            else{
                //set prob zero for other players
                HeuristicNotebook notebook = ((HeuristicAgent) players[idx]).getNotebook();
                for (int i = SUGGESTED_ROOM; i <= SUGGESTED_WEAPON; i++) {
                    notebook.setProbabilityZero(state[i], i - 6, getCurrentPlayer() + 1,envelope);

                    //IF THIS CAUSED PLAYER TO KNOW CARD, MUST MAKE SURE OTHER PLAYER W/ PROB 1 KNOWS THIS
                    if (notebook.know(state[i], i - 6)) {
                        int playerWithCard = notebook.getPlayerIdxWithCard(state[i], i - 6);
                        notifyPlayerHasCard(state[i], i - 6, playerWithCard);
                    }

                }
            }
        }
    }

    private void notifyPlayerHasCard(int card, int cardType, int playerWithCard) {
        int[] envelope = belief.getDeterminizedEnvelope();
        if(playerWithCard == -1) {
            return;
        }
        else if(playerWithCard-1 != myIdx) {
            if(playerWithCard != 0) {
                HeuristicAgent agent = ((HeuristicAgent) board.getPlayers()[playerWithCard - 1]);
                agent.getNotebook().checkOffCard(card, cardType, playerWithCard,envelope);
            }
            for(int i = 0; i <4; i++){
                if(i!=myIdx && i!=playerWithCard-1){
                    HeuristicAgent other = ((HeuristicAgent) board.getPlayers()[i]);
                    other.getNotebook().setProbabilityZero(card,cardType,i+1,envelope);
                }
            }
        }
    }

    private void doFalsification(int[] a) {
        Player[] players = board.getPlayers();
        int[] envelope = belief.getDeterminizedEnvelope();

        for(int idx = 0; idx < 4; idx++) {
            int[] suggestion = new int[]{state[SUGGESTED_ROOM], state[SUGGESTED_SUSPECT], state[SUGGESTED_WEAPON]};

            if(idx != state[SUGGESTER_IDX] && idx != getCurrentPlayer()) {
                if (idx == myIdx) {
                    belief.updateProbabilities(suggestion, getCurrentPlayer() + 1);
                }
                else {
                    HeuristicNotebook notebook = ((HeuristicAgent) players[idx]).getNotebook();
                    //update probabilities for other players
                    notebook.setProbabilityZero(a[1], a[2], idx + 1, envelope);
                    //IF THIS CAUSED PLAYER TO KNOW CARD, MUST MAKE SURE OTHER PLAYER W/ PROB 1 KNOWS THIS
                    if (notebook.know(a[1], a[2])) {
                        int playerWithCard = notebook.getPlayerIdxWithCard(a[1], a[2]);
                        notifyPlayerHasCard(a[1], a[2], playerWithCard);
                    }
                    notebook.updateProbabilities(suggestion, getCurrentPlayer() + 1, envelope);
                }
            }

            else{
                if (idx == myIdx) {
                    belief.checkOffCard(a[1], a[2], getCurrentPlayer() + 1);
                } else {
                    //check off card for suggester
                    ((HeuristicAgent) players[idx]).getNotebook().checkOffCard(a[1], a[2], getCurrentPlayer() + 1, envelope);
                }
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
        actionTypes = new LinkedList<>();
        if(state[CHECKING_WIN_POSSIBILITY] == 1) {
            listWinGamePossibility(options);
            return options;
        }
        if(state[FALSIFYING] == 1){
            listFalsifyPossibilities(options);
            return options;
        }
        if(state[CURRENT_ROLL] == 0 && state[JUST_MOVED] == 0) {
            listDiceResultPossibilities(options);
            actionTypes.add(CHOOSE_DICE);
            return options;
        }
        if(state[JUST_MOVED] == 0) {
            listMovePossibilities(options);
            if(belief.knowEnvelope()) {
                listAccusePossibilities(options);
                actionTypes.add(ACCUSE);
            }
            if (inRoomWithSecretPassage(state[CURRENT_ROOM])) {
                listSecretPassagePossibility(options);
                actionTypes.add(SECRET_PASSAGE);
            }
        }
        if(state[CURRENT_ROOM] != -1 && state[HAS_SUGGESTED] == 0) {
            listSuggestionPossibilities(options);
            actionTypes.add(SUGGEST);
        }
        return options;
    }

    private void listWinGamePossibility(Options options) {
        double roomProb = belief.getCardProb(state[ACCUSED_ROOM],ROOM,0);
        double suspectProb = belief.getCardProb(state[ACCUSED_SUSPECT],SUSPECT,0);
        double weaponProb = belief.getCardProb(state[ACCUSED_WEAPON],WEAPON,0);
        double jointProb = belief.getJointProbabilityInEnvelope(roomProb,suspectProb,weaponProb);
        if(jointProb>0) {
            options.put(Actions.newAction(GAME_WON), jointProb);
            if(!actionTypes.contains(GAME_WON)){
                actionTypes.add(GAME_WON);
            }
        }
        if(1-jointProb > 0) {
            options.put(Actions.newAction(CONTINUE_GAME), 1 - jointProb);
            if(!actionTypes.contains(CONTINUE_GAME)){
                actionTypes.add(CONTINUE_GAME);
            }
        }
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
            double prob;
            int[] envelopeContent = belief.getDeterminizedEnvelope();
            if(state[idx] == envelopeContent[i]){
                i++;
                continue;
            }
            if(myIdx == getCurrentPlayer()) {
                prob = belief.getCardProb(state[idx], cardType, getCurrentPlayer() + 1);
                if(belief.knownHandSize(myIdx+1) >= 4 && prob != 1){
                    i++;
                    continue;
                }
            }
            else{
                HeuristicNotebook notebook = ((HeuristicAgent)board.getPlayers()[getCurrentPlayer()]).getNotebook();
                prob = notebook.getCardProb(state[idx], cardType, getCurrentPlayer()+1);
                if(notebook.knownHandSize(getCurrentPlayer()+1) >= 4 && prob != 1){
                    i++;
                    continue;
                }
            }
            jointProb *= (1-prob);
            if(prob!=0) {
                options.put(Actions.newAction(FALSIFY, state[idx], cardType), prob);
                if(!actionTypes.contains(FALSIFY)){
                    actionTypes.add(FALSIFY);
                }
            }
            i++;
        }
        if(jointProb > 0) {
            options.put(Actions.newAction(NO_FALSIFY), jointProb);
            if(!actionTypes.contains(NO_FALSIFY)) {
                actionTypes.add(NO_FALSIFY);
            }
        }

    }

    private void listMovePossibilities(Options options) {
        int j = 0;
        for (int i = 0; i < 9; i++) {
            if (state[CURRENT_ROOM] != i) {
                options.put(Actions.newAction(MOVE, i, state[CURRENT_ROLL]), 1.0);
                j++;
                actionTypes.add(MOVE);
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
                if(!belief.isFaceUp(suspect,SUSPECT) && !belief.isFaceUp(weapon,WEAPON))
                    options.put(Actions.newAction(SUGGEST, state[CURRENT_ROOM], suspect, weapon), 1.0);
            }
        }
    }

    @Override
    public Game copy() {
        CluedoBelief bel = null;
        if(belief != null)
            bel = (CluedoBelief)belief.copy();
        CluedoMCTS ret = new CluedoMCTS(this.getState(), (CluedoConfig) this.config.copy(), bel, board, myIdx);
        return ret;
    }

    @Override
    public int[] sampleNextAction() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Options options;
        if(getCurrentPlayer() != myIdx && canUseHeuristicDecision()){
            HeuristicMCTS decisionMaker = new HeuristicMCTS((HeuristicAgent)board.getPlayers()[getCurrentPlayer()], state);
            options = decisionMaker.listPossiblities(true);
            actionTypes = decisionMaker.actionTypes;
        }
        else {
            options = listPossiblities(true);
        }
        return sampleActionByType(options,rnd);
    }

    private boolean canUseHeuristicDecision() {
        if(state[CHECKING_WIN_POSSIBILITY] == 0){
            if(state[FALSIFYING] == 0){
                if(state[CURRENT_ROLL] != 0) {
                    if (state[JUST_MOVED] == 0) {
                        return true;
                    }
                }
                else if(state[HAS_SUGGESTED] == 0 && board.inRoom(getCurrentPlayer()+1)){
                    return true;
                }
            }
        }
        return false;
    }

    private int[] sampleActionByType(Options options, ThreadLocalRandom rnd)  {
        Options optionsCopy = options.getCopy();
        if(actionTypes.size() == 1)
            return optionsCopy.getOptions().get(rnd.nextInt(optionsCopy.size()));
        else {
            int actionType = actionTypes.get(rnd.nextInt(actionTypes.size()));
            optionsCopy.removeAllExceptType(actionType);
            if(optionsCopy.getOptions().size() == 0)
                options=options;
            return optionsCopy.getOptions().get(rnd.nextInt(optionsCopy.size()));
        }
    }

    @Override
    public int sampleNextActionIndex() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Options options;
        if(getCurrentPlayer() != myIdx && canUseHeuristicDecision()){
            HeuristicMCTS decisionMaker = new HeuristicMCTS((HeuristicAgent)board.getPlayers()[getCurrentPlayer()+1], state);
            options = decisionMaker.listPossiblities(true);
            actionTypes = decisionMaker.actionTypes;
        }
        else {
            options = listPossiblities(true);
        }
        int[] action = sampleActionByType(options,rnd);
        return options.indexOfAction(action);
    }

    @Override
    public TreeNode generateNode() {
        if(state[CHECKING_WIN_POSSIBILITY] == 1 || (state[FALSIFYING] == 0 && state[CURRENT_ROLL] == 0 && state[JUST_MOVED] != 1))
            return new ChanceNode(getState(), belief.getRepresentation(), isTerminal(), getCurrentPlayer());
        else
            return new StandardNode(getState(), belief.getRepresentation(), isTerminal(), getCurrentPlayer());
    }

    @Override
    public void gameTick() {
        int[] action = sampleNextAction();
        actionTaken = action.clone();
        performAction(action, true);
        if(action[0] == FALSIFY || action[0] == NO_FALSIFY) {
            checkForEnvelope();
        }
        //checkEnvelopeProbs(actionTaken);
    }

    private void checkEnvelopeProbs(int[] actionTaken) {
        Player[] players = board.getPlayers();

        for (int i = 0; i < 4; i++) {
            double[][] probCopy;
            if (i != myIdx) {
                probCopy = ((HeuristicAgent)players[i]).getNotebook().getProbCopy();
            } else {
                probCopy = belief.getProbCopy();
            }
            int[] envelope = belief.getDeterminizedEnvelope();
            for(int j = 0; j < 3; j++) {
                if(j == 0){
                    if(probCopy[envelope[j]][0] == 0){
                        j=j;
                    }
                }
                else if(j==1){
                    if(probCopy[envelope[j]+9][0] == 0){
                        j=j;
                    }
                }
                else if(j==2){
                    if(probCopy[envelope[j]+15][0] == 0){
                        j=j;
                    }
                }
            }
        }
    }

    private void checkForEnvelope() {
        Player[] players = board.getPlayers();

        for (int i = 0; i < 4; i++) {
            if (i != myIdx) {
                if (((HeuristicAgent) players[i]).getNotebook().knowEnvelope()) {
                    state[WINNER] = i;
                    state[GAME_STATE] = 0;
                    break;
                }
            }
        }

    }

    /*private void notifyAllCardInEnvelope(int card, int cardType) {
        Player[] players = board.getPlayers();
        for(int i = 0; i < 4; i++){
            if(i!=myIdx){
                ((HeuristicAgent) players[i]).getNotebook().setProbabilityZero(card,cardType,i+1);
            }
        }
    }*/


    public void setBoard(Board oldBoard, Boolean newGame) {
        this.board = new Board(oldBoard.getPlayerLocations(), belief, oldBoard.getPlayers(), newGame, -1);
        if(state != null) {
            this.board.setTuples(new int[]{state[PLAYER_ONE_X], state[PLAYER_ONE_Y],
                    state[PLAYER_TWO_X], state[PLAYER_TWO_Y],
                    state[PLAYER_THREE_X], state[PLAYER_THREE_Y],
                    state[PLAYER_FOUR_X], state[PLAYER_FOUR_Y],});
        }
    }

    public int[][] getActionAndState(){
        return new int[][]{actionTaken,state};
    }

    public String getActionString(){
        switch(actionTaken[0]){
            case CHOOSE_DICE:
                return "DICE";
            case MOVE:
                return "MOVE";
            case SECRET_PASSAGE:
                return "SECRET";
            case SUGGEST:
                return "SUGGEST";
            case FALSIFY:
                return "FALSIFY";
            case ACCUSE:
                return "ACCUSE";
            case NO_FALSIFY:
                return "NO_FALSIFY";
            case CONTINUE_GAME:
                return "CONTINUE";
            case GAME_WON:
                return "GAME_WON";
            case DO_NOTHING:
                return "NOTHING";
        }
        return "";
    }

}
