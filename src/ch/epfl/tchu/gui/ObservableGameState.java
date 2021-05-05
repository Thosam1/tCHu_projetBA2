package ch.epfl.tchu.gui;
import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.BooleanBinding;
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
 * @author Thösam Norlha-Tsang (330163)
 * représente l'état observable d'une partie de tCHu - une instance de ObservableGameState est spécifique à un joueur
 */
public class ObservableGameState {

    /**A la création, la totalité des propriétés de l'état sont null pour celles contenant un objet
     * 0 pour celles contenant un entrier, false pour celles contenant une valeur booléenne*/
    
    private PlayerId playerId;
    private PublicGameState publicGameState = null;
    private PlayerState playerState = null;

    /**
     *  Propriétés concernant l'état public de la partie
     */
    
    private final IntegerProperty percentTicketsLeft;   //TODO checker que c'est bien IntegerProperty et pas ObjectProperty
    
    private final IntegerProperty percentCardsLeft;

    private final List<ObjectProperty<Card>> faceUpCards;

    private final Map<Route, ObjectProperty<PlayerId>> routeOwners; //TODO can we change values if we declare it final, casse l'immuabilité?

    /**
     *  Propriétés concernant l'état public des de chacun des joueurs
     *  propriété du Player1 à l index 0 et du Player2 à l index 1
     */
    
    private final Map<PlayerId, IntegerProperty> nbTicketsInHand;
    private final Map<PlayerId, IntegerProperty> nbCardsInHand;
    private final Map<PlayerId, IntegerProperty> nbCarsInHand;
    private final Map<PlayerId, IntegerProperty> nbConstructionPoints;

    /**
     *  Propriétés concernant l'état complet du joueur auquel l'instance correspond
     */
    private final ObservableList<Ticket> playerTickets;   //TODO quand c'est une liste pointer à quoi ??? ou les éléments null ?
    private final Map<Card, IntegerProperty> cardsOfInHand;
    private final Map<Route, BooleanProperty> canClaimRoute;

    public ObservableGameState(PlayerId playerId){
        this.playerId = playerId;
        this.percentTicketsLeft = new SimpleIntegerProperty(0); //TODO vérifier que c'est bien SimpleIntegerProperty
        this.percentCardsLeft = new SimpleIntegerProperty(0);
        this.faceUpCards = initFaceUpCards();
        this.routeOwners = initRouteOwners();

        this.nbTicketsInHand = intPropertyIdMap();
        this.nbCardsInHand = intPropertyIdMap();
        this.nbCarsInHand = intPropertyIdMap();
        this.nbConstructionPoints = intPropertyIdMap();

        this.playerTickets = FXCollections.observableArrayList();
        this.cardsOfInHand = initCardsOfInHand();
        this.canClaimRoute = initCanClaimRoute();
    }
    
