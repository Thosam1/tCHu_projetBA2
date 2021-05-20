package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Aymeric de chillaz (326617)
 * @author Thösam Norlha-Tsang (330163) représente l'état observable d'une
 *         partie de tCHu - une instance de ObservableGameState est spécifique à
 *         un joueur qui est passé au constructeur et gardé en attribut
 */
public final class ObservableGameState {

    /**
     * A la création, la totalité des propriétés de l'état sont null pour celles
     * contenant un objet, 0 pour celles contenant un entrier, false pour celles
     * contenant une valeur booléenne
     */

    private final PlayerId playerId;
    private PublicGameState publicGameState = null;
    private PlayerState playerState = null;

    /**
     * Propriétés concernant l'état public de la partie
     */

    private final IntegerProperty percentTicketsLeft;

    private final IntegerProperty percentCardsLeft;

    private final List<ObjectProperty<Card>> faceUpCards;

    private final Map<Route, ObjectProperty<PlayerId>> routeOwners;

    /**
     * Propriétés concernant l'état public des deux joueurs
     */

    private final Map<PlayerId, IntegerProperty> nbTicketsInHand;
    private final Map<PlayerId, IntegerProperty> nbCardsInHand;
    private final Map<PlayerId, IntegerProperty> nbCarsInHand;
    private final Map<PlayerId, IntegerProperty> nbConstructionPoints;

    /**
     * Propriétés concernant l'état complet du joueur auquel l'instance
     * correspond
     */
    private final ObservableList<Ticket> playerTickets;
    private final Map<Card, IntegerProperty> cardsOfInHand;
    private final Map<Route, BooleanProperty> canClaimRoute;

    /**
     * Constructeur de la classe ObservableGameState. Conserve playerId dans
     * l'attribut de meme nom et initialise les propriétés
     */
    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        this.percentTicketsLeft = new SimpleIntegerProperty(
                Constants.INITIAL_VALUE_OF_INTEGER_PROPERTY);
        this.percentCardsLeft = new SimpleIntegerProperty(
                Constants.INITIAL_VALUE_OF_INTEGER_PROPERTY);
        this.faceUpCards = initFaceUpCards();
        this.routeOwners = initRouteOwners();

        this.nbTicketsInHand = initMapPlayerIdIntegerProperty();
        this.nbCardsInHand = initMapPlayerIdIntegerProperty();
        this.nbCarsInHand = initMapPlayerIdIntegerProperty();
        this.nbConstructionPoints = initMapPlayerIdIntegerProperty();

        this.playerTickets = FXCollections.observableArrayList();
        this.cardsOfInHand = initCardsOfInHand();
        this.canClaimRoute = initCanClaimRoute();
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
         * public de la partie
         * 
         * percentTicketsLeft et percentCardsLeft permettent aux joueurs d'avoir
         * une idée de combien de cartes et tickets il reste. percent est une
         * méthode privée qui calcule le pourcentage si la premiere valeur est
         * plus petite ou égale à la seconde lance une exception si ce n'est pas
         * le cas
         * 
         */
        percentTicketsLeft.set(
                percent(newGameState.ticketsCount(), ChMap.tickets().size()));
        percentCardsLeft.set(percent(newGameState.cardState().deckSize(),
                Constants.ALL_CARDS.size()));
        modifyFaceUpCards();
        modifyRouteOwners();

        /**
         * Ce second groupe est celui des propriétés concernant l'état public de
         * chacun des joueurs
         */
        for (PlayerId id : PlayerId.ALL) {
            PublicPlayerState state = publicGameState.playerState(id);
            nbTicketsInHand.get(id).set(state.ticketCount());
            nbCardsInHand.get(id).set(state.cardCount());
            nbCarsInHand.get(id).set(state.carCount());
            nbConstructionPoints.get(id).set(state.claimPoints());
        }

        /**
         * Ce dernier groupe est celui des propriétés concernant l'état privé du
         * joueur auquel l'instance de ObservableGameState correspond
         */
        modifyPlayerTickets();
        modifyCardsOfInHand();
        modifyCanClaimRoute();

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
    private static Map<PlayerId, IntegerProperty> initMapPlayerIdIntegerProperty() {
        Map<PlayerId, IntegerProperty> temp = new HashMap<PlayerId, IntegerProperty>();
        PlayerId.ALL
                .forEach(player -> temp.put(player, new SimpleIntegerProperty(
                        Constants.INITIAL_VALUE_OF_INTEGER_PROPERTY)));
        return Collections.unmodifiableMap(temp);
    }

    private static List<ObjectProperty<Card>> initFaceUpCards() {
        List<ObjectProperty<Card>> temp = new ArrayList<ObjectProperty<Card>>();
        Constants.FACE_UP_CARD_SLOTS
                .forEach(cardSlot -> temp.add(new SimpleObjectProperty<>()));
        return Collections.unmodifiableList(temp);
    }

    private static Map<Route, ObjectProperty<PlayerId>> initRouteOwners() {
        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<Route, ObjectProperty<PlayerId>>();
        ChMap.routes().forEach(
                route -> map.put(route, new SimpleObjectProperty<>(null)));
        return Collections.unmodifiableMap(map);
    }

    private static Map<Card, IntegerProperty> initCardsOfInHand() {
        Map<Card, IntegerProperty> map = new HashMap<Card, IntegerProperty>();
        Card.ALL.forEach(card -> map.put(card, new SimpleIntegerProperty(
                Constants.INITIAL_VALUE_OF_INTEGER_PROPERTY)));
        return Collections.unmodifiableMap(map);
    }

