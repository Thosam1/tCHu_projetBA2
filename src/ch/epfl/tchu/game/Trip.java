package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
/**
 * 
 * @author Aymeric de chillaz (326617)
 *
 */

public final class Trip {   // immuable
    private final Station from;
    private final Station to;
    private final int points;
    /**
     * construit un Trip qui est un trajet entre deux Stations. (celles ci ne doivent pas etre null et les points doivent etre positif)
     * @param from
     *          Station de départ
     * @param to
     *          Station d arrivée
     * @param points
     *          Points associés à ce trajet
     */
    public Trip(Station from, Station to, int points) {
            this.from = Objects.requireNonNull(from);
            this.to = Objects.requireNonNull(to);
            Preconditions.checkArgument(points > 0);
            this.points = points;
    }
    
    /**
     * créé une liste de Trip qui valent autant de points
     * @param from
     *          Stations de départ
     * @param to
     *          Stations d'arrivée
     * @param points
     *          points associé à tous ces Trip
     * @return
     *          Liste contenant des Trips (from.size() * to.size() Trips pour etre exact)
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument(points > 0 && !from.isEmpty() && !to.isEmpty());    // j'ai rajouté les deux dedans si ça joue pour toi
//        if ((from.isEmpty())&&(to.isEmpty())){
//            throw new IllegalArgumentException();
//        }
        ArrayList<Trip> output = new ArrayList<Trip>();
        for (Station start : from) {
            for (Station destination : to) {
                output.add(new Trip(start, destination, points));
            }
        }
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
    
    /**
     * méthode qui permet de savoir combien de points le joueur perd ou gagne sur ce ticket
     * 
     * @param connectivity 
     *          permet d appeler la méthode connected avec les attributs from et to
     * @return 
     *          points si les deux stations sont connectées et -points sinon
     */
    public int points(StationConnectivity connectivity) {
        if(connectivity.connected(from, to)) {
            return points;
        }
        else return -points;
    }
}
