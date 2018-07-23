package main;

import GUI.BoardGUI;
import agents.*;
import agents.Action;
import simulation.Gamelog;
import simulation.Logger;

import javax.swing.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;


public class Cluedo {

    public boolean hasHumanPlayer = false;
    private boolean stillUpdating = false;
    private boolean gameFinished = false;
    private boolean useGUI = false;
    private Card[] deck;
    private Card[] envelope;
    private Card[] faceUpCards;
    private BoardGUI boardGUI;
    public Board board;
    private Player[] players;
    private int playerTurnIndex = 0;
    public static Random rand = new SecureRandom();
    public Gamelog gamelog;
    public Agent winner;
    private int current_roll = roll();

    public Cluedo(LinkedList<String> agents) {
        initializeCards();
        initializePlayers(agents);
        gamelog = new Gamelog(getAgentArray(),envelope,faceUpCards);
        board = new Board(players, boardGUI, faceUpCards);
        setBoard();
        if(useGUI)
            boardGUI = new BoardGUI(board.getTiles(), this, players[0], hasHumanPlayer);
        if(hasHumanPlayer){
            ((HumanAgent)players[0]).setBoardGUI(boardGUI);
        }
        play();
    }

    private void setBoard() {
        for(Player player: players){
            if(((Agent)player).playerType.equals("MCTS"))
                ((BMCTSAgent)player).setBoard(board);
        }
    }

    private Agent[] getAgentArray() {
        return new Agent[]{(Agent)players[0],(Agent)players[1],(Agent)players[2],(Agent)players[3]};
    }

    private void play() {
        while(!gameFinished){
            Player currentPlayer = players[playerTurnIndex];
            boolean actionSuccessful;
            if(((Agent)currentPlayer).accused){
                nextTurn();
                continue;
            }

            LinkedList<Action> possibleActions = getPossibleActions(currentPlayer);
            Action actionTaken = currentPlayer.takeTurn(possibleActions, board.getPlayerLocation(currentPlayer));
            actionSuccessful = doAction(actionTaken, currentPlayer);
            if(!actionSuccessful){
                currentPlayer.actionFailed(actionTaken);
                continue;
            }
            possibleActions = getPossibleActions(currentPlayer);

            if(!possibleActions.isEmpty()){
                actionTaken = currentPlayer.takeTurn(possibleActions, board.getPlayerLocation(currentPlayer));
                doAction(actionTaken, currentPlayer);
            }

            nextTurn();
            currentPlayer.endTurn();
        }
    }

    private void nextTurn() {
        current_roll = roll();
        playerTurnIndex = (playerTurnIndex+1)%4;
        gamelog.nextTurn();
    }

    private boolean doAction(Action actionTaken, Player currentPlayer) {
        switch (actionTaken.actionType) {
            case "move":
                Boolean successful = board.movePlayer(actionTaken, currentPlayer);
                if(successful) {
                    ((Agent) currentPlayer).justMoved = true;
                    ((Agent)currentPlayer).hasSuggested = false;
                    updateGUI(actionTaken, currentPlayer);
                    gamelog.logAction(actionTaken, currentPlayer);
                    if(board.inRoom(currentPlayer)){
                        ((Agent) currentPlayer).playerLog.numRoomsVisited++;
                    }
                }
                else
                    return false;
                break;

            case "suggest":
                updateGUI(actionTaken, currentPlayer);
                suggest(actionTaken, currentPlayer);
                ((Agent)currentPlayer).hasSuggested = true;
                ((Agent) currentPlayer).justMoved = true;
                currentPlayer.recordChangeInEntropy();
                gamelog.logAction(actionTaken, currentPlayer);
                break;

            case "accuse":
                gamelog.logAction(actionTaken, currentPlayer);
                accuse(actionTaken, currentPlayer);
                updateGUI(actionTaken, currentPlayer);
                break;

            case "useSecretPassage":
                board.useSecretPassage(actionTaken, currentPlayer, useGUI);
                ((Agent)currentPlayer).justMoved = true;
                ((Agent)currentPlayer).hasSuggested = false;
                updateGUI(actionTaken, currentPlayer);
                gamelog.logAction(actionTaken, currentPlayer);
                ((Agent) currentPlayer).playerLog.numRoomsVisited++;
                break;

            case "doNothing":
                gamelog.logAction(actionTaken, currentPlayer);
                ((Agent)currentPlayer).justMoved = true;
                break;
        }
        return true;
    }

    private void accuse(Action actionTaken, Player currentPlayer) {
        boolean accusationCorrect = checkAccusation(actionTaken);
        ((Agent)currentPlayer).accused = true;
        if(accusationCorrect) {
            finishGame(currentPlayer);
            actionTaken.accusationRight = true;
        }
        updateGUI(actionTaken,currentPlayer);
    }

    private boolean checkAccusation(Action actionTaken) {
        int numberCorrect = 0;
        for(Card accusedCard: actionTaken.accusation){
            for(Card realCard: envelope){
                if(accusedCard.equals(realCard)) {
                    numberCorrect++;
                    break;
                }
            }
        }
        return (numberCorrect == 3);
    }

    private void finishGame(Player player) {
        winner = (Agent)player;
        gameFinished = true;
    }

