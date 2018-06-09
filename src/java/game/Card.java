import java.util.Random;

public class Card {

    public String cardType;
    public String cardName;

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
}
