package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aymeric de chillaz (326617)
 * @author Thösam Norlha-Tsang (330163) représente l'état observable d'une
 *         partie de tCHu - une instance de ObservableGameState est spécifique à
 *         un joueur
 */
public final class ObservableGameState {
    /**
     * Valeur àl'initialisation des IntegerProperty dans ObservableGameState
     */
    private static final int INITIAL_VALUE_OF_INTEGER_PROPERTY = 0;

    /**
     * A la création, la totalité des propriétés de l'état sont null pour celles
     * contenant un objet 0 pour celles contenant un entier, false pour celles
     * contenant une valeur booléenne
     */
    private final PlayerId playerId;
    private PublicGameState publicGameState = null;
    private PlayerState playerState = null;

    // --- --- Extensions
    private Map<Ticket, Integer> ticketListHandPoints = new HashMap<>(); //ajouté pour le faire le calcul qu'une seule fois, est mis à jour, si le joueur tire des billets ou s'il prend une route -> on peut le mettre dans modifyPlayerTickets dans le setState
    public Map<Ticket, Integer> getTicketListHandPoints(){return ticketListHandPoints;}
    private Map<Ticket, Integer> ticketListPopUp = new HashMap<>();
    public Map<Ticket, Integer> getTicketListPopUp(){return ticketListPopUp;}
    public void setTicketListPopUp(SortedBag<Ticket> ticketsToChoose) {
        ticketListPopUp = (playerState == null) ? ticketListPopUp : playerState.ticketPoint(ticketsToChoose);
    }

    /**
     * Propriétés concernant l'état public de la partie
     */
    private final IntegerProperty propertyPercentTicketsLeft;
    private final IntegerProperty propertyPercentCardsLeft;
    private final List<ObjectProperty<Card>> propertyFaceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> propertyRouteOwners;

    /**
     * Propriétés concernant l'état public des de chacun des joueurs
     */
    private final Map<PlayerId, IntegerProperty> propertyNbTicketsInHand;
    private final Map<PlayerId, IntegerProperty> propertyNbCardsInHand;
    private final Map<PlayerId, IntegerProperty> propertyNbCarsInHand;
    private final Map<PlayerId, IntegerProperty> propertyNbConstructionPoints;

    /**
     * Propriétés concernant l'état complet du joueur auquel l'instance
     * correspond
     */
    private final ObservableList<Ticket> propertyPlayerTickets;
    private final Map<Card, IntegerProperty> propertyCardsOfInHand;
    private final Map<Route, BooleanProperty> propertyCanClaimRoute;

    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        this.propertyPercentTicketsLeft = new SimpleIntegerProperty(
                INITIAL_VALUE_OF_INTEGER_PROPERTY);
        this.propertyPercentCardsLeft = new SimpleIntegerProperty(
                INITIAL_VALUE_OF_INTEGER_PROPERTY);
        this.propertyFaceUpCards = initFaceUpCards();
        this.propertyRouteOwners = initRouteOwners();

        this.propertyNbTicketsInHand = initPropertyIdMap();
        this.propertyNbCardsInHand = initPropertyIdMap();
        this.propertyNbCarsInHand = initPropertyIdMap();
        this.propertyNbConstructionPoints = initPropertyIdMap();

