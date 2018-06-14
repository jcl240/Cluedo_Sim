package GUI;

import agents.Action;
import agents.HumanAgent;
import agents.Player;
import main.Cluedo;

import java.util.LinkedList;

public class PlayerManager {

    private boolean takingTurn = false;
    private boolean canMove = false;
    HumanAgent humanPlayer;
    Cluedo game;
    PlayerPanel playerPanel;
    private LinkedList<Action> currentPossibleActions;

    public PlayerManager(Player humanPlayer, Cluedo game, PlayerPanel playerPanel){
        this.humanPlayer = (HumanAgent)humanPlayer;
        this.game = game;
        this.playerPanel = playerPanel;

    }


    public void suggest() {
    }

    public void accuse() {
    }

    public void falsify() {
    }

    public void clickedTile(int x, int y){
        if(takingTurn && canMove){
            int moveIndex = currentPossibleActions.indexOf(new Action("move"));
            Action moveAction = currentPossibleActions.get(moveIndex);
            moveAction.towards= new int[]{x,y};
            humanPlayer.setChosenAction(moveAction);
        }
    }

    public void next() {
        game.doneUpdating();
    }


    /**
     *  Ends the player's turn by prompting the game
     */
    public void doneViewingCard(){

    }

    public void updateInfo(Action actionTaken, Player currentPlayer) {
        playerPanel.passAction(actionTaken, currentPlayer);
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
