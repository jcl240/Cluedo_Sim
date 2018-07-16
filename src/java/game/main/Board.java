package main;

import GUI.BoardGUI;
import agents.Action;
import agents.Agent;
import agents.Player;
import agents.RandomAgent;
import mcts.game.cluedo.GameStateConstants;
import search.AStar;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;

public class Board implements GameStateConstants {


    private final boolean[][] tiles = new boolean[24][25];
    private Room[] rooms = new Room[9];
    private LinkedList<Tuple<Player,Gamepiece>> playerPieceTuples = new LinkedList<>();
    private int[][] startingLocations = new int[][]{{0,5},{9,24},{23,7},{16,0}};
    private BoardGUI boardGUI;
    public Card[] faceUp;

    /**
     * Constructor for main.Board
     */
    public Board(Player[] players, BoardGUI boardGUI, Card[] faceUp) {
        initializeTiles();
        initializeRooms();
        initializePieces(players);
        this.boardGUI = boardGUI;
        this.faceUp = faceUp;
    }
    public Board(){}

    public Board(Board board) {
        initializeTiles();
        initializeRooms();
        this.playerPieceTuples = board.getTuples();
        this.faceUp = board.faceUp;
    }

    public Board(int[][] playerLocations) {
        initializeTiles();
        initializeRooms();
        initializePieces(playerLocations);
    }

    private void initializePieces(int[][] playerLocations) {
        int i = 1;
        for(int[] location: playerLocations){
            playerPieceTuples.add(new Tuple<>(new RandomAgent(i), new Gamepiece(location)));
            i++;
        }
    }

    public LinkedList<Tuple<Player,Gamepiece>> getTuples() {
        LinkedList<Tuple<Player,Gamepiece>> newList = new LinkedList<>();
        for(Tuple<Player,Gamepiece> tuple: playerPieceTuples){
            Agent agent = (Agent)tuple.x;
            Player player = new RandomAgent(agent.getHandArray().clone(),faceUp.clone(),agent.playerIndex-1,agent);
            int x = tuple.y.getCurrentLocation()[0];
            int y = tuple.y.getCurrentLocation()[1];
            Gamepiece piece = new Gamepiece(new int[]{x,y});
            newList.add(new Tuple<Player,Gamepiece>(player,piece));
        }
        return newList;
    }

    private void initializePieces(Player[] players) {
        int i = 0;
        for(Player player: players){
            playerPieceTuples.add(new Tuple<>(player, new Gamepiece(startingLocations[i])));
            i++;
        }
    }

    /**
     * Initialize the rooms and set the secret passages
     */
    private void initializeRooms() {
        rooms = Room.makeRooms();
    }


