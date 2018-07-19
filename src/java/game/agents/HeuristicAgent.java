package agents;

import main.Card;
import main.Cluedo;
import main.Room;

import java.util.Arrays;
import java.util.LinkedList;

import static main.Card.shuffle;

public class HeuristicAgent extends Agent implements Player{

    public int[] movementGoal;
    private int actionFailCount = 0;
    private HeuristicNotebook notebook;

    public HeuristicAgent(Card[] hand, Card[] faceUp, int index) {
        super(hand, index,"Heuristic");
        notebook = new HeuristicNotebook(hand, playerIndex);
        for(Card card: faceUp) {
            notebook.checkOffCard(card, -1);
        }
    }

    public HeuristicAgent(int i, double[][] probabilities, int[] movementGoal, int MCTSidx) {
        super(i);
        notebook = new HeuristicNotebook(probabilities, i, MCTSidx);
        this.movementGoal = movementGoal;
    }

    @Override
    public void endTurn(){
        this.justMoved = false;
        actionFailCount = 0;
    }

    public Action takeTurn(LinkedList<Action> possibleActions, int[] currentLocation){
        Action actionTaken;
        Card[] solution = notebook.checkForSolution();
        if(actionFailCount == 10){
            return(new Action("doNothing"));
        }
        if(Arrays.equals(currentLocation,movementGoal))
            movementGoal = null;
        if(possibleActions.contains(new Action("accuse")) && (solution != null))
            return(new Action("accuse", solution));
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
                String roomName = notebook.getHighestEntropyRoom();
                Room room = getRoomByName(roomName);
                actionTaken.towards = movementGoal = room.entranceTiles[0];
            }
        }
        else if(actionTaken.actionType.equals("suggest")){
            actionTaken.suggestion = notebook.getInformedSuggestion(actionTaken.currentRoom);
            logSuggestion(actionTaken.suggestion);
        }
        return actionTaken;
    }

    public Room getRoomByName(String roomName) {
        Room[] rooms = Room.makeRooms();
        for(Room room: rooms){
            if(room.roomName.equals(roomName))
                return room;
        }
        return null;
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
        int playerIndex = ((Agent)player).playerIndex;
        notebook.checkOffCard(cardToShow, playerIndex);
    }

    @Override
    public void actionFailed(Action actionTaken) {
        actionFailCount++;
        if(actionTaken.actionType.equals("move"))
            movementGoal = null;
    }

    @Override
    public void noCardToShow(Action actionTaken, Player player) {
        for(Card card:actionTaken.suggestion){
            notebook.setProbabilityZero(card, ((Agent)player).playerIndex);
        }
    }

    @Override
    public void cardShown(Action action, Player cardPlayer) {
        notebook.updateProbabilities(action.suggestion, ((Agent)cardPlayer).playerIndex);
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

    public double[][] getCurrentProbabilities() {
        return notebook.getProbabilities();
    }

    public HeuristicNotebook getNotebook() {
        return this.notebook;
    }
}

