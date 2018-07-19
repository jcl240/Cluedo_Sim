package agents;

import main.Card;
import main.Cluedo;
import main.Room;
import main.Tuple;
import mcts.game.cluedo.GameStateConstants;

import java.util.Arrays;
import java.util.LinkedList;

public class HeuristicNotebook extends Notebook implements GameStateConstants {

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

    public HeuristicNotebook(double[][] probabilities, int idx, int MCTSidx) {
        super();
        this.myIndex = idx;
        this.probabilities = probabilities;
        if(MCTSidx != -1 && (MCTSidx+1 != myIndex)){
            spreadProbabilities(MCTSidx);
        }
        int i = 0;
        for(Tuple<Card, Boolean> tuple : cardList){
            if(isZero(probabilities[i]))
                tuple.y = true;
            i++;
        }
        unknownCount = unknownCardCount();
    }

    private void spreadProbabilities(int MCTSidx) {
        for(double[] probs: probabilities){
            if(probs[MCTSidx+1] == 1){
                for(int i = 0; i < 5; i++){
                    if(i != myIndex)
                        probs[i] = .25;
                }
            }
        }
    }

    private boolean isZero(double[] probability) {
        double sum = 0;
        for(double dob: probability){
            sum += dob;
        }
        return sum==0;
    }


