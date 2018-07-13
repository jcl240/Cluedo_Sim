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
    int PLAYING = 1;

    int ROOM = 1;
    int SUSPECT = 2;
    int WEAPON = 3;

    int CHOOSE_DICE = 0;
    int MOVE = 1;
    int SECRET_PASSAGE = 2;
    int SUGGEST = 3;
    int FALSIFY = 4;
    int ACCUSE = 5;
    int NO_FALSIFY = 6;

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


    /* 0: Playing or winner's index, 1: current player's index,
    2: current player justMoved, 3: current player's room,
    4: current roll, 5: current player is falsifying card,
    6: has suggested
   7: current suggested room, 8: current suggested suspect,
   9: current suggested weapon, 10: suggester index,
   11: player one accused, 12: player two accused,
   13: player three accused, 14: player four accused
*/
    int GAME_STATE = 0;
    int CURRENT_PLAYER = 1;
    int JUST_MOVED = 2;
    int CURRENT_ROOM = 3;
    int CURRENT_ROLL = 4;
    int FALSIFYING = 5;
    int HAS_SUGGESTED = 6;
    int SUGGESTED_ROOM = 7;
    int SUGGESTED_SUSPECT = 8;
    int SUGGESTED_WEAPON = 9;
    int SUGGESTER_IDX = 10;
    int PLAYER_ONE_ACCUSED = 11;
    int PLAYER_TWO_ACCUSED = 12;
    int PLAYER_THREE_ACCUSED = 13;
    int PLAYER_FOUR_ACCUSED = 14;
    int ENTROPY = 15;
    int WINNER =16;
    int PLAYER_ONE_X = 17;
    int PLAYER_ONE_Y = 18;
    int PLAYER_TWO_X = 19;
    int PLAYER_TWO_Y = 20;
    int PLAYER_THREE_X = 21;
    int PLAYER_THREE_Y = 22;
    int PLAYER_FOUR_X = 23;
    int PLAYER_FOUR_Y = 24;
    int TURN = 25;
    int ACCUSED_OFFSET = 11;
    int LOCATION_OFFSSET = 17;

    public final static Color[] playerColor = 
    {
        Color.BLUE,
        Color.GREEN,
        Color.RED,
        Color.YELLOW,
    };
    
}
