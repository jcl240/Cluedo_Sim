package main;

public class Room {

    public final int[][] entranceTiles;
    public boolean hasSecretPassage;
    private Room secretPassage;
    public final String roomName;

    public Room(int[][] entrances, boolean hasSecretPassage, String roomName) {
        this.entranceTiles = entrances;
        this.hasSecretPassage = hasSecretPassage;
        this.roomName = roomName;
    }

    public void setSecretPassageway(Room secretPassage) {
        this.secretPassage = secretPassage;
        if(secretPassage.secretPassage != this) secretPassage.setSecretPassageway(this);
    }

    public static Room[] makeRooms(){
        Room[] rooms = new Room[9];
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
        return rooms;
    }

    public Room getSecretPassage() {
        return secretPassage;
    }
}
