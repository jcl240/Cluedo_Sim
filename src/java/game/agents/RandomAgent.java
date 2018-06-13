package agents;

import main.Card;
import main.Room;
import search.AStar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class RandomAgent extends  Agent implements Player {

    private int[] movementGoal;

    public RandomAgent(Card[] hand, Card[] faceUp, int index) {
        super(hand, faceUp, index);
    }

    @Override
    public void endTurn(){
        this.justMoved = false;
    }

    public Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation){
        Action actionTaken;
        if(Arrays.equals(currentLocation,movementGoal))
            movementGoal = null;
        if(possibleActions.contains(new Action("accuse")) && notebook.unknownCardCount() == 3)
            return(new Action("accuse", notebook.getAccusation()));
        else {
            possibleActions.remove(new Action("accuse"));
            actionTaken = possibleActions.get(new Random().nextInt(possibleActions.size()));
        }
        return decideAction(actionTaken);
    }

    private Action decideAction(Action actionTaken) {
        if(actionTaken.actionType.equals("move")){
            if(movementGoal != null)
                actionTaken.towards = movementGoal;
            else {
                Room[] rooms = Room.makeRooms();
                Room randRoom = rooms[new Random().nextInt(rooms.length)];
                actionTaken.towards = movementGoal =randRoom.entranceTiles[0];
            }
        }
        else if(actionTaken.actionType.equals("suggest")){
            actionTaken.suggestion = notebook.getRandomSuggestion(actionTaken.currentRoom);
        }
        return actionTaken;
    }

    @Override
    public Card falsifySuggestion(Card[] suggestion) {
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
    public void showCard(Card cardToShow) {
        notebook.checkOffCard(cardToShow);
    }

    @Override
    public String[] getHand() {
        String[] stringHand = new String[4];
        int i = 0;
        for(Card card: hand) {
            stringHand[i] = card.cardName;
            i++;
        }
        return stringHand;
    }
}
