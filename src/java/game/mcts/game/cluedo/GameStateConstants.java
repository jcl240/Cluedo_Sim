/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mcts.game.cluedo;

import mcts.game.catan.HexTypeConstants;

import java.awt.*;

/**
 *
 * @author szityu, sorinMD
 */
public interface GameStateConstants extends HexTypeConstants {

    int NPLAYERS = 4;

    int MOVE = 1;
    int SECRET_PASSAGE = 2;
    int SUGGEST = 3;
    int FALSIFY = 4;
    int ACCUSE = 5;

    int NO_ROOM = 0;
    int HALL = 1;
    int LOUNGE = 2;
    int DINING_ROOM = 3;
    int KITCHEN = 4;
    int BALL_ROOM = 5;
    int CONSERVATORY = 6;
    int BILLIARD_ROOM = 7;
    int LIBRARY = 8;
    int STUDY = 9;

    int MUSTARD = 1;
    int PLUM = 2;
    int GREEN = 3;
    int PEACOCK = 4;
    int SCARLET = 5;
    int WHITE = 6;

    int KNIFE = 1;
    int CANDLESTICK = 2;
    int REVOLVER = 3;
    int ROPE = 4;
    int LEAD_PIPE = 5;
    int WRENCH = 6;


    /* 0: Playing or winner's index, 1: current player's index
       2: current player accused, 3: current player justMoved
       4: current player hasSuggested, 5: current player's room
       6: current roll
    */
    int GAME_STATE = 0;
    int CURRENT_PLAYER = 1;
    int HAS_ACCUSED = 2;
    int JUST_MOVED = 3;
    int HAS_SUGGESTED = 4;
    int CURRENT_ROOM = 5;
    int CURRENT_ROLL = 6;

    public final static Color[] playerColor = 
    {
        Color.BLUE,
        Color.GREEN,
        Color.RED,
        Color.YELLOW,
    };
    
}
