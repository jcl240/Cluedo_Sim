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
    private HeuristicNotebook notebook;
    int actionFailCount = 0;
    Board board;
    GameFactory gameFactory;
    MCTS mcts;

    public BMCTSAgent(Card[] hand, Card[] faceUp, int index) {
        super(hand, faceUp, index, "MCTS");
        notebook = new HeuristicNotebook(hand, playerIndex);
        for(Card card: faceUp) {
            notebook.checkOffCard(card, -1);
        }
    }

    @Override
    public Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation) {
        if(notebook.knowEnvelope())
            notebook=notebook;
        if(actionFailCount > 3)
            return new Action("doNothing");
        gameFactory = new GameFactory(new CluedoConfig(), new CluedoBelief(notebook.getProbabilities()), playerIndex-1, board);
        CluedoMCTS gameSim = (CluedoMCTS) gameFactory.getNewGame();
        setState(possibleActions, gameSim);
        mcts = new MCTS(new MCTSConfig(), gameFactory, gameSim.copy());
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
                logSuggestion(action.suggestion);
                break;
            case SECRET_PASSAGE:
                action = getSecretPassageAction(possibleActions, actionArray);
                break;
            case ACCUSE:
                action = getAccuseAction(possibleActions, actionArray);
                break;
            case DO_NOTHING:
                action = new Action("doNothing");
                break;

        }
        return action;
    }

    private Action getSecretPassageAction(LinkedList<Action> possibleActions, int[] actionArray) {
        Action action = getActionFromType("useSecretPassage", possibleActions);
        int roomAfterPassage = getRoomFromSecretPassage(actionArray[1]);
        action.towards = board.getClosestRoomEntrance(roomAfterPassage, board.getPlayerLocation(this));
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
        Card[] accusation = new Card[]{Card.getCardFromIndex(actionArray[1],ROOM),
                Card.getCardFromIndex(actionArray[2],SUSPECT),Card.getCardFromIndex(actionArray[3],WEAPON)};
        accusation = notebook.getMostLikelySolution();
        action.accusation = accusation;
        return action;
    }

    private Action getSuggestAction(LinkedList<Action> possibleActions, int[] actionArray) {
        Action action = getActionFromType("suggest", possibleActions);
        Card[] suggestion = new Card[]{Card.getCardFromIndex(actionArray[1],ROOM),
                Card.getCardFromIndex(actionArray[2],SUSPECT),Card.getCardFromIndex(actionArray[3],WEAPON)};
        action.suggestion = suggestion;
        return action;
    }

    private Action getMoveAction(LinkedList<Action> possibleActions, int[] actionArray) {
        Action action = getActionFromType("move", possibleActions);
        action.towards = board.getClosestRoomEntrance(actionArray[1], board.getPlayerLocation(this));
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
        if(justMoved || action == null)
            return 0;
        return action.roll;
    }

    private void setState(LinkedList<Action> possibleActions, CluedoMCTS gameSim) {
        /* 0: Playing or winner's index, 1: current player's index,
    2: current player justMoved, 3: current player's room,
    4: current roll, 5: current player is falsifying card,
    6: has suggested
   7: current suggested room, 8: current suggested suspect,
   9: current suggested weapon, 10: suggester index,
   11: player one accused, 12: player two accused,
   13: player three accused, 14: player four accused
*/
        int roll = getRoll(possibleActions);
        int moved = justMoved ? 1 : 0;
        int suggested = hasSuggested? 1:0;
        int[][] playerLocations = board.getPlayerLocations();
        gameSim.setState(
                new int[]
                        {PLAYING,playerIndex-1,moved,board.getRoom(playerIndex),roll,0,suggested,
                                0,0,0,0,0,0,0,0,
                                notebook.getCurrentEntropy(),-1,
                                playerLocations[0][0],playerLocations[0][1],playerLocations[1][0],playerLocations[1][1],
                                playerLocations[2][0],playerLocations[2][1],playerLocations[3][0],playerLocations[3][1],
                                0,
                                0,0,0,
                                -1,-1,-1
                        }
                );
    }

    @Override
    public void setBoard(Board board){
        this.board = board;
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
    }

    @Override
    public void noCardToShow(Action actionTaken, Player player) {
        for(Card card:actionTaken.suggestion){
            notebook.setProbabilityZero(card, ((Agent)player).playerIndex);
        }
    }

    @Override
    public void cardShown(Action action, Player cardPlayer) {
        notebook.updateProbabilities(action.suggestion, ((Agent)cardPlayer).playerIndex);
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
