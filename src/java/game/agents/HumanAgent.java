package agents;

import GUI.BoardGUI;
import main.Card;

import javax.swing.*;
import java.util.LinkedList;

public class HumanAgent extends  Agent implements Player{

    private BoardGUI boardGUI;
    private boolean usingGUI;
    private Action chosenAction;
    private Card falsifiedCard;

    public HumanAgent(Card[] hand, Card[] faceUp, int index) {
        super(hand, faceUp, index);
    }

    public HumanAgent(Card[] hand, BoardGUI gui, Card[] faceUp, int index) {
        super(hand, faceUp, index);
        this.boardGUI = gui;
    }

    @Override
    public void endTurn(){
        chosenAction = null;
        justMoved = false;
    }

    public synchronized Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation){
        usingGUI = true;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boardGUI.playerManager.takeTurn(possibleActions, currentLocation);
            }
        });
        try {
            while (usingGUI) {
                wait();
            }
        } catch (InterruptedException e) {
            System.out.println("got interrupted!");
        }
        return chosenAction;
    }

    public void setChosenAction(Action chosenAction){
        synchronized(this) {
            this.chosenAction = chosenAction;
            usingGUI = false;
            notifyAll();
        }
    }

    @Override
    public synchronized Card falsifySuggestion(Player player, Card[] suggestion) {
        String playerName = "Player " + ((Agent)player).playerIndex;
        Card[] cardsContained = handContains(suggestion);
        if(cardsContained.length == 0)
            return null;
        usingGUI = true;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boardGUI.falsifyDialog(playerName, cardsContained);
            }
        });
        try {
            while (usingGUI) {
                wait();
            }
        } catch (InterruptedException e) {
            System.out.println("got interrupted!");
        }
        return falsifiedCard;
    }

    public void setFalsifiedCard(Card falsifiedCard){
        synchronized(this) {
            this.falsifiedCard = falsifiedCard;
            usingGUI = false;
            notifyAll();
        }
    }

    private Card[] handContains(Card[] suggestion) {
        LinkedList<Card> contained = new LinkedList<>();
        for(Card suggestedCard: suggestion){
            for(Card myCard: hand){
                if(suggestedCard.equals(myCard)){
                    contained.add(myCard);
                }
            }
        }
        return contained.toArray(new Card[contained.size()]);
    }

    @Override
    public synchronized void showCard(Player player, Card cardToShow) {
        usingGUI = true;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String playerName = "Player " + ((Agent)player).playerIndex;
                boardGUI.showCard(playerName,cardToShow.cardName);
            }
        });
        try {
            while (usingGUI) {
                wait();
            }
        } catch (InterruptedException e) {
            System.out.println("got interrupted!");
        }
    }

    public void setBoardGUI(BoardGUI gui){
        boardGUI = gui;
    }

    public void doneViewingCard(){
        synchronized(this) {
            usingGUI = false;
            notifyAll();
        }
    }


    @Override
    public void actionFailed(Action actionTaken) {

    }
}
