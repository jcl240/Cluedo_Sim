package simulation;

import agents.HeuristicAgent;
import agents.Player;
import agents.RandomAgent;
import com.mongodb.BasicDBObject;
import main.Cluedo;

import java.util.LinkedList;

public class Simulator {

    public Logger logger;
    public int numGames = 2000;

    public Simulator(){
        String playerTwoType = "Random";
        String playerOneType = "Heuristic";
        String simName = playerOneType+"Vs"+playerTwoType;
        logger = new Logger(simName,playerOneType,playerTwoType);
        while(logger.simlog.i <= numGames) {
            LinkedList<String> agents = new LinkedList<>();
            agents.add(playerOneType);agents.add(playerTwoType);agents.add(playerTwoType);agents.add(playerTwoType);
            Cluedo game = new Cluedo(agents);
            logger.storeGame(game.gamelog, game.winner);
        }
        logger.storeSimulation();
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        /*if(args.length != 4){
            System.out.println("Need 4 params: #games, agent1, agent2, useLogger");
            return;
        }
        try {
            int numGames = Integer.getInteger(args[0]);
            String agentOne = args[1];
            String agentTwo = args[2];
            boolean useLogger = Boolean.parseBoolean(args[3]);
        }
        catch(IllegalArgumentException e){
            System.out.println("Something was wrong with the parameters provided.");
        }*/
    }


}
