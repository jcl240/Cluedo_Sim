package mcts.game.cluedo;

import mcts.game.Belief;

import java.util.LinkedList;


public class CluedoBelief implements Belief, GameStateConstants {
    private double[][] probabilities = new double[21][5];
    private int myIndex;

    CluedoBelief(){}

    CluedoBelief(CluedoBelief old){

    }

    @Override
    public Belief copy() {
        return new CluedoBelief(this);
    }

    @Override
    public int[] getRepresentation() {
        return new int[0];
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

    public void setProbabilityZero(int card, int cardType, int playerIndex) {
        int offset = getOffset(cardType);

        probabilities[card+offset][playerIndex] = 0;
        normalizeProbabilities(card+offset);
    }

    public void checkOffCard(int card, int cardType, int playerIdx) {
        int offset = getOffset(cardType);
        if(cardUnknown(card+offset)) {
            for(int i = 0; i < 5; i++){
                probabilities[card+offset][i] = 0;
            }
            if(playerIdx!=-1)
                probabilities[card+offset][playerIdx] = 1;
        }
    }

    private boolean cardUnknown(int i) {
        double entropy = getEntropy(probabilities[i]);
        return entropy == 0.0;
    }

    private int getOffset(int cardType) {
        switch (cardType){
            case WEAPON:
                return 15;
            case SUSPECT:
                return 9;
            case ROOM:
                return 0;
        }
        return 0;
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


    public void updateProbabilities(int[] suggestion, int playerIndex){
        LinkedList<Integer> cardsPossible = getCardsWithNonZeroProbability(suggestion, playerIndex);
        double probOfObservation = (3.0/15.0);
        double probOfObservationGivenCard = (1.0/cardsPossible.size());
        double update = (probOfObservationGivenCard / probOfObservation);

        for(int card: cardsPossible) {
            probabilities[card][playerIndex] *= update;
            normalizeProbabilities(card);
        }

    }

    private LinkedList<Integer> getCardsWithNonZeroProbability(int[] suggestion, int playerIndex) {
        LinkedList<Integer> nonZeroCards = new LinkedList<>();
        for(int card: suggestion){
            if(probabilities[card][playerIndex]!=0){
                nonZeroCards.add(card);
            }
        }
        return nonZeroCards;
    }

    public int[] checkForSolution(){
        int[] solution = new int[3];
        int j = 0;
        int i = 0;
        for(double[] prob: probabilities){
            if(prob[0] == 1){
                solution[i] = j;
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
