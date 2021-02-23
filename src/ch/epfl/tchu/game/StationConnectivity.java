package ch.epfl.tchu.game;

public interface StationConnectivity {
    public abstract boolean connected(Station s1, Station s2);  // return true iff s1 and s2 are connected by the player

}
