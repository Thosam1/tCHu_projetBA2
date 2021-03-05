package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import javax.print.DocFlavor;
import java.util.List;

/**
 * @author Thösam Norlha-Tsang (330163)
 *
 */

public final class Info {

    private final String player;

    /**
     * construit un générateur de messages liés au joueur ayant le nom donné
     * @param playerName
     */
    public Info(String playerName) {
        this.player = playerName;
    }

    /**
     * @param card
     * @param count
     * @return le nom (français) de la carte donnée, au singulier ssi la valeur absolue du second argument vaut 1 (utilise BLACK_CARD, BLUE_CARD, etc.)
     */
    public static String cardName(Card card, int count) {
        String cardInFrench = "";
        switch (card.color()) {
            case BLACK :
                cardInFrench = StringsFr.BLACK_CARD;
                break;
            case VIOLET :
                cardInFrench = StringsFr.VIOLET_CARD;
                break;
            case BLUE :
                cardInFrench = StringsFr.BLUE_CARD;
                break;
            case GREEN :
                cardInFrench = StringsFr.GREEN_CARD;
                break;
            case YELLOW :
                cardInFrench = StringsFr.YELLOW_CARD;
                break;
            case ORANGE :
                cardInFrench = StringsFr.ORANGE_CARD;
                break;
            case RED :
                cardInFrench = StringsFr.RED_CARD;
                break;
            case WHITE :
                cardInFrench = StringsFr.WHITE_CARD;
                break;
            default:
                cardInFrench = StringsFr.LOCOMOTIVE_CARD;
        }
        if(Math.abs(count) > 1) {
            cardInFrench = cardInFrench + "s";
        }

        return cardInFrench;
    }

    /**
     * @param playerNames
     * @param points
     * @return le message déclarant que les joueurs, dont les noms sont ceux donnés, ont terminé la partie ex æqo en ayant chacun remporté les points donnés (utilise DRAW)
     */
    public static String draw(List<String> playerNames, int points) {

        return String.format(StringsFr.DRAW, playerNames.toString(), points);
    }

    /**
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, player);
    }

    /**
     * @param count
     * @return le message déclarant que le joueur a gardé le nombre de billets donné
     */
    public String keptTickets(int count) {
        String s = "";
        if(count == 0 || count == 1){
            s = "s";
        }
        return String.format(StringsFr.KEPT_N_TICKETS, player, count, s);

    }

    /**
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return  String.format(StringsFr.CAN_PLAY, player);
    }

    /**
     * @param count
     * @return le message déclarant que le joueur a tiré le nombre donné de billets
     */
    public String drewTickets(int count) {
        String s = "";
        if(count == 0 || count == 1){
            s = "s";
        }
        return String.format(StringsFr.DREW_TICKETS, player, count, s);
    }

    /**
     * @return le message déclarant que le joueur a tiré une carte «à l'aveugle», c-à-d du sommet de la pioche
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, player);
    }

    /**
     * @param card
     * @return le message déclarant que le joueur a tiré la carte disposée face visible donnée
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, player, cardName(card, 1));
    }

    /**
     * @param route
     * @param cards
     * @return le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){ // ??   how does it work with lists ?
        // Route name

        // List of cards
        String cardList = "";
        return String.format(StringsFr.CLAIMED_ROUTE, player, routeString(route), cardListString(cards));
    }

    /**
     * @param route
     * @param initialCards
     * @return le message déclarant que le joueur désire s'emparer de la route en tunnel donnée en utilisant initialement les cartes données
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){   // ??
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, player, routeString(route), cardListString(initialCards));
    }

    /**
     * @param drawnCards
     * @param additionalCost
     * @return le message déclarant que le joueur a tiré les trois cartes additionnelles données, et qu'elles impliquent un coût additionel du nombre de cartes donné
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String temp = String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardListString(drawnCards));

        if(additionalCost == 0) {
            temp = temp + String.format(StringsFr.NO_ADDITIONAL_COST);
        } else {
            temp = temp + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, "s");
        }
        return temp;

    }

    /**
     * @param route
     * @return le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     */
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, player, routeString(route));
    }

    /**
     * @param carCount
     * @return le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons, et que le dernier tour commence
     */
    public String lastTurnBegins(int carCount){
        String s = "";
        if(carCount == 0 || carCount == 1){
            s = "s";
        }
        return String.format(StringsFr.LAST_TURN_BEGINS, player, carCount, s);

    }

    /**
     * @param longestTrail
     * @return le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné, qui est le plus long, ou l'un des plus longs
     */
    public String getsLongestTrailBonus(Trail longestTrail){ // ??? EN_DASH_SEPARATOR ?
        String trailStations = longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2();
        return String.format(StringsFr.GETS_BONUS, player, trailStations);
    }

    /**
     * @param points
     * @param loserPoints
     * @return le message déclarant que le joueur remporte la partie avec le nombre de points donnés, son adversaire n'en ayant obtenu que loserPoints
     */
    public String won(int points, int loserPoints){
        String firstS = "";
        String secondS = "";
        if(points>1){firstS = "s";}
        if(loserPoints>1){firstS = "s";}
        return String.format(StringsFr.WINS, player, points,firstS, loserPoints, secondS);

    }

    /**
     * getter pour le nom du joueur
     * @return player
     */
    public String getPlayerName(){
        return player;
    }

    private static String routeString(Route route){
        return (route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name());
    }

    private static String cardListString(SortedBag<Card> cardSortedBag){
        String cardList = "";

        int i = 0;
        for (Card c: cardSortedBag.toSet()) {

            int n = cardSortedBag.countOf(c);
            if(i == cardSortedBag.toSet().size() - 2){
                cardList = cardList + n + " " + cardName(c, n) + StringsFr.AND_SEPARATOR;
            }else if(i == cardSortedBag.toSet().size() - 2){
                cardList = cardList + n + " " + cardName(c, n);
            }else{
                cardList = cardList + n + " " + cardName(c, n) + ", ";
            }

            i += 1;
        }
        return cardList;
    }

//    private static String trailString(Trail trail){
//
//    }

}