    /**
     * Pour mettre à jour l'état que la classe contient + les propriétés
     * @param newGameState la partie publique du jeu
     * @param newPlayerState l'état complet du joueur auquel la classe correspond
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        this.publicGameState = newGameState;
        this.playerState = newPlayerState;
        
        percentTicketsLeft.set(percent(newGameState.ticketsCount(), ChMap.tickets().size()));
        percentCardsLeft.set(percent(newGameState.cardState().deckSize(), Constants.ALL_CARDS.size()));
        modifyFaceUpCards();
        modifyRouteOwners();
        
      //est ce que ce ne serait pas plus propre en faisant un appel à des modify
        for(PlayerId id: PlayerId.ALL) {
            PublicPlayerState state = publicGameState.playerState(id);
            nbTicketsInHand.get(id).set(state.ticketCount());   //TODO est-ce qu'on set bien des objectproperty?
            nbCardsInHand.get(id).set(state.cardCount());
            nbCarsInHand.get(id).set(state.carCount());
            nbConstructionPoints.get(id).set(state.claimPoints());
        }

        modifyPlayerTickets();
        modifyCardsOfInHand();
        modifyCanClaimRoute();

        }

    /**
     *  Méthodes appelées à l'initialisation
     */
    private static Map<PlayerId, IntegerProperty> intPropertyIdMap(){
        Map<PlayerId, IntegerProperty> temp = new HashMap<PlayerId, IntegerProperty>(); //ToDo
        for(PlayerId id : PlayerId.ALL){
            temp.put(id, new SimpleIntegerProperty(0));
        }
        return Collections.unmodifiableMap(temp);
    }
    private static List<ObjectProperty<Card>> initFaceUpCards(){
        List<ObjectProperty<Card>> temp = new ArrayList<ObjectProperty<Card>>();
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            SimpleObjectProperty<Card> card = new SimpleObjectProperty<>();
            temp.add(card);
        }
        return Collections.unmodifiableList(temp);
    }
    private static Map<Route, ObjectProperty<PlayerId>> initRouteOwners(){
        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<Route, ObjectProperty<PlayerId>>();
        for(Route route : ChMap.routes()) {
            map.put(route, new SimpleObjectProperty<>(null));
        }
        return Collections.unmodifiableMap(map);
    }
    private static Map<Card, IntegerProperty> initCardsOfInHand() {
        Map<Card, IntegerProperty> map = new HashMap<Card, IntegerProperty>();
        for (Card card :Card.ALL) {
            map.put(card, new SimpleIntegerProperty(0));
        }
        return Collections.unmodifiableMap(map);
    }
    private Map<Route, BooleanProperty> initCanClaimRoute(){
        Map<Route, BooleanProperty> map = new HashMap<Route, BooleanProperty>();
        for(Route route : ChMap.routes()) {
            map.put(route, new SimpleBooleanProperty(false));
        }
        return Collections.unmodifiableMap(map);
    }

        /**
         *  méthodes privées  pour la modification
         */

    private void modifyFaceUpCards(){ //TODO mieux si on met une méthode non static, sans argument, qui modifie directement la variable
        for(int slot : Constants.FACE_UP_CARD_SLOTS){
            Card newCard = publicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
            }
    }
    
    private void modifyRouteOwners(){
        List<Route> routesPlayer1 = publicGameState.playerState(PlayerId.PLAYER_1).routes();
        List<Route> routesPlayer2 = publicGameState.playerState(PlayerId.PLAYER_2).routes();
        
        for(Route route : ChMap.routes()) {
            if(routesPlayer1.contains(route)){
                routeOwners.get(route).set(PlayerId.PLAYER_1);
            }
            else if(routesPlayer2.contains(route)){
                routeOwners.get(route).set(PlayerId.PLAYER_2);
            }
            else {routeOwners.get(route).set(null);}
        }
    }

    private void modifyPlayerTickets(){
        playerTickets.setAll(playerState.tickets().toList());
    }
                                                                        /**neuf propriétés contenant, pour chaque type de carte wagon/locomotive,
                                                                         * le nombre de cartes de ce type que le joueur a en main
                                                                         * les retourne dans l ordre de l enumeration Card avec locomotive à la fin
                                                                         * */

    private void modifyCardsOfInHand(){
        for(Card card : Card.ALL){
            cardsOfInHand.get(card).set(playerState.cards().countOf(card));  //TODO verify
        }
    }
    private void modifyCanClaimRoute(){
        List<List<Station>> listePaireStations = listePaireStations(publicGameState.claimedRoutes());
        //cette liste est créé avant le for each pour ne pas avoir à en créer une nouvelle à chaque fois
        for(Route route : ChMap.routes()) {
            if(claimable(route, listePaireStations)) {
                canClaimRoute.get(route).set(true);
            }
            else {canClaimRoute.get(route).set(false);}
        }
    }

    /**
     *  Getters des propriétés
     */
    public ReadOnlyIntegerProperty percentTicketsLeft(){return percentTicketsLeft;}
    public ReadOnlyIntegerProperty percentCardsLeft(){return percentCardsLeft;}
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }
    public ReadOnlyObjectProperty routeOwner(Route route){return routeOwners.get(route);}

    public ReadOnlyIntegerProperty nbTicketsInHand(PlayerId id){return nbTicketsInHand.get(id);}
    public ReadOnlyIntegerProperty nbCardsInHand(PlayerId id){return nbCardsInHand.get(id);}
    public ReadOnlyIntegerProperty nbCarsInHand(PlayerId id){return nbCarsInHand.get(id);}
    public ReadOnlyIntegerProperty nbConstructionPoints(PlayerId id){return nbConstructionPoints.get(id);}

    public ObservableList<Ticket> playerTickets(){return playerTickets;}
    public ReadOnlyIntegerProperty cardsOfInHand(Card card){return cardsOfInHand.get(card);}
    public ReadOnlyBooleanProperty canClaimRoute(Route route){return canClaimRoute.get(route);}

    public String faceUpCardName(int slot){return publicGameState.cardState().faceUpCard(slot).name();} //toDo comment faire ce truc d'une façon plus jolie ?
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
     * (pourcentage de a dans b)
     * peut donc prendre une valeur entre 0 et 1
     * C'est pour cela qu'on vérifie que a est plus petit ou égal à b*/
    private static Integer percent(int a, int b) {
        Preconditions.checkArgument(a<=b);
        return (a * 100) / b;
    }   //TODO pourrait-on garder entre 0 et 1 ? -enlever le *100 ? non parce qu on veut une valeur entre 0 et 100
    
    
    /**méthode qui retourne vraie que si le joueur peut actuellement s'emparer de la route
     * Cette méthode prend listePaireStations en paramètre car elle a pour objectif d'etre appelé dans le corps
     * de modifyCanClaimRoute et qu'il serait couteux de créer cette liste de liste de station pour chaques routes*/
    private boolean claimable(Route route, List<List<Station>> listePaireStations) {
        return playerState.canClaimRoute(route)
                && publicGameState.currentPlayerId() == playerId 
                && freeRoute(route, listePaireStations);

    }
    
    /**méthode qui retourne un ReadOnlyBooleanProperty: contient la valeur true que si le joueur peut actuellement s'emparer de la route
     * Ce qui est déterminé lors des updates de canClaimRoute
     * Cette méthode est appelé dans MapViewCreator
     * */
    public ReadOnlyBooleanProperty claimable(Route route) {
        return canClaimRoute.get(route);
    }
    
    /**
     * créé une liste contenant les paires de stations des routes possédé par les joueurs
     * */
    
    private List<List<Station>> listePaireStations(List<Route> claimedRoutes){
        List<List<Station>> listeStations = new ArrayList<>();
        for (Route route : claimedRoutes) {
            listeStations.add(route.stations());
        }
        return listeStations;
    }
    
    /**retourne vraie si la route n'appartient à personne et, 
     * dans le cas d'une route double, sa voisine non plus
     * */
    private boolean freeRoute(Route paramRoute, List<List<Station>> listePaireStations) {
        Boolean output = true; //true si la route et sa voisine (si elle en a) n'appartient à personne
        
        for(List<Station> paireStation : listePaireStations) {
            if(paireStation.contains(paramRoute.station1()) //contains fait appel à equals donc ici nous avons un test par référence et non par structure
                    && paireStation.contains(paramRoute.station2())) {//néanmoins ceci n'est pas problématique car nous nous assurons qu’à chaque gare correspond un seul objet
                                                                      //ainsi égalité par référence = égalité structurelle
                output = false;
            }
        }
       return output;
    }
}
