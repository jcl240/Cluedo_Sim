package main;

import GUI.BoardGUI;
import agents.*;
import agents.Action;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Cluedo {

    public boolean hasHumanPlayer = false;
    private Card[] deck;
    private Card[] envelope;
    private Card[] faceUpCards;
    private boolean useGUI = true;
    private BoardGUI boardGUI;
    public Board board;
    private Player[] players;
    private boolean gameFinished = false;
    private int playerTurnIndex = 0;
    private boolean stillUpdating = false;

    public Cluedo() {
        initializeCards();
        initializePlayers();
        board = new Board(players, boardGUI);
        if(useGUI)
            boardGUI = new BoardGUI(board.getTiles(), this, players[0], hasHumanPlayer);
        if(hasHumanPlayer){
            ((HumanAgent)players[0]).setBoardGUI(boardGUI);
        }
        play();
    }

    private void play() {
        while(!gameFinished){
            Player currentPlayer = players[playerTurnIndex];

            if(((Agent)currentPlayer).accused){
                playerTurnIndex = (playerTurnIndex+1)%4;
                continue;
            }

            LinkedList<Action> possibleActions = getPossibleActions(currentPlayer);
            Action actionTaken = currentPlayer.takeTurn(possibleActions, board.getPlayerLocation(currentPlayer));
            doAction(actionTaken, currentPlayer);
            possibleActions = getPossibleActions(currentPlayer);

            if(!possibleActions.isEmpty()){
                actionTaken = currentPlayer.takeTurn(possibleActions, board.getPlayerLocation(currentPlayer));
                doAction(actionTaken, currentPlayer);
            }

            playerTurnIndex = (playerTurnIndex+1)%4;
            currentPlayer.endTurn();
        }
    }

    private void doAction(Action actionTaken, Player currentPlayer) {
        Boolean successful;
        switch (actionTaken.actionType) {
            case "move":
                successful = board.movePlayer(actionTaken, currentPlayer, useGUI);
                if(successful) ((Agent)currentPlayer).justMoved = true;
                updateGUI(actionTaken, currentPlayer);
                break;
            case "suggest":
                updateGUI(actionTaken, currentPlayer);
                suggest(actionTaken, currentPlayer);
                break;
            case "accuse":
                accuse(actionTaken, currentPlayer);
                updateGUI(actionTaken, currentPlayer);
                break;
            case "useSecretPassage":
                board.useSecretPassage(actionTaken, currentPlayer, useGUI);
                ((Agent)currentPlayer).justMoved = true;
                updateGUI(actionTaken, currentPlayer);
                break;
        }
    }

    private void accuse(Action actionTaken, Player currentPlayer) {
        boolean accusationCorrect = checkAccusation(actionTaken);
        ((Agent)currentPlayer).accused = true;
        if(accusationCorrect)
            finishGame();
    }

    private boolean checkAccusation(Action actionTaken) {
        int numberCorrect = 0;
        for(Card accusedCard: actionTaken.accusation){
            for(Card realCard: envelope){
                if(accusedCard == realCard) {
                    numberCorrect++;
                    break;
                }
            }
        }
        return (numberCorrect == 3);
    }

    private void finishGame() {
        gameFinished = true;
    }

    private void suggest(Action actionTaken, Player currentPlayer) {
        Card cardToShow;
        for(int i = 1; i < 4; i++) {
            cardToShow = players[(playerTurnIndex + i) % 4].falsifySuggestion(currentPlayer, actionTaken.suggestion);
            if (!(cardToShow == null)){
                currentPlayer.showCard(players[(playerTurnIndex + i) % 4], cardToShow);
                if(useGUI)
                    updateGUI(new Action("showCard", cardToShow, currentPlayer), players[(playerTurnIndex + i) % 4]);
                break;
            }
        }
    }

    private synchronized void updateGUI(Action actionTaken, Player currentPlayer) {
        if(useGUI) {
            stillUpdating = true;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    boardGUI.movePiece(board.getPlayerLocations());
                    boardGUI.playerManager.updateInfo(actionTaken, currentPlayer);
                }
            });
            try {
                while (stillUpdating) {
                    wait();
                }
            } catch (InterruptedException e) {
                System.out.println("got interrupted!");
            }
        }
    }

    public int roll() {
        Random rand = new Random();
        return 1 + rand.nextInt(6);
    }

    private LinkedList<Action> getPossibleActions(Player currentPlayer) {
        LinkedList<Action> possibleActions = new LinkedList<>();
        if(!((Agent)currentPlayer).justMoved) {
            possibleActions.add(new Action("move", roll()));
            possibleActions.add(new Action("accuse"));
            if (board.inRoomWithSecretPassage(currentPlayer))
                possibleActions.add(new Action("useSecretPassage", board.getSecretPassage(currentPlayer)));
        }
        else if(board.inRoom(currentPlayer))
            possibleActions.add(new Action("suggest", board.getRoom(currentPlayer)));
        return possibleActions;
    }

    private Card[][] dealCards() {
        Card[][] hands = new Card[4][4];
        for(int player = 1; player < 5; player++){
            for(int card = 4; card > 0; card--)
                hands[player-1][card-1] = deck[(player*4)-card];
        }
        return hands;
    }

    private void initializePlayers() {
        Card[][] cards = dealCards();
        if(hasHumanPlayer)
            players = new Player[]{new HumanAgent(cards[0], faceUpCards,0),new RandomAgent(cards[1], faceUpCards,1),
                new RandomAgent(cards[2], faceUpCards,2), new RandomAgent(cards[3], faceUpCards,3)};
        else
            players = new Player[]{new RandomAgent(cards[0], faceUpCards,0),new RandomAgent(cards[1], faceUpCards,1),
                    new RandomAgent(cards[2], faceUpCards,2), new RandomAgent(cards[3], faceUpCards,3)};
    }

    public void initializeCards(){
        deck = Card.makeCards();
        envelope = Card.makeEnvelope(deck);
        deck = Card.removeCards(envelope,deck);
        deck = Card.shuffle(deck);
        faceUpCards = new Card[]{deck[0], deck[1]};
        deck = Arrays.copyOfRange(deck,2,deck.length);
    }

    public static void main(String[] args) {
        Cluedo game = new Cluedo();
    }

    public void doneUpdating() {
        synchronized(this){
            this.stillUpdating = false;
            notifyAll();
        }
    }

    public String[][] getPlayerHands() {
        String hands[][] = new String[4][4];
        int i = 0;
        for(Player player: players){
            hands[i] = player.getHand();
            i++;
        }
        return hands;
    }

    public String[] getFaceUpCards() {
        String[] faceUpStrings = new String[2];
        int i = 0;
        for(Card card: faceUpCards){
            faceUpStrings[i] = card.cardName;
            i++;
        }
        return faceUpStrings;
    }
}
