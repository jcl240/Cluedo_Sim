package agents;

import main.Card;
import main.Tuple;

import java.util.LinkedList;

public class Notebook {

    LinkedList<Tuple<Card, Boolean>> notebook = new LinkedList<>();
    
    public Notebook(Card[] startingHand){
        for(Card card: startingHand){
            checkOffCard(card);
        }
    }

    public void checkOffCard(Card card) {

    }

    public int unknownCardCount() {

        return 1;
    }
}
