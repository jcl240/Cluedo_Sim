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

    /** Sample uniformly over the action types before sampling specifics in rollouts */
    public boolean SAMPLE_FROM_DISTRIBUTION_OVER_TYPES_IN_ROLLOUTS = true;
    /** Makes all actions equally likely to be legal and it also ignores the type distribution if there is any during belief rollouts. */
    public boolean ENFORCE_UNIFORM_TYPE_DIST_IN_BELIEF_ROLLOUTS = false;
    /** What distribution over action types should be used in rollouts.*/

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
