package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;

/**
 * @author Thösam Norlha-Tsang (330163)
 * class PublicPlayerState, représente la partie publique de l'état d'un joueur, à savoir le nombre de billets, de cartes et de wagons qu'il possède, les routes dont il s'est emparé, et le nombre de points de construction qu'il a ainsi obtenu.
 */
public class PublicPlayerState {    // immuable ??? héritage problème

    private final int ticketCount;  // nbre de billets
    private final int cardCount;    // nbre de cartes
    private final List<Route> routes;   // les routes du joueur

    private final int carCount;   // nbre de wagons
    private final int pointCount;   // nbre de points de construction

    /**
     * Constructeur de la classe: construit l'état public d'un joueur possédant le nombre de billets et de cartes donnés, et s'étant emparé des routes données
     * @param ticketCount
     * @param cardCount
     * @param routes
     * @throws IllegalArgumentException si le nombre de billets ou le nombre de cartes est strictement négatif (< 0)
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = routes;

        int sumOfRoutesLengths = 0;
        int points = 0;

        // IF ROUTE == NULL ?   -> it should fail (null pointer exception)
        for(Route r: routes){
            sumOfRoutesLengths = sumOfRoutesLengths + r.length();
            points += r.claimPoints();
        }

        this.carCount = Constants.INITIAL_CAR_COUNT - sumOfRoutesLengths;   // nombre de wagons
        this.pointCount = points;   // points de construction

    }

    /**
     * @return le nombre de billets que possède le joueur
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * @return le nombre de cartes que possède le joueur
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * @return les routes dont le joueur s'est emparé
     */
    public List<Route> routes(){
        return routes;
    }

    /**
     * @return le nombre de wagons que possède le joueur
     */
    public int carCount() {
        return carCount;
    }

    /**
     * @return  le nombre de points de construction obtenus par le joueur
     */
    public int claimPoints() {
        return pointCount;
    }
}
