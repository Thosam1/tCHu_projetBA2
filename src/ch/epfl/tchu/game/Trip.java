package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

public final class Trip {
    private final Station from;
    private final Station to;
    private final int points;
    
    public Trip(Station from, Station to, int points) {
            this.from = Objects.requireNonNull(from);
            this.to = Objects.requireNonNull(to);
            Preconditions.checkArgument(points > 0);
            this.points = points;
    }
    
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument(points > 0);
        if ((from.isEmpty())&&(to.isEmpty())){
            throw new IllegalArgumentException();
        }
        ArrayList<Trip> output = new ArrayList<Trip>();
        for (Station start : from) {
            for (Station destination : to) {
                output.add(new Trip(start, destination, points));
            }
        }
        return output;
    }
    
    //Utile pour le deuxième constructeur de ticket
    //créé une liste avec un seul trip
    public static List<Trip> simpleTrip(Station from, Station to, int points){
        ArrayList<Trip> output = new ArrayList<Trip>();
        output.add(new Trip(from, to, points));
        return output;
    }
    
    public Station from() {
        return from;
    }
    
    public Station to() {
        return to;
    }
    
    public int points() {
        return points;
    }
    public int points(StationConnectivity connectivity) {
        if(connectivity.connected(from, to)) {
            return points;
        }
        else return -points;
    }
}
