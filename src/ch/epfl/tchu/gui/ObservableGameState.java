package ch.epfl.tchu.gui;
import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
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
    
    private final IntegerProperty percentTicketsLeft;
    
    private final IntegerProperty percentCardsLeft;

    private final List<ObjectProperty<Card>> faceUpCards;       // TODO check -> mieux de stocket dans une array[5] de taille 5 ?

//    private final List<ObjectProperty<Route>> routeOwners =               // je pense qu'une map est plus adéquate pour s'y retrouver
//            createRouteOwners();
    private final Map<Route, ObjectProperty<PlayerId>> routeOwners; //TODO can we change values if we declare it final, casse l'immuabilité?  // vérifier que ce n'est pas ObjectProperty<PlayerId>


    /**
     *  Propriétés concernant l'état public des de chacun des joueurs
     *  propriété du Player1 à l index 0 et du Player2 à l index 1
     */ //TODO je pense qu'une map serait plus lisible et plus sûr, que parier sur l'index de la liste
    
    private final Map<PlayerId, IntegerProperty> nbTicketsInHand;
    private final Map<PlayerId, IntegerProperty> nbCardsInHand;
    private final Map<PlayerId, IntegerProperty> nbCarsInHand;
    private final Map<PlayerId, IntegerProperty> nbConstructionPoints;

    /**
     *  Propriétés concernant l'état complet du joueur auquel l'instance correspond
     */
    private final ObservableList<Ticket> playerTickets;   //TODO quand c'est une liste pointer à quoi ??? ou les éléments null ?
//    private List<ObjectProperty<Integer>> cardsOfInHand = createCardsOfInHand();    //TODO je pense qu'ici c'est mieux d'utiliser une map pour ensuite changer etc...
    private final Map<Card, IntegerProperty> cardsOfInHand;
