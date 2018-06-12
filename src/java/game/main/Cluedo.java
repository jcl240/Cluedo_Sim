package main;

import GUI.BoardGUI;
import agents.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Cluedo {

    private Card[] deck;
    private Card[] envelope;
    private Card[] faceUpCards;
    private boolean useGUI = true;
    private BoardGUI boardGUI;
    private Board board;
    private Player[] players;
    private boolean gameFinished = false;
    private int playerTurnIndex = 0;

    public Cluedo() {
        initializeCards();
        initializePlayers();
        board = new Board(players, boardGUI);
        if(useGUI)
            boardGUI = new BoardGUI(board.getTiles());
        play();
    }

    private void play() {
        while(!gameFinished){
            Player currentPlayer = players[playerTurnIndex];
            LinkedList<Action> possibleActions = getPossibleActions(currentPlayer);
            Action actionTaken = currentPlayer.takeTurn(possibleActions);
            doAction(actionTaken, currentPlayer);
            possibleActions = getPossibleActions(currentPlayer);
            if(!possibleActions.isEmpty()){
                actionTaken = currentPlayer.takeTurn(possibleActions);
                doAction(actionTaken, currentPlayer);
            }
            else{
                playerTurnIndex = (playerTurnIndex+1)%4;
                currentPlayer.endTurn();
            }
        }
    }

    private void doAction(Action actionTaken, Player currentPlayer) {
        switch (actionTaken.actionType) {
            case "move":
                board.movePiece(actionTaken, currentPlayer);
                break;
            case "suggest":
                suggest(actionTaken, currentPlayer);
                break;
            case "accuse":
                accuse(actionTaken, currentPlayer);
                break;
            case "useSecretPassage":
                board.movePiece(actionTaken, currentPlayer);
                break;
        }

        if(useGUI)
            updateGUI(actionTaken, currentPlayer);
    }

    private void accuse(Action actionTaken, Player currentPlayer) {
        boolean accusationCorrect = checkAccusation(actionTaken);
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
            cardToShow = players[(playerTurnIndex + i) % 4].falsifySuggestion(actionTaken.suggestion);
            if (!(cardToShow == null)){
                currentPlayer.showCard(cardToShow);
                break;
            }
        }
        
    }

    private void updateGUI(Action actionTaken, Player currentPlayer) {

    }

    public int roll() {
        Random rand = new Random();
        return 1 + rand.nextInt(6);
    }

    private LinkedList<Action> getPossibleActions(Player currentPlayer) {
        LinkedList<Action> possibleActions = new LinkedList<>();
        if(!((Agent)currentPlayer).justMoved) {
            possibleActions.add(new Action("move"));
            possibleActions.add(new Action("accuse"));
            if (board.inRoomWithSecretPassage(currentPlayer))
                possibleActions.add(new Action("useSecretPassage"));
        }
        else if(board.inRoom(currentPlayer))
            possibleActions.add(new Action("suggest"));
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
        players = new Player[]{new HumanAgent(cards[0], boardGUI),new RandomAgent(cards[1]), new RandomAgent(cards[2]), new RandomAgent(cards[3])};
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
}
