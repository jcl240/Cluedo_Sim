package agents;

import main.Card;
import mcts.game.GameFactory;
import mcts.game.catan.Catan;
import mcts.game.catan.CatanConfig;
import mcts.game.cluedo.Cluedo;
import mcts.game.cluedo.CluedoConfig;

import java.util.LinkedList;

public class BMCTSAgent extends Agent implements Player {
    Cluedo game;
    GameFactory gameFactory;

    public BMCTSAgent(Card[] hand, Card[] faceUp, int index, String type) {
        super(hand, faceUp, index, type);
        gameFactory = new GameFactory(new CluedoConfig(), null);
        game = (Cluedo) gameFactory.getNewGame();
    }

    @Override
    public Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation) {
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