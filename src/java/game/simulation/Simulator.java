package simulation;

public class Simulator {

    public Logger logger = new Logger();

    public Simulator(){
        //Cluedo game = new Cluedo(logger, null);
    }

    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        if(args.length != 4){
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
        }
    }


}