//    private List<ObjectProperty<Boolean>> canClaimRoute = createCanClaimRoute();
    private final Map<Route, BooleanProperty> canClaimRoute;   //TODO vérifer que c'est bien les deux ObjectProperty<Route> ? ou pas nécessaire pour route
    
    /*private List<Ticket> playerTickets = new ArrayList<Ticket>(); //TODO faut-il initialiser à 0 ou null ?
    private Map<Card, Integer> cardsOf_inHand = Map.of(Card.BLACK, 0, Card.VIOLET, 0, Card.BLUE, 0, Card.GREEN, 0, Card.YELLOW, 0, Card.ORANGE, 0, Card.RED, 0, Card.WHITE, 0, Card.ORANGE, 0, Card.LOCOMOTIVE, 0);
    private Map<Route, Boolean> canClaimRoute;  //TODO good idea to initialise it in constructor
*/

  //comment faire pour que les valeurs par défaut soient 0 null etc. ?
    
    public ObservableGameState(PlayerId playerId){
        this.playerId = playerId;
        this.percentTicketsLeft = new SimpleIntegerProperty(0);
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
        //TODO tous les mettre dans le constructeur pour éviter de les rappeler à chaque fois
    }

  //comment faire pour que les valeurs par défaut soient 0 null etc. ?
    
    
    /**
     * Pour mettre à jour l'état que la classe contient + les propriétés
     * @param newGameState la partie publique du jeu
     * @param newPlayerState l'état complet du joueur auquel la classe correspond
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        this.publicGameState = newGameState;
        this.playerState = newPlayerState;
        percentTicketsLeft.set(pourcentage(newGameState.ticketsCount(), ChMap.tickets().size()));
        percentCardsLeft.set(pourcentage(newGameState.cardState().deckSize(), Constants.ALL_CARDS.size()));

        modifyFaceUpCards(faceUpCards);
        modifyRouteOwners(routeOwners);

      //est ce que ce ne serait pas plus propre en faisant un appel à des modify
        for(PlayerId id: PlayerId.ALL) {
            PublicPlayerState state = publicGameState.playerState(id);
            nbTicketsInHand.get(id).set(state.ticketCount());   //TODO est-ce qu'on set bien des objectproperty?
            nbCardsInHand.get(id).set(state.cardCount());
            nbCarsInHand.get(id).set(state.carCount());
            nbConstructionPoints.get(id).set(state.claimPoints());
        }
        
        modifyPlayerTickets(playerTickets);
        modifyCardsOfInHand(cardsOfInHand);
        modifyCanClaimRoute(canClaimRoute);

        }



    /**
     *
     */
    private static Map<PlayerId, IntegerProperty> intPropertyIdMap(){
        Map<PlayerId, IntegerProperty> temp = Map.of();
        for(PlayerId id : PlayerId.ALL){
            temp.put(id, new SimpleIntegerProperty(0));
        }
        return temp;
    }
    private static List<ObjectProperty<Card>> initFaceUpCards(){
        List<ObjectProperty<Card>> temp = new ArrayList<ObjectProperty<Card>>();
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            SimpleObjectProperty<Card> card = new SimpleObjectProperty<>();
            temp.add(card);
        }
        return temp;
    }
    private static Map<Route, ObjectProperty<PlayerId>> initRouteOwners(){
        Map<Route, ObjectProperty<PlayerId>> map = Map.of();
        for(Route route : ChMap.routes()) {
            map.put(route, new SimpleObjectProperty<>(null));
        }
        return map;
    }
    private static Map<Card, IntegerProperty> initCardsOfInHand() {
        Map<Card, IntegerProperty> map = Map.of();
        for (Card card :Card.ALL) {
            map.put(card, new SimpleIntegerProperty(0));
        }
        return map;
    }
    private Map<Route, BooleanProperty> initCanClaimRoute(){
        Map<Route, BooleanProperty> map = Map.of();
        for(Route route : ChMap.routes()) {
            map.put(route, new SimpleBooleanProperty(false));
        }
        return map;
    }

        /**
         *  méthodes privées  pour la modification
         */
    /*
    private SimpleIntegerProperty modifyPercentTicketsLeft(){
        Integer output = pourcentage(publicGameState.ticketsCount(), ChMap.tickets().size());
        return new SimpleIntegerProperty(output);
    }
    
    private SimpleObjectProperty<Integer> modifyPercentCardsLeft(){
        Integer output = pourcentage(publicGameState.cardState().deckSize(), Constants.ALL_CARDS.size());
        return new SimpleObjectProperty<>(output);
    }
    */
    private void modifyFaceUpCards(List<ObjectProperty<Card>> faceUpCards){
        for(int slot : Constants.FACE_UP_CARD_SLOTS){
            Card newCard = publicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
            }
    }
    
    private void modifyRouteOwners(Map<Route, ObjectProperty<PlayerId>> routeOwners){
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
    /*
    private static Map<ObjectProperty<Route>, PlayerId> createRouteOwners(){
        Map<ObjectProperty<Route>, PlayerId> map = Map.of();
        for(Route route : ChMap.routes()) {
            if(publicGameState.playerState(PlayerId.PLAYER_1).routes().contains(route)){
                map.put(new SimpleObjectProperty<Route>(route), PlayerId.PLAYER_1);
            }else if(publicGameState.playerState(PlayerId.PLAYER_2).routes().contains(route)){
                map.put(new SimpleObjectProperty<Route>(route), PlayerId.PLAYER_2);
            }else {map.put(new SimpleObjectProperty<Route>(route), null);}
        }
        return map;
    }
*/
    /**
     *  -   -   -   -   -       -   -   -   -   -       -   -   -   -   -   -   -
     */
   /* 
    private List<SimpleObjectProperty<Integer>> createNbTicketsInHand(){
        List<SimpleObjectProperty<Integer>> output = List.of(new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_1).ticketCount()),
                new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_2).ticketCount()));
        return output;
    }
    private List<SimpleObjectProperty<Integer>> createNbCardsInHand(){
        List<SimpleObjectProperty<Integer>> output = List.of(new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_1).cardCount()),
                new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_2).cardCount()));
        return output;
    }
    private List<SimpleObjectProperty<Integer>> createNbCarsInHand(){
        List<SimpleObjectProperty<Integer>> output = List.of(new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_1).carCount()),
                new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_2).carCount()));
        return output;
        }
    private List<SimpleObjectProperty<Integer>> createNbConstructionPoints(){
        List<SimpleObjectProperty<Integer>> output = List.of(new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_1).claimPoints()),
                new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_2).claimPoints()));
        return output;
        }
    */
    private void modifyPlayerTickets(ObservableList<Ticket> playerTickets){
        playerTickets.setAll(playerState.tickets().toList());
    }

    
    /**neuf propriétés contenant, pour chaque type de carte wagon/locomotive, 
     * le nombre de cartes de ce type que le joueur a en main
     * les retourne dans l ordre de l enumeration Card avec locomotive à la fin
     * */

    private void modifyCardsOfInHand(Map<Card, IntegerProperty> cardsOfInHand){
        for(Card card : Card.ALL){
            cardsOfInHand.get(card).set(playerState.cards().countOf(card));  //TODO verify
        }
    }
    
    /*
    private Map<Card, Integer> modifyCardsOfInHand(){
        Map<Card, Integer> output = Map.of();
        for(Card card : Card.ALL){
            output.put(card, playerState.cards().countOf(card));  //TODO verify
        }
        return output;
    }*/
    
    private void modifyCanClaimRoute(Map<Route, BooleanProperty> canClaimRoute){
        List<List<Station>> listePaireStations = listePaireStations(publicGameState.claimedRoutes());
        //cette liste est créé avant le for each pour ne pas avoir à en créer une nouvelle à chaque fois
        
        for(Route route : ChMap.routes()) {
            if(claimable(route, listePaireStations)) {
                canClaimRoute.get(route).set(true);
            }
            else {canClaimRoute.get(route).set(false);}
        }
    }
    
/*    private static Map<Route, Boolean> createCanClaimRoute(){
            Map<Route, Boolean> output = Map.of();
            for(Route route : ChMap.routes()) {
                if(!publicGameState.playerState(PlayerId.PLAYER_1).routes().contains(route) && !publicGameState.playerState(PlayerId.PLAYER_2).routes().contains(route) && playerState.canClaimRoute(route)) {
                    output.put(route, true);
                }else {output.put(route, false);}
            }
            return output;
        }*/
    
    /**
     *  Getters des propriétés
     */

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
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
    /**
     * méthode privée permettant de calculer un pourcentage qui retourne un int
     * (pourcentage de a dans b)
     * peut donc prendre une valeur entre 0 et 1
     * C'est pour cela qu'on vérifie que a est plus petit ou égal à b*/
    private static Integer pourcentage(int a, int b) {
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