    private Map<Route, BooleanProperty> initCanClaimRoute() {
        Map<Route, BooleanProperty> map = new HashMap<Route, BooleanProperty>();
        ChMap.routes().forEach(
                route -> map.put(route, new SimpleBooleanProperty(false)));
        return Collections.unmodifiableMap(map);
    }

    /**
     * méthodes privées pour la modification des propriétés
     */

    /**
     * update les FACE_UP_CARD_SLOTS cartes face visible stocké dans la
     * propriété afin quelles soient les même que celles de publicGameState
     */
    private void modifyFaceUpCards() {
        Constants.FACE_UP_CARD_SLOTS
                .forEach(cardSlot -> faceUpCards.get(cardSlot)
                        .set(publicGameState.cardState().faceUpCard(cardSlot)));
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
                    routeOwners.get(route).set(player);
                    break;
                }
                routeOwners.get(route).set(null);
            }
        }
    }

    /**
     * update les tickets du joueur auquel la classe correspond
     */
    private void modifyPlayerTickets() {
        playerTickets.setAll(playerState.tickets().toList());
    }

    /**
     * neuf propriétés contenant, pour chaque type de carte wagon/locomotive, le
     * nombre de cartes de ce type que le joueur a en main les retourne dans l
     * ordre de l enumeration Card avec locomotive à la fin
     */
    private void modifyCardsOfInHand() {
        for (Card card : Card.ALL) {
            cardsOfInHand.get(card).set(playerState.cards().countOf(card));
        }
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
        // cette liste est créé avant le for each pour ne pas avoir à en créer
        // une nouvelle à chaque fois
        ChMap.routes().forEach(route -> canClaimRoute.get(route)
                .set((claimable(route, listePaireStations) ? true : false)));
    }

    /**
     * Getters des propriétés
     */
    public ReadOnlyIntegerProperty percentTicketsLeft() {
        return percentTicketsLeft;
    }

    public ReadOnlyIntegerProperty percentCardsLeft() {
        return percentCardsLeft;
    }

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    public ReadOnlyObjectProperty<PlayerId> routeOwner(Route route) {
        return routeOwners.get(route);
    }

    public ReadOnlyIntegerProperty nbTicketsInHand(PlayerId id) {
        return nbTicketsInHand.get(id);
    }

    public ReadOnlyIntegerProperty nbCardsInHand(PlayerId id) {
        return nbCardsInHand.get(id);
    }

    public ReadOnlyIntegerProperty nbCarsInHand(PlayerId id) {
        return nbCarsInHand.get(id);
    }

    public ReadOnlyIntegerProperty nbConstructionPoints(PlayerId id) {
        return nbConstructionPoints.get(id);
    }

    public ObservableList<Ticket> playerTickets() {
        return playerTickets;
    }

    public ReadOnlyIntegerProperty cardsOfInHand(Card card) {
        return cardsOfInHand.get(card);
    }

    public ReadOnlyBooleanProperty canClaimRoute(Route route) {
        return canClaimRoute.get(route);
    }

    public String faceUpCardName(int slot) {
        return publicGameState.cardState().faceUpCard(slot).name();
    }

    public boolean getCanDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    public boolean getCanDrawCards() {
        return publicGameState.canDrawCards();
    }

    public List<SortedBag<Card>> getPossibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }

    public ObjectProperty<PlayerId> getRouteOwner(Route route) {
        return routeOwners.get(route);
    }

    public PublicGameState getPublicGameState() {
        return publicGameState;
    }

    /**
     * méthode privée permettant de calculer un pourcentage qui retourne un int
     * (pourcentage de a dans b) peut donc prendre une valeur entre 0 et 1 C'est
     * pour cela qu'on vérifie que a est plus petit ou égal à b
     */
    private Integer percent(int a, int b) {
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

    private List<List<Station>> listePaireStations(List<Route> claimedRoutes) {
        List<List<Station>> listeStations = new ArrayList<>();
        for (Route route : claimedRoutes) {
            listeStations.add(route.stations());
        }
        return listeStations;
    }

    /**
     * retourne vraie si la route n'appartient à personne et, dans le cas d'une
     * route double, sa voisine non plus
     */
    private boolean freeRoute(Route paramRoute,
            List<List<Station>> listePaireStations) {
        Boolean output = true; // true si la route et sa voisine (si elle en a)
                               // n'appartient à personne

        for (List<Station> paireStation : listePaireStations) {
            if (paireStation.contains(paramRoute.station1())
                    && paireStation.contains(paramRoute.station2())) {
                // contains fait appel à equals donc ici nous avons un test
                // par référence et non par structure
                // néanmoins ceci n'est pas problématique car nous nous assurons
                // qu’à chaque gare correspond un seul objet ainsi égalité par
                // référence = égalité structurelle
                output = false;
            }
        }
        return output;
    }

    /**
     * méthode qui retourne un ReadOnlyBooleanProperty: contient la valeur true
     * que si le joueur peut actuellement s'emparer de la route Ce qui est
     * déterminé lors des updates de canClaimRoute Cette méthode est appelé dans
     * MapViewCreator
     */
    public ReadOnlyBooleanProperty claimable(Route route) {
        return canClaimRoute.get(route);
    }

}
