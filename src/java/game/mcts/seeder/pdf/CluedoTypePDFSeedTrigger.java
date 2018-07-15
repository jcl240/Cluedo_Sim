package mcts.seeder.pdf;

import com.google.common.util.concurrent.AtomicDoubleArray;
import mcts.game.GameFactory;
import mcts.game.cluedo.CluedoMCTS;
import mcts.game.cluedo.GameStateConstants;
import mcts.game.cluedo.typepdf.ActionTypePdf;
import mcts.game.cluedo.typepdf.UniformActionTypePdf;
import mcts.seeder.SeedTrigger;
import mcts.tree.node.StandardNode;
import mcts.tree.node.TreeNode;

import java.util.ArrayList;
import java.util.Map;

/**
 * This uses the typed pdf to seed in the tree without launching new threads.
 *
 *
 */
public class CluedoTypePDFSeedTrigger extends SeedTrigger implements GameStateConstants {

	public ActionTypePdf pdf = new UniformActionTypePdf();

	public CluedoTypePDFSeedTrigger() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void addNode(TreeNode node, GameFactory factory) {
		//no need to start a new thread just for this. Also no need to track what was already evaluated as this should be pretty quick
		int task = getTaskIDFromState(node);
		if(task == -1)
			return;
		CluedoMCTS game = (CluedoMCTS) factory.getGame(node.getState());
		ArrayList<Integer> types = game.listActionTypes();
		Map<Integer,Double> dist = pdf.getDist(types);
			
		ArrayList<int[]> actions = game.listPossiblities(false).getOptions();
		double[] prob = new double[actions.size()];
		for(Integer t : dist.keySet()) {
			int count = 0;
			for(int[] act : actions) {
				if(act[0] == t)
					count++;
			}
			double val = dist.get(t)/(double)count;
			for(int i = 0; i < actions.size(); i++) {
				if(actions.get(i)[0] == t)
					prob[i] = val;
			}
		}
		((StandardNode)node).pValue = new AtomicDoubleArray(prob);
	}

	@Override
	public void cleanUp() {
		//nothing to clean up
	}
		
	/**
	 * Logic to check if it is normal task. This type of seeding works only for this task.
	 * @param n the tree node containing the state description
	 * @return
	 */
	private int getTaskIDFromState(TreeNode n){
		return 1;
	} 
	
	
}