        this.propertyPlayerTickets = FXCollections.observableArrayList();
        this.propertyCardsOfInHand = initCardsOfInHand();
        this.propertyCanClaimRoute = initCanClaimRoute();
    }

    /**
     * Méthode publique qui met à jour publicGameState, playerState et les
     * propriétés
     * 
     * @param newGameState
     *            la partie publique du jeu
     * @param newPlayerState
     *            l'état complet du joueur auquel la classe correspond
     */
    public void setState(PublicGameState newGameState,
            PlayerState newPlayerState) {
        // publicGameState et playerState sont updaté
        this.publicGameState = newGameState;
        this.playerState = newPlayerState;

        /**
         * Les premieres propriétés à etre updaté sont celles concernant l'état
         * public de la partie (l'ordre des propriétés n'est pas important
         * lorsqu'on les updates)
         * 
         * percentTicketsLeft et percentCardsLeft permettent aux joueurs d'avoir
         * une idée de combien de cartes et tickets il reste. percent est une
         * méthode privée qui calcule le pourcentage si la premiere valeur est
         * plus petite ou égale à la seconde lance une exception si ce n'est pas
         * le cas
         * 
         */
        propertyPercentTicketsLeft.set(
                percent(newGameState.ticketsCount(), ChMap.tickets().size()));
        propertyPercentCardsLeft
                .set(percent(newGameState.cardState().deckSize(),
                        Constants.ALL_CARDS.size()));
        modifyFaceUpCards();
        modifyRouteOwners();

        /**
         * Ce second groupe est celui des propriétés concernant l'état public de
         * chacun des joueurs
         */
        for (PlayerId id : PlayerId.ALL) {
            PublicPlayerState state = publicGameState.playerState(id);
            propertyNbTicketsInHand.get(id).set(state.ticketCount());
            propertyNbCardsInHand.get(id).set(state.cardCount());
            propertyNbCarsInHand.get(id).set(state.carCount());
            propertyNbConstructionPoints.get(id).set(state.claimPoints());
        }

        /**
         * Ce dernier groupe est celui des propriétés concernant l'état privé du
         * joueur auquel l'instance de ObservableGameState correspond
         */

        modifyCardsOfInHand();
        modifyCanClaimRoute();
        modifyPlayerTickets();

    }

    /**
     * Méthodes appelées à l'initialisation
     * 
     * A la création, la totalité des propriétés de l'état sont null pour celles
     * contenant un objet, 0 pour celles contenant un entrier, false pour celles
     * contenant une valeur booléenne
     * 
     * La constante INITIAL_VALUE_INTEGER_PROPERTY est égale à 0
     */
    private static Map<PlayerId, IntegerProperty> initPropertyIdMap() {
        Map<PlayerId, IntegerProperty> temp = new HashMap<>();
        PlayerId.ALL.forEach(id -> temp.put(id,
                new SimpleIntegerProperty(INITIAL_VALUE_OF_INTEGER_PROPERTY)));
        return Collections.unmodifiableMap(temp);
    }

    private static List<ObjectProperty<Card>> initFaceUpCards() {
        List<ObjectProperty<Card>> temp = new ArrayList<>();
        Constants.FACE_UP_CARD_SLOTS
                .forEach(i -> temp.add(new SimpleObjectProperty<>()));
        return Collections.unmodifiableList(temp);
    }

    private static Map<Route, ObjectProperty<PlayerId>> initRouteOwners() {
        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<>();
        ChMap.routes().forEach(
                route -> map.put(route, new SimpleObjectProperty<>(null)));
        return Collections.unmodifiableMap(map);
    }

    private static Map<Card, IntegerProperty> initCardsOfInHand() {
        Map<Card, IntegerProperty> map = new HashMap<>();
        Card.ALL.forEach(card -> map.put(card,
                new SimpleIntegerProperty(INITIAL_VALUE_OF_INTEGER_PROPERTY)));
        return Collections.unmodifiableMap(map);
    }

    private static Map<Route, BooleanProperty> initCanClaimRoute() {
        Map<Route, BooleanProperty> map = new HashMap<>();
        ChMap.routes().forEach(
                route -> map.put(route, new SimpleBooleanProperty(false)));
        return Collections.unmodifiableMap(map);
    }

    /**
     * méthodes privées pour la modification
     */

    /**
     * update les FACE_UP_CARD_SLOTS cartes face visible stocké dans la
     * propriété afin quelles soient les même que celles de publicGameState
     */
    private void modifyFaceUpCards() {
        Constants.FACE_UP_CARD_SLOTS.forEach(slot -> propertyFaceUpCards
                .get(slot).set(publicGameState.cardState().faceUpCard(slot)));
    }

    /**
     * chaque valeur associé aux routes contient l'identité du joueur la
     * possédant, ou null si elle n'appartient à personne
     */
    private void modifyRouteOwners() {
        // contient les routes des joueurs
        Map<PlayerId, List<Route>> mapOfPlayerRoutes = new HashMap<>();
        PlayerId.ALL.forEach(player -> mapOfPlayerRoutes.put(player,
                publicGameState.playerState(player).routes()));

        for (Route route : ChMap.routes()) {
            for (PlayerId player : PlayerId.ALL) {
                if (mapOfPlayerRoutes.get(player).contains(route)) {
                    propertyRouteOwners.get(route).set(player);
                    break;
                }
            }
        }
    }

    /**
     * update les tickets du joueur auquel la classe correspond
     */
    private void modifyPlayerTickets() {
        propertyPlayerTickets.setAll(playerState.tickets().toList());
        ticketListHandPoints = playerState.ticketPoint(playerState.tickets()); // --- --- Extension
    }

    /**
     * neuf propriétés contenant, pour chaque type de carte wagon/locomotive, le
     * nombre de cartes de ce type que le joueur a en main les retourne dans l
     * ordre de l enumeration Card avec locomotive à la fin
     */
    private void modifyCardsOfInHand() {
        Card.ALL.forEach(card -> propertyCardsOfInHand.get(card)
                .set(playerState.cards().countOf(card)));
    }

    /**
     * Modifie les propriétés contenant une valeur booléenne qui n'est vraie que
     * si le joueur peut actuellement s'emparer de la route. Pour cela, il faut
     * que le joueur soit le joueur courant, que la route n'appartienne à
     * personne et, dans le cas d'une route double, sa voisine non plus, et
     * finallement que le joueur ait les wagons et les cartes nécessaires pour
     * s'emparer de la route — ou en tout cas tenter de le faire s'il s'agit
     * d'un tunnel.
     * 
     * listePaireStation est une méthode privée qui créé une liste contenant les
     * paires de stations des routes possédé par les joueurs
     * 
     * claimable est une méthode privée qui permet de vérifier que les trois
     * conditions ci-dessus sont respéctés
     */
    private void modifyCanClaimRoute() {
        List<List<Station>> listePaireStations = listePaireStations(
                publicGameState.claimedRoutes());
        ChMap.routes().forEach(route -> propertyCanClaimRoute.get(route)
                .set(claimable(route, listePaireStations)));
    }

    /**
     * Getters des propriétés
     */
    public ReadOnlyIntegerProperty percentTicketsLeft() {
        return propertyPercentTicketsLeft;
    }

    public ReadOnlyIntegerProperty percentCardsLeft() {
        return propertyPercentCardsLeft;
    }

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return propertyFaceUpCards.get(slot);
    }

    public ReadOnlyIntegerProperty nbTicketsInHand(PlayerId id) {
        return propertyNbTicketsInHand.get(id);
    }

    public ReadOnlyIntegerProperty nbCardsInHand(PlayerId id) {
        return propertyNbCardsInHand.get(id);
    }

    public ReadOnlyIntegerProperty nbCarsInHand(PlayerId id) {
        return propertyNbCarsInHand.get(id);
    }

    public ReadOnlyIntegerProperty nbConstructionPoints(PlayerId id) {
        return propertyNbConstructionPoints.get(id);
    }

    public ObservableList<Ticket> playerTickets() {
        return propertyPlayerTickets;
    }

    public ReadOnlyIntegerProperty cardsOfInHand(Card card) {
        return propertyCardsOfInHand.get(card);
    }

    public ReadOnlyBooleanProperty canClaimRoute(Route route) {
        return propertyCanClaimRoute.get(route);
    }

    public boolean getCanDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    public List<SortedBag<Card>> getPossibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }

    public ObjectProperty<PlayerId> getRouteOwner(Route route) {
        return propertyRouteOwners.get(route);
    }

    public PublicGameState getPublicGameState() {
        return publicGameState;
    }

    /**
     * méthode privée permettant de calculer un pourcentage qui retourne un int
     * (pourcentage de a dans b) peut donc prendre une valeur entre 0 et 1 C'est
     * pour cela qu'on vérifie que a est plus petit ou égal à b
     */
    private static Integer percent(int a, int b) {
        Preconditions.checkArgument(a <= b);
        return (a * 100) / b;
    }

    /**
     * méthode qui retourne vraie que si le joueur peut actuellement s'emparer
     * de la route Cette méthode prend listePaireStations en paramètre car elle
     * a pour objectif d'etre appelé dans le corps de modifyCanClaimRoute et
     * qu'il serait couteux de créer cette liste de liste de station pour
     * chaques routes
     */
    private boolean claimable(Route route,
            List<List<Station>> listePaireStations) {
        return playerState.canClaimRoute(route)
                && publicGameState.currentPlayerId() == playerId
                && freeRoute(route, listePaireStations);

    }

    /**
     * créé une liste contenant les paires de stations des routes possédé par
     * les joueurs
     */

    private static List<List<Station>> listePaireStations(List<Route> claimedRoutes) {
        List<List<Station>> listeStations = new ArrayList<>();
        claimedRoutes.forEach(route -> listeStations.add(route.stations()));
        return listeStations;
    }

    /**
     * retourne vraie si la route n'appartient à personne et, dans le cas d'une
     * route double, sa voisine non plus
     * 
     * contains fait appel à equals donc ici nous avons un test par référence et
     * non par structure néanmoins ceci n'est pas problématique car nous nous
     * assurons qu’à chaque gare correspond un seul objet ainsi égalité par
     * référence = égalité structurelle ainsi égalité par référence = égalité
     * structurelle
     */
    private static boolean freeRoute(Route paramRoute,
            List<List<Station>> listePaireStations) {
        boolean output = true; // true si la route et sa voisine (si elle en a)
                               // n'appartiennent à personne

        for (List<Station> paireStation : listePaireStations) {
            if (paireStation.contains(paramRoute.station1())
                    && paireStation.contains(paramRoute.station2())) {

                output = false;
                break;
            }
        }
        return output;
    }

    // --- --- Extension ticket vert
    public PlayerState playerState(){
        return playerState;
    }

    // --- --- Extension pour rendre rouge, je les deux méthodes dedans en static
    public List<Route> allAvailableRoutesPlayer(List<Route> owned) {
        List<Route> allAvailableRoutes = new ArrayList<>(owned);
        List<List<Station>> stations = listePaireStations(getPublicGameState().claimedRoutes());
        for(Route r : ChMap.routes()){
            if(freeRoute(r, stations)){
                allAvailableRoutes.add(r);}
        }
        return allAvailableRoutes;
    }

    // -- Extension ticket vert, initiallement dans modifyPlayerTickets, lors de la prise de route, n'est pas mis à jour instantanément, donc on l'a mis avec receiveInfo() de GraphicalPlayer
    public void updateTicketListHandPoints(){
        if(playerState != null){ticketListHandPoints = playerState.ticketPoint(playerState.tickets());}
    }

}