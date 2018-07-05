package mcts.game.cluedo;

import mcts.MCTSConfig;
import mcts.game.GameConfig;
import mcts.game.GameFactory;

public class CluedoConfig extends GameConfig {

    public CluedoConfig() {
        id = GameFactory.CLUEDO;
    }

    @Override
    protected GameConfig copy() {
        return new CluedoConfig();
    }

    @Override
    public void selfCheck(MCTSConfig config) {

    }
}
