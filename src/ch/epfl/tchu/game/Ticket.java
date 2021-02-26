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
        Preconditions.checkArgument(!trips.isEmpty());
        this.trips = List.copyOf(trips); // prendre en attribut une liste immuable

        //vérification que toutes les gares de départ sont les meme
        if (trips!=null) {
            String firstInitialStationName = trips.get(0).from().name();
            for (Trip trip : trips) { 
                Preconditions.checkArgument(trip.from().name().equals(firstInitialStationName));
            }
        }
        text = (trips.size()!=0) ? computeText(trips) : "";
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
    
    /**
     * getter de l'attribut text qui est associé à ce ticket
     * @return text
     */
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
        int maxNegativePoints = -100; //ce nombre est arbitraire, il en faut juste un très bas
        for (Trip trip : trips) {
            int tripPoints = trip.points(connectivity);
            if (tripPoints>=0) {
                    maxPositivePoints = Math.max(tripPoints, maxPositivePoints);
                }
            else {
                    maxNegativePoints = Math.max(tripPoints, maxNegativePoints);
            }
        }
        return (maxPositivePoints > 0) ? maxPositivePoints : maxNegativePoints;
        }
    
    /**
     * méthode qui compare deux tickets
     * @return une valeure dans {-1,0,1} Tout dépend de la comparaison qui est faite par ordre alphabétique
     * négatif si that.text() vient après, 0 si ce sont les meme et positif si that.text() vient avant
     */
    public int compareTo(Ticket that) {
        return this.text()
                .compareTo(that.text());
    }
    
    /**
     * @return text permet de print les billets
     */
    @Override
    public String toString() {
        return text;
    }
}
