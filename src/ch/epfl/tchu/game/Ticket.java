package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import ch.epfl.tchu.Preconditions;
/**
 * 
 * @author Aymeric de chillaz (326617)
 *
 */
public final class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String text;
    
    /**
     * construit un Ticket avec la liste de voyages (Trip) donnée
     * 
     * @param trips 
     *          la liste contenant les Trip
     * @throws IllegalArgumentException 
     *          si le paramètre trips est null ou si les gares de départ pour chaques
     *          Trip ne sont pas les meme
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());  // if empty throws an IllegalArgumentException
        this.trips = trips; // Objects.requireNonNull(List.copyOf(trips)); à supprimer
       
        //vérification que toutes les gares de départ sont les meme
        if (trips!=null) {
            for (Trip trip1 : trips) {              // we could improve this with a better algorithm
                for(Trip trip2 : trips){
                    if(!(trip1.from().equals(trip2.from()))){
                        throw new IllegalArgumentException();
                    }
                }
//                if(!(trip.from().equals(trips.get(0).from()))){ // là tu compares seulement l'index 0 avec tous les autres, mais qu'en est-il de l'index 1,2,3,... ?
//                    throw new IllegalArgumentException();
//                }
            }

        }
        text = computeText(trips);
        }
    
    /**
     * deuxieme constructeur qui construit un Ticket à partir de deux Stations et 
     * des points
     * 
     * @param from
     *          Station de départ
     * @param to
     *          Station d'arrivée
     * @param points
     *          Points associé à ce Ticket si les deux gares sont reliés
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }
    
    public String text() {
        return text;
    }
    
    /**
     * méthode appelée dans le constructeur, qui créé le text associé au ticket
     * 
     * @param trips
     *          liste contenant les Trip
     * @return
     *          retourne le text associé au ticket
     *          si trips contient plus qu un Trip alors, les Stations de destination doivent etre comprisent entre {}
     */
    private static String computeText(List<Trip> trips) {
        String initialStation = trips.get(0).from().name();
        TreeSet<String> destinations = new TreeSet<>();
        String output;
        for (Trip trip : trips) {
            destinations.add(trip.to() + " (" + trip.points() +")");
            }
        output = String.join(", ", destinations);
        //met des accolades si il y a plus qu une gare de destination
        
        if (destinations.size()>1) {
            output = String.format("{%s}", output);
            }
        output = String.format("%s - %s", initialStation, output);
        return output;
        }
    
    public static void main(String args[]) {
        var s1 = new Station(0, "From");
        var s2 = new Station(1, "To");
        var s3 = new Station(2, "2");
        var t = new Ticket(List.of(new Trip(s1, s2, 15), new Trip(s1,s3, 15)));
        System.out.println(t.text());
    }
    
    /**
     * méthode qui détermine combien un joueur perd ou gagne en points pour un ticket
     * @param connectivity
     *          permet d appeler la méthode points sur chacuns des Trip contenu dans l attribut trips
     * @return
     *          retourne la plus grande valeure positive
     *          et retourne le plus petit malus, si le joueur ne gagne pas de points sur ce ticket
     */
    public int points(StationConnectivity connectivity) {
        int maxPositivePoints = 0;
        int maxNegativePoints = -400; //ce nombre est arbitraire, il en faut juste un très bas
        for (Trip trip : trips) {
            int tripPoints = trip.points(connectivity);
            if (tripPoints>=0) {
                if(tripPoints > maxPositivePoints) {
                    maxPositivePoints = tripPoints;
                    }
                }
            else {
                if(tripPoints > maxNegativePoints) {
                    maxNegativePoints = tripPoints;
                }
            }
        }

        if(maxPositivePoints != 0) return maxPositivePoints;
        else return maxNegativePoints;
        }
    
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }
    
    @Override
    public String toString() {
        return text;
    }
}
