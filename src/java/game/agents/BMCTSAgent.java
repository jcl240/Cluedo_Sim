package agents;

import main.Board;
import main.Card;
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

public class BMCTSAgent extends Agent implements Player, GameStateConstants {
    CluedoMCTS gameSim;
    GameFactory gameFactory;

    public BMCTSAgent(Card[] hand, Card[] faceUp, int index) {
        super(hand, faceUp, index, "MCTS");
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

        return null;
    }

    private int getRoll(LinkedList<Action> possibleActions) {
        for(Action action: possibleActions){
            if(action.actionType.equals("move"))
                return action.roll;
        }
        return -1;
    }

    private void setState(int roll) {
        gameSim.setState(new int[]{1,0,0,0,roll,0,2,0,0,0,0,0,0,0,0});
    }

    @Override
    public void endTurn() {

    }

    @Override
    public Card falsifySuggestion(Player player, Card[] suggestion) {
        return null;
    }

    @Override
    public void showCard(Player player, Card cardToShow) {

    }

    @Override
    public void actionFailed(Action actionTaken) {

    }

    @Override
    public void noCardToShow(Action actionTaken, Player player) {

    }

    @Override
    public void cardShown(Action action, Player cardPlayer) {

    }

    @Override
    public void setBoard(Board board){
        gameFactory.setBoard(board);
        gameSim = (CluedoMCTS) gameFactory.getNewGame();
    }
}