    public void checkOffCard(Card card, int playerIdx) {
        int index = cardList.indexOf(new Tuple<>(card,false));
        if(index != -1) {
            cardList.get(index).y = true;
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
        int index = cardList.indexOf(new Tuple<>(card,false));
        if(index == -1)
            index = cardList.indexOf(new Tuple<>(card,true));

        probabilities[index][playerIndex] = 0;
        normalizeProbabilities(index);
    }

    public Card[] getInformedSuggestion(Room currentRoom) {
        Card[] suggestion = new Card[3];
        int roomIdx = Card.getCardIndex(currentRoom.roomName);
        suggestion[0] = Card.getCardFromIndex(roomIdx, 0);
        String[] types = {"suspect", "weapon"};
        int i = 1;
        double maxEntropy = -10;
        for(int j = 0; j < cardList.size(); j++){
            Tuple<Card,Boolean> tuple = cardList.get(j);
            String cardType = tuple.x.cardType;
            if(!cardType.equals(types[i-1]) && !cardType.equals("room")){
                i++;
                maxEntropy = -10;
            }
            double entropy = getEntropy(probabilities[j]);
            if(tuple.x.cardType.equals(types[i-1]) && entropy > maxEntropy) {
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
                solution[i] = cardList.get(j).x;
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

    public Card[] getMostLikelySolution() {
        Card[] solution = new Card[3];
        String[] types = new String[]{"room","suspect","weapon"};
        int j = 0;
        int i = 0;
        double maxProb = -1;
        for(double[] prob: probabilities){
            if(!cardList.get(i).x.cardType.equals(types[j])) {
                j++;
                maxProb = -1;
            }
            if(prob[0] > maxProb && cardList.get(i).x.cardType.equals(types[j])){
                solution[j] = cardList.get(i).x;
                maxProb = prob[0];
            }
            i++;
        }
        return solution;
    }

    public String getHighestEntropyRoom() {
        LinkedList<Card> roomsWithMaxEntropy = new LinkedList<>();
        double maxEntropy = -10;
        for(int j = 0; j < cardList.size(); j++){
            Tuple<Card,Boolean> tuple = cardList.get(j);
            String cardType = tuple.x.cardType;
            double entropy = getEntropy(probabilities[j]);
            if(!tuple.y && tuple.x.cardType.equals("room") && entropy > maxEntropy) {
                maxEntropy = entropy;
                removeAll(roomsWithMaxEntropy);
                roomsWithMaxEntropy.add(tuple.x);
            }
            else if(!tuple.y && tuple.x.cardType.equals("room") && entropy == maxEntropy){
                roomsWithMaxEntropy.add(tuple.x);
            }

        }

        Card randMaxRoom = roomsWithMaxEntropy.get(Cluedo.rand.nextInt(roomsWithMaxEntropy.size()));

        return randMaxRoom.cardName;
    }

    private void removeAll(LinkedList<Card> list) {
        while(list.size() > 0){
            list.removeFirst();
        }
    }



    public void updateProbabilities(Card[] suggestion, int playerIndex){
        LinkedList<Card> cardsPossible = getCardsWithNonZeroProbability(suggestion, playerIndex);
        double probOfObservation = (1.0/unknownCount);
        double probOfObservationGivenCard = (1.0/cardsPossible.size());
        double update = (probOfObservationGivenCard / probOfObservation);

        for(Card card: cardsPossible) {
            int index = cardList.indexOf(new Tuple<>(card, false));
            if (index == -1)
                index = cardList.indexOf(new Tuple<>(card, true));

            probabilities[index][playerIndex] *= update;
            normalizeProbabilities(index);

        }

    }

    private LinkedList<Card> getCardsWithNonZeroProbability(Card[] suggestion, int playerIndex) {
        LinkedList<Card> nonZeroCards = new LinkedList<>();
        for(Card card: suggestion){
            int index = cardList.indexOf(new Tuple<>(card,false));
            if(index == -1)
                index = cardList.indexOf(new Tuple<>(card,true));

            if(probabilities[index][playerIndex]!=0){
                nonZeroCards.add(cardList.get(index).x);
            }

        }
        return nonZeroCards;
    }

    public double[][] getProbabilities() {
        return probabilities;
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

    public void setProbabilities(double[][] probabilities) {
        this.probabilities = probabilities;
    }

    public void setProbabilityZero(int cardIdx, int cardType, int playerIndex) {
        int index = cardIdx+getOffset(cardType);
        probabilities[index][playerIndex] = 0;
        normalizeProbabilities(index);
    }

    public void updateProbabilities(int[] suggestion, int playerIndex) {
        int room = suggestion[0];
        int suspect = suggestion[1]+getOffset(SUSPECT);
        int weapon = suggestion[2]+getOffset(WEAPON);
        int[] indices = new int[]{room,suspect,weapon};
        LinkedList<Integer> cardsPossible = getCardsWithNonZeroProbability(indices, playerIndex);
        double probOfObservation = (1.0/unknownCount);
        double probOfObservationGivenCard = (1.0/cardsPossible.size());
        double update = (probOfObservationGivenCard / probOfObservation);

        for(int index: cardsPossible) {
            probabilities[index][playerIndex] *= update;
            normalizeProbabilities(index);
        }
    }

    private LinkedList<Integer> getCardsWithNonZeroProbability(int[] suggestion, int playerIndex) {
        LinkedList<Integer> nonZeroCards = new LinkedList<>();
        for(int index: suggestion){
            if(probabilities[index][playerIndex]!=0){
                nonZeroCards.add(index);
            }
        }
        return nonZeroCards;
    }

    public void checkOffCard(int cardIdx, int cardType, int playerIdx) {
        int offset = getOffset(cardType);
        int index = cardIdx+offset;
        cardList.get(index).y = true;
        for(int i = 0; i < 5; i++){
            probabilities[index][i] = 0;
        }
        if(playerIdx!=-1)
            probabilities[index][playerIdx] = 1;
        if(playerIdx == myIndex && knownHandSize() == 4){
            setAllExceptHandZero();
        }
    }

    private void setAllExceptHandZero() {
        int i = 0;
        for(double[] prob: probabilities){
            if(prob[myIndex] != 1) {
                setProbabilityZero(cardList.get(i).x, myIndex);
            }
            i++;
        }
    }

    private int knownHandSize() {
        int numKnown = 0;
        for(double[] prob: probabilities){
            if(prob[myIndex] == 1)
                numKnown++;
        }
        return numKnown;
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

    public double getCardProb(int card, int cardType, int playerIdx) {
        int offset = getOffset(cardType);
        return probabilities[card+offset][playerIdx];
    }
}
