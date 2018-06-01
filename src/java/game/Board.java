import search.AStar;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Board {
    private final boolean[][] tiles = new boolean[24][25];

    public Board() {
        initializeTiles();
        //System.out.println(Arrays.deepToString(tiles).replaceAll("],", "]," + System.getProperty("line.separator")));
        int[] end = {9,24};
        int[] start = {14,24};
        AStar searcher = new AStar(start,end,tiles);
        int success = searcher.search();
        System.out.println(Arrays.deepToString(searcher.getFinalPath()));
    }

    public static void main(String[] args){
        Board board = new Board();
    }

    private void initializeTiles(){
        int[][] rowRanges = {
                {0,7,7},{0,16,16},
                {1,7,8},{1,15,16},
                {2,7,8},{2,15,16},
                {3,7,8},{3,15,16},
                {4,1,8},{4,15,16},
                {5,0,8},{5,15,16},
                {6,6,8},{6,15,22},
                {7,7,23},
                {8,7,8},{8,14,22},
                {9,7,8},{9,14,15},
                {10,6,8},{10,14,15},
                {11,1,8},{11,14,15},
                {12,6,8},{12,14,15},
                {13,6,8},{13,14,15},
                {14,6,8},{14,14,15},
                {15,6,18},
                {16,6,22},
                {17,1,7},{17,16,23},
                {18,0,7},{18,16,17},
                {19,5,7},{19,16,17},
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

    private void setRange(int row, int startCol, int endCol) {
        for(int i = startCol; i <= endCol; i++) this.tiles[i][row] = true;
    }
}
