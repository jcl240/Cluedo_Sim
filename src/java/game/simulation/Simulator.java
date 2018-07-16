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
    public static int numGames = 200;
    public static String playerTwoType = "Heuristic";
    public static String playerOneType = "MCTS";
    public int playerOneWins = 0;
    public int playerTwoWins = 0;

    public Simulator(){
        String simName = playerOneType+"Vs"+playerTwoType;
        //logger = new Logger(simName,playerOneType,playerTwoType);
        PrintWriter out = null;
        try {
            out = new PrintWriter("Cluedo_Sim/out/artifacts/Simulator/results.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int i = 0;
        while(i <= numGames) {
            LinkedList<String> agents = new LinkedList<>();
            agents.add(playerOneType);agents.add(playerTwoType);agents.add(playerTwoType);agents.add(playerTwoType);
            Cluedo game = new Cluedo(agents);
            i++;
            //logger.storeGame(game.gamelog, game.winner);
            if(game.winner.playerType.equals(playerOneType)){
                playerOneWins++;
            }
            else
                playerTwoWins++;
        }
        out.println(playerOneWins);
        out.println(playerTwoWins);
        out.close();
        //logger.storeSimulation();
    }

    public static void main(String[] args) {
        /*
        if(args.length != 3){
            System.out.println("Need 3 params: #games, agent1, agent2");
            return;
        }
        try {
            numGames = Integer.valueOf(args[0]);
            playerOneType = args[1];
            playerTwoType = args[2];
        }
        catch(IllegalArgumentException e){
            System.out.println("Something was wrong with the parameters provided.");
        }
        */
        Simulator simulator = new Simulator();
    }


}
