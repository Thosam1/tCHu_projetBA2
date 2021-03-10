package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thösam Norlha-Tsang (330163)
 * class PlayerState représente l'état complet d'un joueur. Elle hérite de PublicPlayerState.
 */

public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;
    /**
     * Constructeur de la classe: construit l'état d'un joueur possédant les billets, cartes et routes donnés.
     * @param tickets
     * @param cards
     * @param routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
        this.routes = routes;
    }

    /**
     * méthode de construction statique :
     * @param initialCards
     * @return l'état initial d'un joueur auquel les cartes initiales données ont été distribuées (dans cet état initial, le joueur ne possède encore aucun billet, et ne s'est emparé d'aucune route)
     * @throws IllegalArgumentException si le nombre de cartes initiales ne vaut pas 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size() == 4);

        return new PlayerState(null, initialCards, null);   // do we have to initialize to 0 ?
//        return new PlayerState(SortedBag.of(new ArrayList<>()), initialCards, new ArrayList<>());
    }

    /**
     * @return les billets du joueur
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    /**
     * @param newTickets
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        SortedBag<Ticket> unionTickets = tickets().union(newTickets);
        return new PlayerState(unionTickets, cards(), routes());
    }

    /**
     * @return les cartes wagon/locomotive du joueur
     */
    SortedBag<Card> cards(){
        return cards;
    }

    /**
     * @param card
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     */
    public PlayerState withAddedCard(Card card){
        SortedBag<Card> addedCard = cards();
        SortedBag<Card> single = SortedBag.of(Collections.singletonList(card));
        addedCard = addedCard.union(single);
        return new PlayerState(tickets(), addedCard, routes());
    }

    /**
     * @param additionalCards
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        SortedBag<Card> unionCards = cards();;
        unionCards = unionCards.union(additionalCards);
        return new PlayerState(tickets(), unionCards, routes());
    }

//    /**
//     * @param route
//     * @return vrai ssi le joueur peut s'emparer de la route donnée, c-à-d s'il lui reste assez de wagons et s'il possède les cartes nécessaires
//     */
//    public boolean canClaimRoute(Route route){  // ??? how to catch a route ???
//        if(route.length() ==){
//
//        }
//    }
//
//    /**
//     *
//     * @param route
//     * @return la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour prendre possession de la route donnée
//     * @throws IllegalArgumentException si le joueur n'a pas assez de wagons pour s'emparer de la route
//     */
//    public List<SortedBag<Card>> possibleClaimCards(Route route){
//        Preconditions.checkArgument();
//    }
//
//    /**
//     *
//     * @param additionalCardsCount
//     * @param initialCards
//     * @param drawnCards
//     * @return la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un tunnel, trié par ordre croissant du nombre de cartes locomotives, sachant qu'il a initialement posé les cartes initialCards, que les 3 cartes tirées du sommet de la pioche sont drawnCards, et que ces dernières forcent le joueur à poser encore additionalCardsCount cartes
//     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus), si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents, ou si l'ensemble des cartes tirées ne contient pas exactement 3 cartes
//     */
//    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
//        Preconditions.checkArgument();
//    }
//
//    /**
//     *
//     * @param route
//     * @param claimCards
//     * @return un état identique au récepteur, si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données
//     */
//    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
//        SortedBag<Card> reworkCards = cards().difference(claimCards);    // just check in case
//        List<Route> reworkRoutes = routes();
//        reworkRoutes.add(route);
//
//        return new PlayerState(tickets(),reworkCards, reworkRoutes);
//    }
//
//    /**
//     *
//     * @return
//     */
//    public int ticketPoints(){  // ?!?!?
//        int ticketPoints = 0;
//        for(Ticket t: tickets){
//            t.points();
//        }
//        return ticketPoints;
//    }
//
//    /**
//     * @return la totalité des points obtenus par le joueur à la fin de la partie, à savoir la somme des points retournés par les méthodes claimPoints et ticketPoints
//     */
//    public int finalPoints(){
//        return claimPoints() + ticketPoints();
//    }

}
