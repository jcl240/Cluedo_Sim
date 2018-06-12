package agents;

import main.Card;
import main.Room;
import search.AStar;

import java.util.LinkedList;
import java.util.Random;

public class RandomAgent extends  Agent implements Player {

    public RandomAgent(Card[] hand, Card[] faceUp) {
        super(hand, faceUp);
    }

    @Override
    public void endTurn(){
        this.justMoved = false;
    }

    public Action takeTurn(LinkedList<Action> possibleActions){
        Action actionTaken;
        if(possibleActions.contains(new Action("accuse")) && notebook.unknownCardCount() == 3)
            return(new Action("accuse", notebook.getAccusation()));
        else
            actionTaken = possibleActions.get(new Random().nextInt(possibleActions.size()));
        return decideAction(actionTaken);
    }

    private Action decideAction(Action actionTaken) {
        if(actionTaken.actionType.equals("move")){
            Room[] rooms = Room.makeRooms();
            Room randRoom = rooms[new Random().nextInt(rooms.length)];
            actionTaken.towards = randRoom.entranceTiles[0];
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
                if(suggestedCard == myCard) {
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
}
