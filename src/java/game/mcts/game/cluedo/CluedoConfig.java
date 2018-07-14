package mcts.game.cluedo;

import mcts.MCTSConfig;
import mcts.game.GameConfig;
import mcts.game.GameFactory;
import mcts.seeder.NullSeedTrigger;
import mcts.seeder.SeedTrigger;
import mcts.tree.selection.SelectionPolicy;
import mcts.tree.selection.UCT;
import mcts.tree.update.StateUpdater;
import mcts.tree.update.UpdatePolicy;

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
