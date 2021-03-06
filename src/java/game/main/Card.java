package main;

import mcts.game.cluedo.GameStateConstants;

import java.util.*;

public class Card implements GameStateConstants {

    public String cardType;
    public String cardName;
    public int cardIndex;

    public Card(String type, String name, int i) {
        cardType = type;
        cardName = name;
        cardIndex = i;
    }

    public Card(String type, String name) {
        cardType = type;
        cardName = name;
    }

    //Fisher-Yates shuffle
    public static Card[] shuffle(Card[] cards){
        int n = cards.length;
        Random random = new Random();
        // Loop over array.
        for (int i = 0; i < cards.length; i++) {
            // Get a random index of the array past the current index.
            // ... The argument is an exclusive bound.
            //     It will not go past the array's end.
            int randomValue = i + random.nextInt(n - i);
            // Swap the random element with the present element.
            Card randomElement = cards[randomValue];
            cards[randomValue] = cards[i];
            cards[i] = randomElement;
        }
        return cards;
    }

    public static Card[] makeCards(){
        String[] names = new String[]{"hall","lounge","diningroom","kitchen","ballroom","conservatory","billiardroom","library","study",
                "mustard","plum","green","peacock","scarlet","white",
                "knife","candlestick","revolver","rope","leadpipe","wrench"};
        Card[] deck = new Card[21];
        String type;
        for(int i = 0; i < names.length; i++){
            if(i < 9){
                type = "room";
                deck[i] = new Card(type,names[i],i);
            }
            else if(i <15){
                type = "suspect";
                deck[i] = new Card(type,names[i],i-9);
            }
            else{
                type = "weapon";
                deck[i] = new Card(type,names[i],i-15);
            }

        }
        return deck;
    }

    public static int getCardIndex(String cardName){
        Card[] cards = makeCards();
        int i = 0;
        for(Card card: cards){
            if(card.cardName.equals(cardName))
                return i;
            i++;
        }
        return -1;
    }

    public static  Card[] makeEnvelope(Card[] deck){
        Card[] rooms = shuffle(Arrays.copyOfRange(deck,0,9));
        Card[] weapons = shuffle(Arrays.copyOfRange(deck,9,15));
        Card[] suspects = shuffle(Arrays.copyOfRange(deck,15,21));
        Card[] envelope = new Card[]{rooms[0],weapons[0],suspects[0]};
        return envelope;
    }

    public static Card[] removeCards(Card[] envelope, Card[] deck){
        Card[] newDeck = new Card[18];
        int j = 0;
        for(int i = 0; i <deck.length; i++){
            if(!inEnvelope(deck[i], envelope)){
                newDeck[j] = deck[i];
                j++;
            }
        }
        return newDeck;
    }

    private static boolean inEnvelope(Card card, Card[] envelope) {
        for(Card x: envelope){
            if(x.cardName.equals(card.cardName)) return true;
        }
        return false;
    }

    public static Card getCardFromIndex(int cardIndex, int cardType) {
        Card[] cards = Card.makeCards();
        int offset = getOffset(cardType);
        return cards[cardIndex+offset];
    }

    private static int getOffset(int cardType) {
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


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final Card other = (Card)obj;
        return (this.cardType.equals(other.cardType) &&
                this.cardName.equals(other.cardName));
    }

}
