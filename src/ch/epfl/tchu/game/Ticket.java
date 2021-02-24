package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public final class Ticket implements Comparable<Ticket> {
    private final List<Trip> trips;
    private final String text;
    
    public Ticket(List<Trip> trips) {
        this.trips = Objects.requireNonNull(trips);
       
        //vérification que toutes les gares de départ sont les meme
        
        for (Trip trip : trips) {
            if(!(trip.from().equals(trips.get(0).from()))){
                throw new IllegalArgumentException();
                }
            }
        text = computeText(trips);
        }
    
    public Ticket(Station from, Station to, int points) {
        this(Trip.simpleTrip(from,to,points));
    }
    
    public String text() {
        return text;
    }
    
    private static String computeText(List<Trip> trips) {
        String initialStation = trips.get(0).from().name();
        TreeSet<String> destinations = new TreeSet<>();
        String output;
        int numberOfDestinations = 0;
        for (Trip trip : trips) {
            destinations.add(trip.to() + " (" + trip.points() +")");
            ++numberOfDestinations;
            }
        output = String.join(", ", destinations);
        //met des accolades si il y a plus qu une gare de destination
        if (numberOfDestinations!=1) {
            output = String.format("{%s}", output);
            }
        output = String.format("%s - %s", initialStation, output);
        return output;
        }
    //Je ne sais pas si j ai fait la bonne chose ici
    //L idée est que si aucunes gare n a été atteinte, le score est le plus grand score (négatif)
    // -4 et -8 ==> -4
    //et que sinon, les points sont ceux de la gare qui donne le plus de point
    public int points(StationConnectivity connectivity) {
        int maxPositivePoints = 0;
        int maxNegativePoints = -100; //ce nombre est arbitraire, il en faut juste un très bas
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
