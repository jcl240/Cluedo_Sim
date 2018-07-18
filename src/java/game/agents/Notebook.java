package agents;

import main.Card;
import main.Cluedo;
import main.Room;
import main.Tuple;

import java.util.LinkedList;
import java.util.Random;

public class Notebook {

    LinkedList<Tuple<Card, Boolean>> cardList = new LinkedList<>();
    int unknownCount;

    public Notebook(Card[] startingHand){
        initializeNotebook();
        for(Card card: startingHand){
            checkOffCard(card);
        }
    }

    public Notebook(){
        initializeNotebook();
    }

    protected void initializeNotebook() {
        Card[] deck = Card.makeCards();
        for(Card card:deck){
            cardList.add(new Tuple<>(card, false));
        }
    }

    public void checkOffCard(Card card) {
        int index = cardList.indexOf(new Tuple<>(card,false));
        if(index != -1)
            cardList.get(index).y = true;
        unknownCount = unknownCardCount();
    }

    public int unknownCardCount() {
        int count = 0;
        for(Tuple<Card, Boolean> tuple: cardList){
            if(!tuple.y)
                count++;
        }
        return count;
    }

    public Card[] getAccusation() {
        Card[] accusation = new Card[3];
        int i = 0;
        for(Tuple<Card, Boolean> tuple: cardList){
            if(!tuple.y) {
                accusation[i] = tuple.x;
                i++;
            }
        }
        return accusation;
    }

    public Card[] getRandomSuggestion(Room currentRoom) {
        Card[] accusation = new Card[3];
        accusation[0] = new Card("room",currentRoom.roomName);
        String[] types = {"weapon", "suspect"};
        int i = 1;
        while(i < 3){
            Tuple<Card,Boolean> tuple = cardList.get(Cluedo.rand.nextInt(cardList.size()));
            if(!tuple.y && tuple.x.cardType.equals(types[i-1])) {
                accusation[i] = tuple.x;
                i++;
            }
        }
        return accusation;
    }

    public LinkedList<Card> getUnknownRooms() {
        LinkedList<Card> roomlist = new LinkedList<>();
        for(Tuple<Card,Boolean> tuple: cardList){
            if(!tuple.y && tuple.x.cardType.equals("room"))
                roomlist.add(tuple.x);
        }
        return roomlist;
    }

    public String knowCard(Card card) {
        int index = cardList.indexOf(new Tuple<>(card,false));
        if(index != -1)
            return "false";
        else
            return "true";
    }
}
