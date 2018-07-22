package mcts.game.cluedo;

import agents.Action;
import agents.HeuristicAgent;
import main.Board;
import main.Card;
import main.Room;
import mcts.game.catan.Actions;
import mcts.utils.Options;

import java.util.LinkedList;

public class HeuristicMCTS implements GameStateConstants{

    private HeuristicAgent agent;
    int[] state;
    public LinkedList<Integer> actionTypes;
    private CluedoBelief belief;

    public HeuristicMCTS(HeuristicAgent agent, int[] state, CluedoBelief belief){
        this.agent = agent;
        this.state = state.clone();
        actionTypes = new LinkedList<>();
        this.belief = belief;
    }

    public Options listPossiblities(boolean sample) {
        Options options = new Options();
        if(state[CURRENT_ROOM] != -1)
            options=options;
        if(state[JUST_MOVED] == 0) {
            returnMoveAction(options);
            if(belief.knowEnvelope()) {
                returnAccuseAction(options);
            }
            if (inRoomWithSecretPassage(state[CURRENT_ROOM])) {
                returnSecretPassageAction(options);
            }
        }
        if(state[CURRENT_ROOM] != -1 && state[HAS_SUGGESTED] == 0) {
            returnSuggestionAction(options);
        }
        return options;
    }

    private void returnSuggestionAction(Options options) {
        int[] cardSuggestion = belief.getInformedSuggestion(state[CURRENT_ROOM]);
        int suspect = cardSuggestion[1];
        int weapon = cardSuggestion[2];
        options.put(Actions.newAction(SUGGEST, state[CURRENT_ROOM], suspect, weapon), 1.0);
        actionTypes.add(SUGGEST);
    }

    private void returnSecretPassageAction(Options options) {
        options.put(Actions.newAction(SECRET_PASSAGE, state[CURRENT_ROOM]), 1.0);
        actionTypes.add(SECRET_PASSAGE);
    }

    private void returnAccuseAction(Options options) {
        while(actionTypes.size()>0) {
            actionTypes.remove();
        }
        options.removeAllExceptType(ACCUSE);
        int[] accusation = belief.getMostLikelySolution();
        options.put(Actions.newAction(ACCUSE, accusation[0], accusation[1], accusation[2]), 1.0);
        actionTypes.add(ACCUSE);
    }

    private void returnMoveAction(Options options) {
        int roomIdx;
        if(agent.movementGoal != null){
            roomIdx = agent.movementGoal[0];
        }
        else {
            roomIdx = belief.getHighestEntropyRoom();
        }
        agent.movementGoal = new int[]{roomIdx};
        options.put(Actions.newAction(MOVE,roomIdx,state[CURRENT_ROLL]),1.0);
        actionTypes.add(MOVE);
    }

    private boolean inRoomWithSecretPassage(int i) {
        if(i == STUDY || i == LOUNGE || i == KITCHEN || i == CONSERVATORY){
            return true;
        }
        else
            return false;
    }

    private int getRoomIndex(String roomName){
        int roomIdx = -1;
        switch(roomName){
            case "study":
                roomIdx = STUDY;
                break;
            case "lounge":
                roomIdx = LOUNGE;
                break;
            case "kitchen":
                roomIdx = KITCHEN;
                break;
            case "billiardroom":
                roomIdx = BILLIARD_ROOM;
                break;
            case "ballroom":
                roomIdx = BALL_ROOM;
                break;
            case "conservatory":
                roomIdx = CONSERVATORY;
                break;
            case "library":
                roomIdx = LIBRARY;
                break;
            case "hall":
                roomIdx = HALL;
                break;
            case "diningroom":
                roomIdx = DINING_ROOM;
                break;
        }
        return roomIdx;
    }

}
