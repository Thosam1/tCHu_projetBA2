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

public final class Trip {
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
     * @throws IllegalArgumentException si les points sont égaux à 0 ou négatifs
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
        Preconditions.checkArgument(points > 0 && !from.isEmpty() && !to.isEmpty());

        ArrayList<Trip> output = new ArrayList<Trip>();

        for (Station start : from) {
            for (Station destination : to) {
                output.add(new Trip(start, destination, points));
            }
        }
        return output;
    }
    
    /**
     * getter de l'attribut from qui est la Station initiale
     * @return from 
     */
    public Station from() {
        return from;
    }
    
    /**
     * getter de l'attribut to qui est la Station finale
     * @return to
     */
    public Station to() {
        return to;
    }
    
    /**
     * getter de l'attribut points qui est le nombre de point associé à la validation de ce Trip
     * @return points
     */
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
        return (connectivity.connected(from, to)) ? points : -points;
    }
}
