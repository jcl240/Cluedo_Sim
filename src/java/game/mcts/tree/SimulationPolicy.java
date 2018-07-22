package mcts.tree;

import mcts.game.Game;
import mcts.game.cluedo.CluedoMCTS;

import java.util.LinkedList;

/**
 * A utility for playing the game following a policy until max depth or a
 * terminal node is reached
 * TODO: set the maxDepth field via the configuration file
 * @author sorinMD
 *
 */
public class SimulationPolicy {
	private static int maxDepth = 100000;
	/**
	 * Run the rollout policy to the end of the game
	 * @param state
	 * @return the game in the final state
	 */
	public static Game simulate(Game game){
		int depth = 0;
		/*LinkedList<int[][]> actionsAndStates = new LinkedList<>();
		LinkedList<String> actionStrings = new LinkedList<>();*/
		while(!game.isTerminal()){
			depth++;
			game.gameTick();
			/*actionsAndStates.add(((CluedoMCTS)game).getActionAndState());
			actionStrings.add(((CluedoMCTS)game).getActionString());
			if(depth == 300)
				depth=depth;
			if(game.isTerminal())
				System.out.println(depth);
			if(depth==5000)
				depth=depth;*/
			if(depth > maxDepth){
				System.err.println("WARNING: rollout reached max depth!!!");
				break;
			}
		}
		return game;
	}
}
