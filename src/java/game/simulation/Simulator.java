package simulation;

import com.mongodb.BasicDBObject;
import main.Cluedo;

public class Simulator {

    public Logger logger;
    public int numGames = 100;

    public Simulator(){
        logger = new Logger("Agent1vsAgent2");
        while(logger.simlog.i <= numGames) {
            Cluedo game = new Cluedo(null);
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
