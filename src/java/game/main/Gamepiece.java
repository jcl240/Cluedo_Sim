package main;

public class Gamepiece {
    
    private int[] currentLocation;

    public Gamepiece(int[] location) {
        currentLocation = location;
    }

    public Gamepiece(Gamepiece y) {
        this.currentLocation = y.currentLocation.clone();
    }

    public int[] getCurrentLocation(){
        return currentLocation;
    }

    public void setCurrentLocation(int[] newLocation) {
        currentLocation = newLocation;
    }
}
