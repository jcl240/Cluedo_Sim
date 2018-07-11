package agents;

import main.Board;
import main.Card;
import main.Room;
import mcts.MCTS;
import mcts.MCTSConfig;
import mcts.game.GameFactory;
import mcts.game.catan.Catan;
import mcts.game.catan.CatanConfig;
import mcts.game.cluedo.CluedoBelief;
import mcts.game.cluedo.CluedoMCTS;
import mcts.game.cluedo.CluedoConfig;
import mcts.game.cluedo.GameStateConstants;
import mcts.listeners.SearchListener;
import mcts.utils.Options;

import java.util.LinkedList;

import static main.Card.shuffle;

public class BMCTSAgent extends Agent implements Player, GameStateConstants {
    CluedoMCTS gameSim;
    GameFactory gameFactory;
    private HeuristicNotebook notebook;
    int actionFailCount = 0;
    private int[] movementGoal;
    Board board;

    public BMCTSAgent(Card[] hand, Card[] faceUp, int index) {
        super(hand, faceUp, index, "MCTS");
        notebook = new HeuristicNotebook(hand, playerIndex);
        for(Card card: faceUp) {
            notebook.checkOffCard(card, -1);
        }
        gameFactory = new GameFactory(new CluedoConfig(), new CluedoBelief());
    }

    @Override
    public Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation) {
        int roll = getRoll(possibleActions);
        setState(roll);
        MCTS mcts = new MCTS(new MCTSConfig(), gameFactory, gameSim.copy());
        //TODO: find a better approach to wait for the tree to finish...
        SearchListener listener = mcts.search();
        listener.waitForFinish();
        int idx = mcts.getNextActionIndex();
        Options options = gameSim.listPossiblities(false);
        Action actionToTake = getAction(options,idx,possibleActions);
        return actionToTake;
    }

    private Action getAction(Options options, int idx, LinkedList<Action> possibleActions) {
        int[] actionArray = options.getOptions().get(idx);
        Action action = new Action("doNothing");
        switch (actionArray[0]) {
            case MOVE:
                action = getMoveAction(possibleActions, actionArray);
                break;
            case SUGGEST:
                action = getSuggestAction(possibleActions, actionArray);
                break;
            case SECRET_PASSAGE:
                action = getSecretPassageAction(possibleActions, actionArray);
                break;
            case ACCUSE:
                action = getAccuseAction(possibleActions, actionArray);
                break;
        }
        return action;
    }

    private Action getSecretPassageAction(LinkedList<Action> possibleActions, int[] actionArray) {
        Action action = getActionFromType("useSecretPassage", possibleActions);
        int roomAfterPassage = getRoomFromSecretPassage(actionArray[1]);
        action.towards = board.getRoomEntrance(roomAfterPassage);
        return action;
    }

    private int getRoomFromSecretPassage(int i) {
        switch (i){
            case STUDY:
                return KITCHEN;
            case KITCHEN:
                return STUDY;
            case LOUNGE:
                return CONSERVATORY;
            case CONSERVATORY:
                return LOUNGE;
        }
        return -1;
    }

    private Action getAccuseAction(LinkedList<Action> possibleActions, int[] actionArray) {
        Action action = getActionFromType("accuse", possibleActions);
        Card[] accusation = new Card[]{Card.getCardFromIndex(ROOM,actionArray[1]),
                Card.getCardFromIndex(SUSPECT,actionArray[2]),Card.getCardFromIndex(WEAPON,actionArray[3])};
        action.accusation = accusation;
        return action;
    }

    private Action getSuggestAction(LinkedList<Action> possibleActions, int[] actionArray) {
        Action action = getActionFromType("suggest", possibleActions);
        Card[] suggestion = new Card[]{Card.getCardFromIndex(ROOM,actionArray[1]),
                Card.getCardFromIndex(SUSPECT,actionArray[2]),Card.getCardFromIndex(WEAPON,actionArray[3])};
        action.suggestion = suggestion;
        return action;
    }

    private Action getMoveAction(LinkedList<Action> possibleActions, int[] actionArray) {
        Action action = getActionFromType("move", possibleActions);
        action.towards = board.getRoomEntrance(actionArray[1]);
        return action;
    }

    private Action getActionFromType(String s, LinkedList<Action> possibleActions) {
        for(Action action: possibleActions){
            if(action.actionType.equals(s))
                return action;
        }
        return null;
    }

    private int getRoll(LinkedList<Action> possibleActions) {
        Action action = getActionFromType("move", possibleActions);
        return action.roll;
    }

    private void setState(int roll) {
        gameSim.setState(new int[]{1,playerIndex,0,0,roll,0,2,0,0,0,0,0,0,0,0});
    }

    @Override
    public void endTurn() {
        this.justMoved = false;
        actionFailCount = 0;
    }

    @Override
    public Card falsifySuggestion(Player player, Card[] suggestion) {
        suggestion = shuffle(suggestion);
        for(Card suggestedCard: suggestion){
            for(Card myCard: this.hand){
                if(suggestedCard.equals(myCard)) {
                    return myCard;
                }
            }
        }
        return null;
    }

    @Override
    public void showCard(Player player, Card cardToShow) {
        int playerIndex = ((Agent)player).playerIndex;
        notebook.checkOffCard(cardToShow, playerIndex);
    }

    @Override
    public void actionFailed(Action actionTaken) {
        actionFailCount++;
        if(actionTaken.actionType.equals("move"))
            movementGoal = null;
    }

    @Override
    public void noCardToShow(Action actionTaken, Player player) {
        for(Card card:actionTaken.suggestion){
            notebook.setProbabilityZero(card, ((Agent)player).playerIndex);
        }
    }

    @Override
    public void cardShown(Action action, Player cardPlayer) {
        notebook.updateProbabilities(action.suggestion, cardPlayer);
    }

    @Override
    public void setBoard(Board board){
        this.board = board;
        gameFactory.setBoard(board);
        gameSim = (CluedoMCTS) gameFactory.getNewGame();
    }

    private void logSuggestion(Card[] suggestion){
        int numKnown = 1;
        LinkedList<String> suggestionList = new LinkedList<>();
        for(Card card: suggestion){
            suggestionList.add(card.cardName);
            suggestionList.add(notebook.knowCard(card));
        }
        playerLog.logSuggestion(numKnown, suggestionList);
    }
}
