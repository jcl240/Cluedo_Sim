package agents;

import main.Card;
import main.Cluedo;
import main.Room;

import java.util.Arrays;
import java.util.LinkedList;

import static main.Card.shuffle;

public class RandomAgent extends  Agent implements Player {

    private int[] movementGoal;
    private int actionFailCount = 0;

    public RandomAgent(Card[] hand, Card[] faceUp, int index) {
        super(hand, faceUp, index,"Random");
    }

    @Override
    public void endTurn(){
        this.justMoved = false;
        actionFailCount = 0;
    }

    public Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation){
        Action actionTaken;
        if(actionFailCount == 10){
            return(new Action("doNothing"));
        }
        if(Arrays.equals(currentLocation,movementGoal))
            movementGoal = null;
        if(possibleActions.contains(new Action("accuse")) && notebook.unknownCardCount() == 3)
            return(new Action("accuse", notebook.getAccusation()));
        else {
            possibleActions.remove(new Action("accuse"));
            actionTaken = possibleActions.get(Cluedo.rand.nextInt(possibleActions.size()));
        }
        return decideAction(actionTaken);
    }

    private Action decideAction(Action actionTaken) {
        if(actionTaken.actionType.equals("move")){
            if(movementGoal != null)
                actionTaken.towards = movementGoal;
            else {
                Room[] rooms = Room.makeRooms();
                Room randRoom = rooms[Cluedo.rand.nextInt(rooms.length)];
                LinkedList<Card> ukr = getUnknownRooms();
                actionTaken.towards = movementGoal =randRoom.entranceTiles[0];
            }
        }
        else if(actionTaken.actionType.equals("suggest")){
            actionTaken.suggestion = notebook.getRandomSuggestion(actionTaken.currentRoom);
            logSuggestion(actionTaken.suggestion);
        }
        return actionTaken;
    }

    @Override
    public Card falsifySuggestion(Player player, Card[] suggestion) {
        suggestion = shuffle(suggestion);
        for(Card suggestedCard: suggestion){
            for(Card myCard: this.hand){
                if(suggestedCard.equals(myCard)) {
                    return myCard;
                }
            }
        }
        return null;
    }

    @Override
    public void showCard(Player player, Card cardToShow) {
        LinkedList<Card> ukr = getUnknownRooms();
        notebook.checkOffCard(cardToShow);
    }

    @Override
    public void actionFailed(Action actionTaken) {
        actionFailCount++;
        if(actionTaken.actionType.equals("move"))
            movementGoal = null;
    }

    private void logSuggestion(Card[] suggestion){
        int numKnown = 1;
        LinkedList<String> suggestionList = new LinkedList<>();
        for(Card card: suggestion){
            suggestionList.add(card.cardName);
            suggestionList.add(notebook.knowCard(card));
        }
        playerLog.logSuggestion(numKnown, suggestionList);
    }

    public LinkedList<Card> getUnknownRooms() {
        return notebook.getUnknownRooms();
    }
}
