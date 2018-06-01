public class Room {

    public final int[][] entranceTiles;
    private boolean hasSecretPassage;
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
}
