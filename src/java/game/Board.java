import search.AStar;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Board {
    private final boolean[][] tiles = new boolean[24][25];
    private final Room[] rooms = new Room[9];
    private final Gamepiece[] gamepieces = new Gamepiece[4];

    /**
     * Constructor for Board
     */
    public Board() {
        initializeTiles();
        initializeRooms();
        initializePieces();
    }

    private void initializePieces() {
    }

    /**
     * Initialize the rooms and set the secret passages
     */
    private void initializeRooms() {
        rooms[0] = new Room(new int[][]{{6,3}},true, "study");
        rooms[1] = new Room(new int[][]{{6,8},{3,10}},false,"library");
        rooms[2] = new Room(new int[][]{{1,12},{5,15}},false,"billiardRoom");
        rooms[3] = new Room(new int[][]{{4,19}},true,"conservatory");
        rooms[4] = new Room(new int[][]{{8,19},{9,17},{14,17},{15,19}},false,"ballroom");
        rooms[5] = new Room(new int[][]{{19,18}},true,"kitchen");
        rooms[6] = new Room(new int[][]{{16,12},{17,9}},false,"diningRoom");
        rooms[7] = new Room(new int[][]{{17,5}},true,"lounge");
        rooms[8] = new Room(new int[][]{{11,6},{12,6},{9,4}},false, "hall");
        rooms[0].setSecretPassageway(rooms[5]);
        rooms[3].setSecretPassageway(rooms[7]);
    }

    /**
     * Main method for testing purposes
     * @param args
     */
    public static void main(String[] args){
        Board board = new Board();
    }

    /**
     * Defines the board tiles where trues are valid board tiles, includes doors
     */
    private void initializeTiles(){
        int[][] rowRanges = {
                {0,7,7},{0,16,16},
                {1,7,8},{1,15,16},
                {2,7,8},{2,15,16},
                {3,6,8},{3,15,16},
                {4,1,9},{4,15,16},
                {5,0,8},{5,15,17},
                {6,6,8},{6,15,22},{6,11,12},
                {7,7,23},
                {8,6,8},{8,14,22},
                {9,7,8},{9,14,15},{9,17,17},
                {10,6,8},{10,14,15},{10,3,3},
                {11,1,8},{11,14,15},
                {12,6,8},{12,14,16},{12,1,1},
                {13,6,8},{13,14,15},
                {14,6,8},{14,14,15},
                {15,5,18},
                {16,6,22},
                {17,1,7},{17,16,23},{17,9,9},{17,14,14},
                {18,0,7},{18,16,17},{18,19,19},
                {19,4,8},{19,15,17},
                {20,6,7},{20,16,17},
                {21,6,7},{21,16,17},
                {22,6,7},{22,16,17},
                {23,7,9},{23,14,16},
                {24,9,9},{24,14,14},
                                };
        for(int[] range: rowRanges) {
            setRange(range[0], range[1], range[2]);
        }
    }

    /**
     *  Helper method for setting a range of columns to true in a row
     * @param row
     * @param startCol
     * @param endCol
     */
    private void setRange(int row, int startCol, int endCol) {
        for(int i = startCol; i <= endCol; i++) this.tiles[i][row] = true;
    }
}

/*
 * Code for A* testing
 * int[] end = {9,24};
 * int[] start = {14,24};
 * AStar searcher = new AStar(start,end,tiles);
 * int success = searcher.search();
 * System.out.println(Arrays.deepToString(searcher.getFinalPath()));
 */