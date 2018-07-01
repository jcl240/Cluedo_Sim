package GUI;

import agents.Action;
import agents.HumanAgent;
import agents.Player;
import main.Card;
import main.Cluedo;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

public class PlayerManager {

    private boolean takingTurn = false;
    private boolean canMove = false;
    HumanAgent humanPlayer;
    Cluedo game;
    PlayerPanel playerPanel;
    private LinkedList<Action> currentPossibleActions;
    LinkedList<int[]> secretPassageTiles = new LinkedList<>();

    public PlayerManager(Player humanPlayer, Cluedo game, PlayerPanel playerPanel){
        this.humanPlayer = (HumanAgent)humanPlayer;
        this.game = game;
        this.playerPanel = playerPanel;

        secretPassageTiles.add(new int[]{0,3});
        secretPassageTiles.add(new int[]{23,5});
        secretPassageTiles.add(new int[]{1,19});
        secretPassageTiles.add(new int[]{18,23});
    }

    public PlayerManager(Cluedo game, PlayerPanel playerPanel){
        this.game = game;
        this.playerPanel = playerPanel;

    }


    public void suggest(Card[] comboBoxSelections) {
        if(takingTurn){
            Action action = getAction("suggest");
            action.suggestion = comboBoxSelections;
            humanPlayer.setChosenAction(action);
        }
    }

    public void accuse(Card[] comboBoxSelections) {
        if(takingTurn){
            Action action = getAction("accuse");
            action.accusation = comboBoxSelections;
            humanPlayer.setChosenAction(action);
        }
    }

    public void falsify(Card falsifiedCard) {
        humanPlayer.setFalsifiedCard(falsifiedCard);
    }

    public Action getAction(String actionName){
        int actionIndex = currentPossibleActions.indexOf(new Action(actionName));
        return currentPossibleActions.get(actionIndex);
    }

    public void clickedTile(int x, int y){
        if(takingTurn && canMove && isSecretPassage(x,y) && currentPossibleActions.contains(new Action("useSecretPassage"))){
            Action secretAction = getAction("useSecretPassage");
            humanPlayer.setChosenAction(secretAction);
        }
        else if(takingTurn && canMove){
            Action moveAction = getAction("move");
            moveAction.towards= new int[]{x,y};
            humanPlayer.setChosenAction(moveAction);
        }
    }

    private boolean isSecretPassage(int x, int y) {
        for(int[] coord: secretPassageTiles){
            if(x == coord[0] && y == coord[1]){
                return true;
            }
        }
        return false;
    }

    public void next() {
        game.doneUpdating();
    }


    /**
     *  Ends the player's turn by prompting the game
     */
    public void doneViewingCard(){
        humanPlayer.doneViewingCard();
    }

    public void updateInfo(Action actionTaken, Player currentPlayer) {
        boolean humanShownCard = false;
        boolean isHumanActing = currentPlayer.equals(humanPlayer);
        if(actionTaken.actionType.equals("showCard")) {
            if(actionTaken.shownTo.equals(humanPlayer))
                humanShownCard =true;
        }
        if(!actionTaken.actionType.equals("accuse") && (isHumanActing || humanShownCard))
            next();
        else{
            playerPanel.passAction(actionTaken, currentPlayer);
        }
    }

    public void takeTurn(LinkedList<Action> possibleActions, int[] currentLocation) {
        playerPanel.infoActionPanel.setTakingTurn();
        currentPossibleActions = possibleActions;
        takingTurn = true;
        initializeMoveAction(possibleActions);
        initializeSuggestAndAccuse(possibleActions);

    }

    private void initializeSuggestAndAccuse(LinkedList<Action> possibleActions) {
        playerPanel.infoActionPanel.setAccuseButton(possibleActions.contains(new Action("accuse")));
        playerPanel.infoActionPanel.setSuggestButton(possibleActions.contains(new Action("suggest")));
    }

    private void initializeMoveAction(LinkedList<Action> possibleActions) {
        int moveIndex = possibleActions.indexOf(new Action("move"));
        if(moveIndex != -1) {
            canMove = true;
            playerPanel.infoActionPanel.setRollLabel(possibleActions.get(moveIndex).roll);
        }
        else{
            canMove = false;
            playerPanel.infoActionPanel.hideRollLabel();
        }
    }
}
