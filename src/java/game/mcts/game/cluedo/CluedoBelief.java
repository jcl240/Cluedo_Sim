package mcts.game.cluedo;

import mcts.game.Belief;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class CluedoBelief implements Belief, GameStateConstants {
    private double[][] probabilities = new double[21][5];

    public CluedoBelief(){}

    public CluedoBelief(double[][] arr){
        for(int i = 0; i < arr.length; i++){
            this.probabilities[i] = arr[i].clone();
        }
    }

    public CluedoBelief(CluedoBelief old){
        for(int i = 0; i < old.probabilities.length; i++){
            this.probabilities[i] = old.probabilities[i].clone();
        }
    }

    @Override
    public Belief copy() {
        return new CluedoBelief(this);
    }

    @Override
    public int[] getRepresentation() {
        return convertProbs(probabilities);
    }

    public int[] convertProbs(double[][] arr) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < arr.length; i++) {
            // tiny change 1: proper dimensions
            for (int j = 0; j < arr[i].length; j++) {
                // tiny change 2: actually store the values
                list.add((int)(arr[i][j]*100));
            }
        }

        // now you need to find a mode in the list.

        // tiny change 3, if you definitely need an array
        int[] vector = new int[list.size()];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = list.get(i);
        }
        return vector;
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
        double fullprob = 0;
        for(int j = 0; j < 5; j++){
            if(j != playerIndex)
                fullprob+=probabilities[card+offset][j];
        }
        probabilities[card+offset][playerIndex] = 0;
        normalizeProbabilities(card+offset);
        if(knownHandSize(playerIndex) == 4){
            setAllExceptHandZero(playerIndex);
        }
        if(knowEnvelope())
            setAllZeroEnvelope();
    }

    private void setAllZeroEnvelope() {
        int i = 0;
        for(double[] row: probabilities){
            if(row[0] != 1)
                row[0] = 0;
            normalizeProbabilities(i);
            i++;
        }
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
        if(knownHandSize(playerIdx) == 4){
            setAllExceptHandZero(playerIdx);
        }
        if(knowEnvelope())
            setAllZeroEnvelope();
    }

    private boolean cardUnknown(int i) {
        double entropy = getEntropy(probabilities[i]);
        return entropy != 0.0;
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
        suggestion[1] += getOffset(2);
        suggestion[2] += getOffset(3);
        LinkedList<Integer> cardsPossible = getCardsWithNonZeroProbability(suggestion, playerIndex);
        double probOfObservation = (3.0/15.0);
        double probOfObservationGivenCard = (1.0/cardsPossible.size());
        double update = (probOfObservationGivenCard / probOfObservation);

        for(int card: cardsPossible) {
            probabilities[card][playerIndex] *= update;
            normalizeProbabilities(card);
        }
        if(knownHandSize(playerIndex) == 4){
            setAllExceptHandZero(playerIndex);
        }
        if(knowEnvelope())
            setAllZeroEnvelope();

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

    public double getCardProb(int card, int cardType, int playerIdx) {
        int offset = getOffset(cardType);
        return probabilities[card+offset][playerIdx];
    }

    public int getCurrentEntropy() {
        double entropySum = 0;
        for(int i = 0; i < probabilities.length; i++){
            entropySum += getEntropy(probabilities[i]);
        }
        return (int)entropySum;
    }

    public int getNumberOfZeros() {
        int i = 0;
        for(double[] prob: probabilities){
            if(prob[0]+prob[1]+prob[2]+prob[3]+prob[4] == 0)
                i++;
        }
        return i;
    }

    public double getJointProbabilityInEnvelope(double roomProb, double suspectProb, double weaponProb) {
        double totalMass = 0;
        for(double[] prob: probabilities){
            totalMass += prob[0];
        }
        return ((roomProb/totalMass)*(suspectProb/totalMass)*(weaponProb/totalMass));
    }

    public double[][] getProbabilities() {
        double[][] probs = new double[21][5];
        for(int i = 0; i < this.probabilities.length; i++){
            probs[i] = this.probabilities[i].clone();
        }
        return probs;
    }

    public boolean isFaceUp(int cardIdx, int cardType) {
        int offset = getOffset(cardType);
        int index = cardIdx+offset;
        return (probabilities[index][0]+probabilities[index][1]+probabilities[index][2]+probabilities[index][3]+probabilities[index][4] == 0);
    }

    public boolean knowEnvelope() {
        int i = 0;
        for(double[] row: probabilities){
            if(row[0] == 1)
                i++;
        }
        if(i>3)
            i=i;
        return (i==3);
    }

    private void setAllExceptHandZero(int playerIdx) {
        int i = 0;
        for(double[] prob: probabilities){
            if(prob[playerIdx] != 1) {
                probabilities[i][playerIdx] = 0;
                normalizeProbabilities(i);
            }
            i++;
        }
    }

    private int knownHandSize(int playerIdx) {
        int numKnown = 0;
        for(double[] prob: probabilities){
            if(prob[playerIdx] == 1)
                numKnown++;
        }
        return numKnown;
    }
}
