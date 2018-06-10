package main;

import agents.HumanAgent;
import agents.Player;
import agents.RandomAgent;

import java.util.Arrays;

public class Cluedo {

    private Card[] deck;
    private Card[] envelope;
    private Card[] faceUpCards;
    private boolean useGUI = true;
    private BoardGUI boardGUI;
    private Board board;
    private Player[] players;

    public Cluedo() {
        initializeCards();
        initializePlayers();
        board = new Board();
        if(useGUI)
            boardGUI = new BoardGUI(board.getTiles());
        play();
    }

    private void play() {

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
        players = new Player[]{new HumanAgent(cards[0]),new RandomAgent(cards[1]), new RandomAgent(cards[2]), new RandomAgent(cards[3])};
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