    /**
     * Defines the board tiles where trues are valid board tiles, includes doors
     */
    private void initializeTiles(){
        int[][] rowRanges = {
                {0,7,7},{0,16,16},
                {1,7,8},{1,15,16},
                {2,7,8},{2,15,16},
                {3,0,0},{3,6,8},{3,15,16},
                {4,1,9},{4,15,16},
                {5,0,8},{5,15,17},{5,23,23},
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
                {19,1,1},{19,4,8},{19,15,17},
                {20,6,7},{20,16,17},
                {21,6,7},{21,16,17},
                {22,6,7},{22,16,17},
                {23,7,9},{23,14,16},{23,18,18},
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

    public boolean[][] getTiles() {
        return tiles;
    }

    public Boolean movePlayer(Action actionTaken, Player currentPlayer) {
        int[] start = getPlayerLocation(currentPlayer);
        int[] end = actionTaken.towards;
        AStar astar = new AStar(start,end,getCurrentTiles(currentPlayer));
        Boolean successful = astar.search();
        int[][] path = astar.getFinalPath();
        if(successful) {
            if (path.length > actionTaken.roll)
                movePiece(currentPlayer, path[actionTaken.roll]);
            else
                movePiece(currentPlayer, path[path.length-1]);
        }
        return successful;
    }

    public Boolean movePlayer(int[] action, int playerIndex) {
        Player currentPlayer = getPlayer(playerIndex);
        Action act = new Action("move",action[2]);
        act.towards = getRoomByAction(action, playerIndex);
        return movePlayer(act, (Player)currentPlayer);
    }

    private Player getPlayer(int playerIndex) {
        for(Tuple<Player,Gamepiece> tuple: playerPieceTuples){
            if(((Agent)tuple.x).playerIndex == playerIndex){
                return tuple.x;
            }
        }
        return null;
    }

    private int[] getRoomByAction(int[] action, int playerIndex) {
        int[] playerPosition = getPlayerLocation(getPlayer(playerIndex));
        int[] location;
        location = getClosestRoomEntrance(action[1], playerPosition);
        return location;
    }

    public int[] getClosestRoomEntrance(int i, int[] playerPosition) {
        String roomName = "";
        switch(i){
            case STUDY:
                roomName = "study";
                break;
            case LOUNGE:
                roomName = "lounge";
                break;
            case KITCHEN:
                roomName = "kitchen";
                break;
            case BILLIARD_ROOM:
                roomName = "billiardroom";
                break;
            case BALL_ROOM:
                roomName = "ballroom";
                break;
            case CONSERVATORY:
                roomName = "conservatory";
                break;
            case LIBRARY:
                roomName = "library";
                break;
            case HALL:
                roomName = "hall";
                break;
            case DINING_ROOM:
                roomName = "diningroom";
                break;
        }
        for(Room room: rooms){
            if(room.roomName.equals(roomName)){
                int[] closest = new int[]{};
                double closestDistance = 1000;
                for(int[] entrance: room.entranceTiles){
                    double distance = getDistance(entrance, playerPosition);
                    if(distance < closestDistance){
                        closestDistance = distance;
                        closest = entrance;
                    }
                }
                return closest;
            }
        }
        return new int[]{};
    }

    private double getDistance(int[] entrance, int[] playerPosition) {
        return Math.sqrt(Math.pow(entrance[0]-playerPosition[0],2)+Math.pow(entrance[1]-playerPosition[1],2));
    }


    public void movePiece(Player currentPlayer, int[] newLocation) {
        for(Tuple<Player, Gamepiece> tuple: playerPieceTuples) {
            if(tuple.x.equals(currentPlayer)) tuple.y.setCurrentLocation(newLocation);
        }
    }

    private boolean[][] getCurrentTiles(Player currentPlayer) {
        boolean[][] currentTiles = getTilesCopy();
        int[] playerLocation = getPlayerLocation(currentPlayer);
        int[][] playerLocations = getPlayerLocations();
        for(int[] location: playerLocations){
            int x = location[0];
            int y = location[1];
            if(!isRoomTile(location) && !Arrays.equals(location, playerLocation))
                currentTiles[x][y] = false;
        }
        return currentTiles;
    }

    private boolean[][] getTilesCopy() {
        boolean [][] copy = new boolean[24][25];
        for(int x = 0; x < tiles.length; x++){
            copy[x] = tiles[x].clone();
        }
        return copy;
    }

    private boolean isRoomTile(int[] location) {
        Room room = getRoomByLocation(location);
        return !(room == null);
    }


    public boolean inRoomWithSecretPassage(Player currentPlayer) {
        int[] playerLocation = getPlayerLocation(currentPlayer);
        Room room = getRoomByLocation(playerLocation);
        if(room == null) return false;
        return (room.hasSecretPassage);
    }

    public int[] getPlayerLocation(Player currentPlayer) {
        for(Tuple<Player, Gamepiece> tuple:playerPieceTuples){
            if((tuple.x).equals(currentPlayer)){
                return tuple.y.getCurrentLocation();
            }
        }
        return null;
    }

    public boolean inRoom(Player currentPlayer) {
        int[] playerLocation = getPlayerLocation(currentPlayer);
        Room room = getRoomByLocation(playerLocation);
        return (room != null);
    }

    public Room getRoomByLocation(int[] playerLocation) {
        for(Room room:rooms){
            for(int[] location:room.entranceTiles){
                if(Arrays.equals(playerLocation,location)) return room;
            }
        }
        return null;
    }

    public Room getRoom(Player currentPlayer) {
        int[] playerLocation = getPlayerLocation(currentPlayer);
        Room room = getRoomByLocation(playerLocation);
        return room;
    }

    public int[][] getPlayerLocations() {
        int[][] locations = new int[4][2];
        int i = 0;
        for(Tuple<Player, Gamepiece> tuple:playerPieceTuples){
            locations[i] = tuple.y.getCurrentLocation();
            i++;
        }
        return locations;
    }

    public int[] getSecretPassage(Player currentPlayer) {
        Room nextRoom = getRoom(currentPlayer).getSecretPassage();
        return nextRoom.entranceTiles[0];
    }

    public void useSecretPassage(Action actionTaken, Player currentPlayer, boolean useGUI) {
        movePiece(currentPlayer, actionTaken.towards);
    }

    public void useSecretPassage(int[] a, int i) {
        movePiece(getPlayer(i), getSecretPassageByAction(a, i));
    }

    private int[] getSecretPassageByAction(int[] a, int playerIndex) {
        int[] room = getRoomByAction(a, playerIndex);
        Room nextRoom = getRoomByLocation(room).getSecretPassage();
        return nextRoom.entranceTiles[Cluedo.rand.nextInt(nextRoom.entranceTiles.length)];
    }

    public boolean inRoom(int playerIndex) {
        Player player = getPlayer(playerIndex);
        return inRoom(player);
    }

    public int getRoom(int playerIndex) {
        Room room = getRoom(getPlayer(playerIndex));
        int roomIdx = 0;
        if(room == null)
            return roomIdx;
        switch(room.roomName){
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

    public void setTuples(int[] locations) {
        int i = 0;
        for(Tuple<Player, Gamepiece> tuple: playerPieceTuples){
            tuple.y.setCurrentLocation(new int[]{locations[i*2],locations[i*2+1]});
            i++;
        }
    }

    public int getRoomIndexByLocation(int[] towards) {
        Room room = getRoomByLocation(towards);
        int roomIdx = 0;
        switch(room.roomName){
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

    public boolean canMove(int playerIdx, int roomIdx) {
        Player currentPlayer = getPlayer(playerIdx);
        int[] start = getPlayerLocation(currentPlayer);
        int[] entrance = getClosestRoomEntrance(roomIdx, start);
        AStar astar = new AStar(start,entrance,getCurrentTiles(currentPlayer));
        Boolean successful = astar.search();
        return successful;
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