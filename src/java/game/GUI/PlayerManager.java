package GUI;

import agents.Action;
import agents.Player;
import main.Cluedo;

import java.util.LinkedList;

public class PlayerManager {

    private boolean takingTurn = false;
    private boolean canMove = false;
    Player humanPlayer;
    Cluedo game;
    PlayerPanel playerPanel;

    public PlayerManager(Player humanPlayer, Cluedo game, PlayerPanel playerPanel){
        this.humanPlayer = humanPlayer;
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
        takingTurn = true;
        if(possibleActions.contains(new Action("move")))
            canMove = true;
    }
}