    private void suggest(Action actionTaken, Player currentPlayer) {
        Card cardToShow;
        moveSuggestee(currentPlayer,actionTaken);
        for(int i = 1; i < 4; i++) {
            cardToShow = players[(playerTurnIndex + i) % 4].falsifySuggestion(currentPlayer, actionTaken.suggestion);
            if (!(cardToShow == null)){
                currentPlayer.showCard(players[(playerTurnIndex + i) % 4], cardToShow);
                if(useGUI)
                    updateGUI(new Action("showCard", cardToShow, currentPlayer), players[(playerTurnIndex + i) % 4]);
                broadCastCardShown(actionTaken, currentPlayer, players[(playerTurnIndex + i) % 4]);
                break;
            }
            else{
                broadCastNoCardShown(actionTaken, players[(playerTurnIndex + i) % 4]);
                if(useGUI)
                    updateGUI(new Action("showCard", null, currentPlayer), players[(playerTurnIndex + i) % 4]);
            }
        }
    }

    private void broadCastCardShown(Action action, Player currentPlayer, Player cardPlayer) {
        for(Player player: players){
            if(!player.equals(currentPlayer) && !player.equals(cardPlayer))
                player.cardShown(action, cardPlayer);
        }
    }

    private void broadCastNoCardShown(Action action, Player noCardPlayer) {
        for(Player player: players){
            if(!player.equals(noCardPlayer))
                player.noCardToShow(action, noCardPlayer);
            }
    }

    private void moveSuggestee(Player suggester, Action action) {
        String suggestee = action.suggestion[2].cardName;
        Room room = board.getRoom(suggester);
        switch (suggestee){
            case "peacock":
                if(((Agent)suggester).playerIndex != 1) {
                    board.movePiece(players[0], room.entranceTiles[0]);
                    ((Agent) players[0]).playerLog.numRoomsVisited++;
                    ((Agent) players[0]).hasSuggested = false;
                }
                break;
            case "green":
                if(((Agent)suggester).playerIndex != 2) {
                    board.movePiece(players[1], room.entranceTiles[0]);
                    ((Agent) players[1]).playerLog.numRoomsVisited++;
                    ((Agent) players[1]).hasSuggested = false;
                }
                break;
            case "scarlet":
                if(((Agent)suggester).playerIndex != 3) {
                    board.movePiece(players[2], room.entranceTiles[0]);
                    ((Agent) players[2]).playerLog.numRoomsVisited++;
                    ((Agent) players[2]).hasSuggested = false;
                }
                break;
            case "mustard":
                if(((Agent)suggester).playerIndex != 4) {
                    board.movePiece(players[3], room.entranceTiles[0]);
                    ((Agent) players[3]).playerLog.numRoomsVisited++;
                    ((Agent) players[3]).hasSuggested = false;
                }
                break;
        }
    }

    private synchronized void updateGUI(Action actionTaken, Player currentPlayer) {
        if(useGUI) {
            stillUpdating = true;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    boardGUI.movePiece(board.getPlayerLocations());
                    boardGUI.playerManager.updateInfo(actionTaken, currentPlayer, hasHumanPlayer);
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

    public void doneUpdating() {
        synchronized(this){
            this.stillUpdating = false;
            notifyAll();
        }
    }


    public int roll() {
        return 1 + rand.nextInt(6);
    }

    private LinkedList<Action> getPossibleActions(Player currentPlayer) {
        LinkedList<Action> possibleActions = new LinkedList<>();
        if(((Agent)currentPlayer).accused)
            return possibleActions;
        if(!((Agent)currentPlayer).justMoved) {
            possibleActions.add(new Action("move", current_roll));
            possibleActions.add(new Action("accuse"));
            if (board.inRoomWithSecretPassage(currentPlayer))
                possibleActions.add(new Action("useSecretPassage", board.getSecretPassage(currentPlayer)));
        }
        if(board.inRoom(currentPlayer) && !((Agent)currentPlayer).hasSuggested)
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

    private void initializePlayers(LinkedList<String> agents) {
        Card[][] cards = dealCards();
        players = new Player[4];
        if(hasHumanPlayer) {
            players = new Player[]{new HumanAgent(cards[0], faceUpCards, 0), new HeuristicAgent(cards[1], faceUpCards, 1),
                    new HeuristicAgent(cards[2], faceUpCards, 2), new HeuristicAgent(cards[3], faceUpCards, 3)};
        }
        else if(useGUI){
            players = new Player[]{new BMCTSAgent(cards[0], faceUpCards, 0), new HeuristicAgent(cards[1], faceUpCards, 1),
                    new HeuristicAgent(cards[2], faceUpCards, 2), new HeuristicAgent(cards[3], faceUpCards, 3)};
        }
        else {
            /*players = new Player[]{new BMCTSAgent(cards[0], faceUpCards, 0), new HeuristicAgent(cards[1], faceUpCards, 1),
                    new HeuristicAgent(cards[2], faceUpCards, 2), new HeuristicAgent(cards[3], faceUpCards, 3)};*/

            for (int i = 0; i < 4; i++) {
                int randIdx;
                if (i == 3)
                    randIdx = 0;
                else
                    randIdx = rand.nextInt(agents.size());
                String agentType = agents.remove(randIdx);
                addPlayer(agentType, i, cards);
            }
        }
    }

    private void addPlayer(String agentType, int i, Card[][] cards) {
        switch (agentType){
            case "Heuristic":
                players[i] = new HeuristicAgent(cards[i], faceUpCards, i);
                break;
            case "Random":
                players[i] = new RandomAgent(cards[i], faceUpCards, i);
                break;
            case "MCTS":
                players[i] = new BMCTSAgent(cards[i],faceUpCards,i);
        }
    }

    public void initializeCards(){
        deck = Card.makeCards();
        envelope = Card.makeEnvelope(deck);
        deck = Card.removeCards(envelope,deck);
        deck = Card.shuffle(deck);
        faceUpCards = new Card[]{deck[0], deck[1]};
        deck = Arrays.copyOfRange(deck,2,deck.length);
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

    public static void main(String args[]){
        Cluedo game = new Cluedo(null);
    }

}
