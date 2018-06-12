package main;

public class Gamepiece {
    
    private int[] currentLocation;

    public Gamepiece(int[] location) {
        currentLocation = location;
    }

    public int[] getCurrentLocation(){
        return currentLocation;
    }

    public void setCurrentLocation(int[] newLocation) {
        currentLocation = newLocation;
    }
}
