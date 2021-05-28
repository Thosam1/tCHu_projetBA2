package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Classe PlayerState représente l'état complet d'un joueur. Elle hérite de PublicPlayerState.
 * @author Thösam Norlha-Tsang (330163)
 * @author Aymeric de chillaz (326617)
 */

public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

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
    }


    /**
     * méthode de construction statique :
     * @param initialCards
     * @return l'état initial d'un joueur auquel les cartes initiales données ont été distribuées (dans cet état initial, le joueur ne possède encore aucun billet, et ne s'est emparé d'aucune route)
     * @throws IllegalArgumentException si le nombre de cartes initiales ne vaut pas 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, new ArrayList<>());

    }

    /**
     * @return les billets du joueur
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }
    /**
     * @return les cartes wagon/locomotive du joueur
     */
    public SortedBag<Card> cards(){
        return cards;
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
     * @param card
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     */
    public PlayerState withAddedCard(Card card){
        return new PlayerState(tickets(), cards().union(
                SortedBag.of(card)), routes());
    }

    /**
     * @param route
     * @return vrai ssi le joueur peut s'emparer de la route donnée, c-à-d s'il lui reste assez de wagons et s'il possède les cartes nécessaires
     */
    public boolean canClaimRoute(Route route){ 
        return (carCount() >= route.length() && possibleClaimCards(route).isEmpty() == false);
    }

    /**
     *
     * @param route
     * @return la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour prendre possession de la route donnée
     * @throws IllegalArgumentException si le joueur n'a pas assez de wagons pour s'emparer de la route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(carCount() >= route.length()); // wagons (pièces) =/= cartes wagons - locomotives

        List<SortedBag<Card>> cardList = new ArrayList<>(route.possibleClaimCards());
        cardList.removeIf( e -> !cards().contains(e) ); // si les cartes ne sont pas dans les mains du joueurs, on les enlève de l'ensemble
        return cardList;
    }

    /**
     *
     * @param additionalCardsCount
     * @param initialCards
     * @return la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un tunnel, trié par ordre croissant du nombre de cartes locomotives, sachant qu'il a initialement posé les cartes initialCards, que les 3 cartes tirées du sommet de la pioche sont drawnCards, et que ces dernières forcent le joueur à poser encore additionalCardsCount cartes
     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus), si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents, ou si l'ensemble des cartes tirées ne contient pas exactement 3 cartes
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards){
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3);
        //Preconditions.checkArgument(initialCards != null); implicitement vérifié
        Preconditions.checkArgument(initialCards.size() >= 1);
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);

        Card initialCardsType = Card.LOCOMOTIVE;
        for (Card card : initialCards) {
            if(card != Card.LOCOMOTIVE) {
                initialCardsType = card;
            }
        }

        //On retire les cartes initialCards des cartes détenus par le joueur
        SortedBag<Card> cardsWithoutInitialCards = cards.difference(initialCards);

        Map<Card, Integer> map = new HashMap<>(cardsWithoutInitialCards.toMap());
        //SortedBag.of ne doit pas prendre des valeurs null
        map.putIfAbsent(Card.LOCOMOTIVE, 0);
        map.putIfAbsent(initialCardsType, 0);

        SortedBag<Card> cartesUtilisables;
        if(initialCardsType == Card.LOCOMOTIVE) { //le joueur a que posé des Locomotives
            cartesUtilisables = SortedBag.of(map.get(Card.LOCOMOTIVE), Card.LOCOMOTIVE);
        }
        else {//on est interessé par ses Locomotives et ses cartes de type initialCarsType
            cartesUtilisables = SortedBag.of(map.get(Card.LOCOMOTIVE), Card.LOCOMOTIVE,
                    map.get(initialCardsType), initialCardsType);
        }

        if(cartesUtilisables.size()<additionalCardsCount) { //return liste vide si il n'est pas possible d avoir un subset
            return List.of();}
        else {
            List<SortedBag<Card>> options = new ArrayList<>(cartesUtilisables.subsetsOfSize(additionalCardsCount));
            options.sort(
                    Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
            return options;
        }

    }


    /**
     *
     * @param route
     * @param claimCards
     * @return un état identique au récepteur, si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> reworkRoutes = new ArrayList<>(routes());
        reworkRoutes.add(route);
        return new PlayerState(tickets(), cards().difference(claimCards), reworkRoutes);
    }

    /**
     * @return le nombre de points gagné ou perdu avec les tickets
     */
    /**
     * @return le nombre de points gagné ou perdu avec les tickets
     */
    public int ticketPoints(){
        int ticketPoints = 0;
        for(Map.Entry<Ticket, Integer> c : ticketPoint(tickets()).entrySet()){
            ticketPoints += c.getValue();
        }
        return ticketPoints;
    }

    /**
     * @return la totalité des points obtenus par le joueur à la fin de la partie, à savoir la somme des points retournés par les méthodes claimPoints et ticketPoints
     */
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }

    //  --- --- --- Extension
    public Map<Ticket, Integer> ticketPoint(SortedBag<Ticket> tickets){
        if(tickets.isEmpty()) return new HashMap<>();
        Map<Ticket, Integer> ticketPoints = new HashMap<>();
        int idMax = -1;

        //nous cherchons l'identité maximale des stations qui relient les routes du joueur
        //Ceci est utile juste après pour la création du StationPartition.Builder

        for(Route r : routes()){
            idMax = Math.max(Math.max(r.station1().id(), r.station2().id()), idMax);
        }
        StationPartition.Builder builder = new StationPartition.Builder(idMax+1);

        //nous connectons toutes les stations pour obtenir la StationPartition du joueur
        routes().forEach(r -> builder.connect(r.station1(), r.station2()));
        StationPartition partition = builder.build();

        for (Ticket ticket : tickets) {
            ticketPoints.put(ticket, ticket.points(partition));
        }
        return ticketPoints;

    }
}
