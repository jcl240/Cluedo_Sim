package mcts;

import java.util.ArrayList;

import mcts.game.DeterminizationSampler;
import mcts.game.Game;
import mcts.game.GameFactory;
import mcts.listeners.SearchListener;
import mcts.tree.ExpansionPolicy;
import mcts.tree.SimulationPolicy;
import mcts.tree.Tree;
import mcts.tree.node.ChanceNode;
import mcts.tree.node.TreeNode;
import mcts.utils.Selection;
import mcts.utils.GameSample;
import mcts.utils.Priority;
import mcts.utils.PriorityRunnable;

/**
 * Performs one pass through the four stages of the Monte Carlo Tree Search algorithm.
 * @author sorinMD
 *
 */
public class MCTSAgent implements Runnable,PriorityRunnable{
	private Tree tree;
	private SearchListener listener;
	private MCTSConfig config;
	private GameFactory gameFactory;
	private static Priority priority = Priority.LOW;
	
	public MCTSAgent(Tree tree, SearchListener listener, MCTSConfig config,  GameFactory gameFactory) {
		this.tree = tree;
		this.listener = listener;
		this.gameFactory = gameFactory;
		this.config = config;
	}
	
	@Override
	public void run() {
		try {
			GameFactory factory = gameFactory.copy();
			ArrayList<Selection> visited = new ArrayList<Selection>();
			TreeNode node = tree.getRoot();
			//placeholder: there aren't any nodes to visit before root
			Selection selection = new Selection(false, node, 1.0);
			visited.add(selection); 
			
			boolean allSiblingsVisited = true; //ensures the first expansion of the tree node is performed
			int depth = 1;
			while(!node.isLeaf() && !node.isTerminal()){
				selection = config.selectionPolicy.selectChild(node, tree, factory, null);
				node = selection.getNode();
				allSiblingsVisited = selection.getBoolean();
				visited.add(selection);
				if(!allSiblingsVisited)
					break; //do not select further, still gathering statistics on siblings
				if(depth > config.maxTreeDepth) //reached max tree depth or stuck in one of the game's cycles.
					break;
				depth++;	
			}
			
			/*
			 * Expand only if certain conditions are satisfied:
			 * -all siblings have been visited at least SelectionPolicy.MINVISITS times;
			 * -the node is a leaf and not a terminal node;
			 * -the node can be expanded (never expand chance/nature nodes);
			 * -max tree size was not reached.
			 */
			if(!node.isTerminal() && node.canExpand() && allSiblingsVisited && !tree.maxSizeReached() && node.isLeaf()){
				selection = ExpansionPolicy.expand(tree, node, config.trigger, factory, null, config.nRootActProbSmoothing);
				visited.add(selection);
				node = selection.getNode();
			}
			
			Game game = factory.getGame(node.getState());
			DeterminizationSampler sampler = factory.getDeterminizationSampler();
			GameSample sample = null;
			double[] wins = new double[GameFactory.nMaxPlayers()];
			//TODO: these rollouts could also be parallelised if multiple
			for (int i=0; i < config.nRolloutsPerIteration; i++) {
				Game gameClone = game.copy();
				if(config.observableRollouts && factory.getBelief() != null) {
					//chance nodes may be different between a fully-observable state and a belief so execute them before sampling
					while(node instanceof ChanceNode && !gameClone.isTerminal()) {
						gameClone.gameTick();
						node = gameClone.generateNode();
					}
					if(!gameClone.isTerminal()) {
						sample = sampler.sampleObservableState(gameClone, factory);
						gameClone = sample.getGame();
					}
				}
				gameClone = SimulationPolicy.simulate(gameClone);
				if(gameClone.getWinner() != -1) {
					if(config.observableRollouts && config.weightedReturn) {
						double prob = 1.0;
						if(sample != null)//non-terminal leaf node case, otherwise all states in the belief are terminal.
							prob = sample.getProb();
						if(config.nRootStateProbSmoothing > 1) {
							prob = Math.pow(prob, 1.0/config.nRootStateProbSmoothing);
						}
						wins[gameClone.getWinner()] += prob;
					}else
						wins[gameClone.getWinner()] += 1.0;
				}else {
		 			/*
		 			 * this means everyone lost or the game hasn't finished yet (it
		 			 * could happen in Catan if roll-outs are too long, probably caused
		 			 * by no more legal building locations available on the board)
		 			 */
					System.err.println("WARNING: Possibly updating with results from a game that was not finished");
				}
			}
			int nRollouts = config.nRolloutsPerIteration;
			if(config.averageRolloutsResults) {
				for(int i = 0; i < wins.length; i++)
					wins[i] = wins[i]/config.nRolloutsPerIteration;
				nRollouts = 1;
			}
			config.updatePolicy.update(visited, wins, nRollouts);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			/*
			 * in case anything goes wrong in some of the iterations, we should
			 * still increment the listener to avoid deadlocks
			 */
			listener.increment();
		}
		
	}

	@Override
	public int getPriority() {
		return priority.getValue();
	}

}
