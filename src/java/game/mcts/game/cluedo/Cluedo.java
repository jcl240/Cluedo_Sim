package mcts.game.cluedo;

import mcts.game.Game;
import mcts.tree.node.TreeNode;
import mcts.utils.Options;

public class Cluedo implements Game {
    @Override
    public int[] getState() {
        return new int[0];
    }

    @Override
    public int getWinner() {
        return 0;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public int getCurrentPlayer() {
        return 0;
    }

    @Override
    public void performAction(int[] a, boolean sample) {

    }

    @Override
    public Options listPossiblities(boolean sample) {
        return null;
    }

    @Override
    public Game copy() {
        return null;
    }

    @Override
    public int[] sampleNextAction() {
        return new int[0];
    }

    @Override
    public int sampleNextActionIndex() {
        return 0;
    }

    @Override
    public TreeNode generateNode() {
        return null;
    }

    @Override
    public void gameTick() {

    }
}
