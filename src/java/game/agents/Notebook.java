package agents;

import main.Card;
import main.Tuple;

import java.util.LinkedList;

public class Notebook {

    LinkedList<Tuple<Card, Boolean>> notebook = new LinkedList<>();

    public Notebook(){
        initializeNotebook();
        checkOffCard(new Card("weapon","knife"));
        
    }

    public Notebook(Card[] startingHand){
        initializeNotebook();
        for(Card card: startingHand){
            checkOffCard(card);
        }
    }

    private void initializeNotebook() {
        Card[] deck = Card.makeCards();
        for(Card card:deck){
            notebook.add(new Tuple<>(card, false));
        }
    }

    public void checkOffCard(Card card) {
        int index = notebook.indexOf(new Tuple<>(card,false));
        if(index != -1)
            notebook.get(index).y = true;

    }

    public int unknownCardCount() {
        int count = 0;
        for(Tuple<Card, Boolean> tuple: notebook){
            if(!tuple.y)
                count++;
        }
        return count;
    }

    public Card[] getAccusation() {
        return new Card[3];
    }

    public static void main(String[] args){
        Notebook nb = new Notebook();
    }
}
