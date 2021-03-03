package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;

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
     * retourne le nom (français) de la carte donnée, au singulier ssi la valeur absolue du second argument vaut 1 (utilise BLACK_CARD, BLUE_CARD, etc.)
     * @param card
     * @param count
     * @return
     */
    public static String cardName(Card card, int count) {

        return card.name();
    }

    /**
     * retourne le message déclarant que les joueurs, dont les noms sont ceux donnés, ont terminé la partie ex æqo en ayant chacun remporté les points donnés (utilise DRAW)
     * @param playerNames
     * @param points
     * @return
     */
    public static String draw(List<String> playerNames, int points) {

        return "DRAW";
    }

    /**
     *
     * @return
     */
    public String willPlayFirst() {

    }
    public String keptTickets(int count) {

    }
    public String canPlay() {

    }
    public String drewTickets(int count) {

    }
    public String drewBlindCard() {

    }
    public String drewVisibleCard(Card card) {

    }
    public String claimedRoute(Route route, SortedBag<Card> cards){

    }
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){

    }
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){

    }
    public String didNotClaimRoute(Route route){

    }
    public String lastTurnBegins(int carCount){

    }
    public String getsLongestTrailBonus(Trail longestTrail){

    }
    public String won(int points, int loserPoints){

    }

}
