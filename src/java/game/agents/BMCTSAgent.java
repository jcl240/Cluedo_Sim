package agents;

import main.Board;
import main.Card;
import mcts.MCTS;
import mcts.MCTSConfig;
import mcts.game.GameFactory;
import mcts.game.catan.Catan;
import mcts.game.catan.CatanConfig;
import mcts.game.cluedo.CluedoMCTS;
import mcts.game.cluedo.CluedoConfig;
import mcts.listeners.SearchListener;
import mcts.utils.Options;

import java.util.LinkedList;

public class BMCTSAgent extends Agent implements Player {
    CluedoMCTS gameSim;
    GameFactory gameFactory;

    public BMCTSAgent(Card[] hand, Card[] faceUp, int index, String type, Board board) {
        super(hand, faceUp, index, type);
        gameFactory = new GameFactory(new CluedoConfig(), null);
        gameSim = (CluedoMCTS) gameFactory.getNewGame();
        gameSim.setBoard(board);
    }

    @Override
    public Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation) {
        MCTS mcts = new MCTS(new MCTSConfig(), gameFactory, gameSim.copy());
        //TODO: find a better approach to wait for the tree to finish...
        SearchListener listener = mcts.search();
        listener.waitForFinish();
        int idx = mcts.getNextActionIndex();
        Options options = gameSim.listPossiblities(false);


        return null;
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
}
