package simulation;

import agents.HeuristicAgent;
import agents.Player;
import agents.RandomAgent;
import com.mongodb.BasicDBObject;
import main.Cluedo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class Simulator {

    public Logger logger;
    public static int numGames = 2000;
    public static String playerOneType = "MCTS";
    public static String playerTwoType = "Random 1";
    public int playerOneWins = 0;
    public int playerTwoWins = 0;

    public Simulator(){
        String simName = playerOneType+"Vs"+playerTwoType;
        logger = new Logger(simName,playerOneType,playerTwoType);
        PrintWriter out = null;
        int i = 0;
        while(i < numGames) {
            LinkedList<String> agents = new LinkedList<>();
            agents.add(playerOneType);agents.add(playerTwoType);agents.add(playerTwoType);agents.add(playerTwoType);
            Cluedo game = new Cluedo(agents);
            i++;
            logger.storeGame(game.gamelog, game.winner);
            if(game.winner.playerType.equals(playerOneType)){
                playerOneWins++;
            }
            else {
                playerTwoWins++;
            }
            System.out.println("Game " + i +" over, winner: "+ game.winner.playerType);

            if(i%100==0){
                System.out.println(playerOneType + " wins: "+ playerOneWins);
                System.out.println(playerTwoType + " wins: "+ playerTwoWins);
                logger.storeSimulation();
            }
            System.gc();
        }


        logger.storeSimulation();
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
    }


}
