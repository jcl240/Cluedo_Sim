package agents;

import main.Card;
import main.Cluedo;
import main.Room;
import main.Tuple;

import java.util.Arrays;
import java.util.LinkedList;

public class HeuristicNotebook extends Notebook{

    private double[][] probabilities = new double[21][5];
    private int myIndex;

    public HeuristicNotebook(Card[] startingHand, int myIndex){
        super();
        this.myIndex = myIndex;
        for(double[] array: probabilities) {
            Arrays.fill(array, .25);
            array[myIndex] = 0;
        }
        for(Card card: startingHand){
            checkOffCard(card, myIndex);
        }
    }


    public void checkOffCard(Card card, int playerIdx) {
        int index = notebook.indexOf(new Tuple<>(card,false));
        if(index != -1) {
            notebook.get(index).y = true;
            for(int i = 0; i < 5; i++){
                probabilities[index][i] = 0;
            }
            if(playerIdx!=-1)
                probabilities[index][playerIdx] = 1;
        }
        unknownCount = unknownCardCount();
    }

    public void normalizeProbabilities(int index){
        double[] pmf = probabilities[index];
        double sum = pmf[0]+pmf[1]+pmf[2]+pmf[3]+pmf[4];
        int i = 0;
        if(sum != 0) {
            for (double prob : pmf) {
                pmf[i] = pmf[i] / sum;
                i++;
            }
        }
        int j = 0;
    }


    public void setProbabilityZero(Card card, int playerIndex) {
        int index = notebook.indexOf(new Tuple<>(card,false));
        if(index == -1)
            index = notebook.indexOf(new Tuple<>(card,true));

        probabilities[index][playerIndex] = 0;
        normalizeProbabilities(index);
    }

    public Card[] getInformedSuggestion(Room currentRoom) {
        Card[] suggestion = new Card[3];
        suggestion[0] = new Card("room",currentRoom.roomName);
        String[] types = {"weapon", "suspect"};
        int i = 1;
        double maxEntropy = -10;
        for(int j = 0; j < notebook.size(); j++){
            Tuple<Card,Boolean> tuple = notebook.get(j);
            String cardType = tuple.x.cardType;
            if(!cardType.equals(types[i-1]) && !cardType.equals("room")){
                i++;
                maxEntropy = -10;
            }
            double entropy = getEntropy(probabilities[j]);
            if(!tuple.y && tuple.x.cardType.equals(types[i-1]) && entropy > maxEntropy) {
                suggestion[i] = tuple.x;
                maxEntropy = entropy;
            }
        }
        return suggestion;
    }

    public double getEntropy(double[] probs){
        double sum = 0;
        for(double prob: probs){
            if(prob == 0)
                continue;
            sum -= prob*Math.log(prob)/Math.log(2);
        }
        return sum;
    }

    public Card[] checkForSolution(){
        Card[] solution = new Card[3];
        int j = 0;
        int i = 0;
        for(double[] prob: probabilities){
            if(prob[0] == 1){
                solution[i] = notebook.get(j).x;
                i++;
            }
            j++;
        }
        if(i == 3){
            return solution;
        }
        else
            return null;
    }
}
