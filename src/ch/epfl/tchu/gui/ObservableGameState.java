package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * représente l'état observable d'une partie de tCHu - une instance de ObservableGameState est spécifique à un joueur
 */
public class ObservableGameState {

    /**A la création, la totalité des propriétés de l'état sont null pour celles contenant un objet
     * 0 pour celles contenant un entrier, false pour celles contenant une valeur booléenne*/
    
    private static PlayerId playerId;
    private static PublicGameState publicGameState = null;
    private static PlayerState playerState = null;

    /**
     *  Propriétés concernant l'état public de la partie
     */
    
    private final ObjectProperty<Integer> percentTicketsLeft;
    
    private final ObjectProperty<Integer> percentCardsLeft;

    private final List<ObjectProperty<Card>> faceUpCards;       // TODO check -> mieux de stocket dans une array[5] de taille 5 ?

//    private final List<ObjectProperty<Route>> routeOwners =               // je pense qu'une map est plus adéquate pour s'y retrouver
//            createRouteOwners();
    private final Map<Route, ObjectProperty<PlayerId>> routeOwners; //TODO can we change values if we declare it final, casse l'immuabilité?  // vérifier que ce n'est pas ObjectProperty<PlayerId>


    /**
     *  Propriétés concernant l'état public des de chacun des joueurs
     *  propriété du Player1 à l index 0 et du Player2 à l index 1
     */ //TODO je pense qu'une map serait plus lisible et plus sûr, que parier sur l'index de la liste
    
    private final Map<PlayerId, ObjectProperty<Integer>> nbTicketsInHand;
    private final Map<PlayerId,ObjectProperty<Integer>> nbCardsInHand;
    private final Map<PlayerId,ObjectProperty<Integer>> nbCarsInHand;
    private final Map<PlayerId,ObjectProperty<Integer>> nbConstructionPoints;

    /**
     *  Propriétés concernant l'état complet du joueur auquel l'instance correspond
     */
    private final List<ObjectProperty<Ticket>> playerTickets;   //TODO quand c'est une liste pointer à quoi ??? ou les éléments null ?
//    private List<ObjectProperty<Integer>> cardsOfInHand = createCardsOfInHand();    //TODO je pense qu'ici c'est mieux d'utiliser une map pour ensuite changer etc...
    private final Map<Card, ObjectProperty<Integer>> cardsOfInHand;
//    private List<ObjectProperty<Boolean>> canClaimRoute = createCanClaimRoute();
    private final Map<Route, ObjectProperty<Boolean>> canClaimRoute;   //TODO vérifer que c'est bien les deux ObjectProperty<Route> ? ou pas nécessaire pour route
    
    /*private List<Ticket> playerTickets = new ArrayList<Ticket>(); //TODO faut-il initialiser à 0 ou null ?
    private Map<Card, Integer> cardsOf_inHand = Map.of(Card.BLACK, 0, Card.VIOLET, 0, Card.BLUE, 0, Card.GREEN, 0, Card.YELLOW, 0, Card.ORANGE, 0, Card.RED, 0, Card.WHITE, 0, Card.ORANGE, 0, Card.LOCOMOTIVE, 0);
    private Map<Route, Boolean> canClaimRoute;  //TODO good idea to initialise it in constructor
*/

    public ObservableGameState(PlayerId playerId){
        this.playerId = playerId;
        this.percentTicketsLeft = new SimpleObjectProperty<Integer>(0);
        this.percentCardsLeft = new SimpleObjectProperty<Integer>(0);
        this.faceUpCards = initFaceUpCards();
        this.routeOwners = initRouteOwners();

        this.nbTicketsInHand = intPropertyIdMap();
        this.nbCardsInHand = intPropertyIdMap();
        this.nbCarsInHand = intPropertyIdMap();
        this.nbConstructionPoints = intPropertyIdMap();

        this.playerTickets; //TODO question
        this.cardsOfInHand = initCardsOfInHand();
        this.canClaimRoute = initCanClaimRoute();
        //TODO tous les mettre dans le constructeur pour éviter de les rappeler à chaque fois
    }

    /**
     * Pour mettre à jour l'état que la classe contient + les propriétés
     * @param newGameState la partie publique du jeu
     * @param newPlayerState l'état complet du joueur auquel la classe correspond
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        this.publicGameState = newGameState;
        this.playerState = newPlayerState;
        percentTicketsLeft.set(pourcentage(newGameState.ticketsCount(), ChMap.tickets().size()));//est ce qu il existe une constante pour 46 et 110?    yesss
        percentCardsLeft.set(pourcentage(newGameState.cardState().deckSize(), Constants.ALL_CARDS.size()));
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
          }
        
        //TODO modif routeOwners

        for(PlayerId id: PlayerId.ALL) {
            PublicPlayerState state = publicGameState.playerState(id);
            nbTicketsInHand.get(id).set(state.ticketCount());   //TODO est-ce qu'on set bien des objectproperty?
            nbCardsInHand.get(id).set(state.cardCount());
            nbCarsInHand.get(id).set(state.carCount());
            nbConstructionPoints.get(id).set(state.claimPoints());
        }
        
//        for(Ticket ticket : playerState.tickets()) {
//            playerTickets.get //comment on fait pour ne pas créer des nouvelles instances et seulement modifier celles qui existent deja?
//        }
        
        //TODO modif cardsOfInHand
        
//        for (int i = 0; i < ChMap.routes().size(); ++i) {
//            if(publicGameState.currentPlayerId() == playerId && route appartient a personne //méthode privé à créer
//                    && playerState.canClaimRoute(ChMap.routes().get(i))) {
//                canClaimRoute.get(i).set(true);
//            }
//            else canClaimRoute.get(i).set(false);
//        }
        List<Ticket> tempPlayerTickets = createPlayerTickets();
        for(int i = 0; i < tempPlayerTickets.size(); i++){
            playerTickets.get(i).set(tempPlayerTickets.get(i));
        }
        Map<Card, Integer> tempCardsOfInHand = createCardsOfInHand();
        for(Map.Entry<Card, Integer> c : tempCardsOfInHand.entrySet()){
           cardsOfInHand.get(c).set(c.getValue());
        }
        Map<Route, Boolean> tempCanClaimRoute = createCanClaimRoute();
        for(Map.Entry<Route, Boolean> c : tempCanClaimRoute.entrySet()){
            canClaimRoute.get(c).set(c.getValue());
        }

        
    }


    /**
     *
     */
    private static Map<PlayerId, ObjectProperty<Integer>> intPropertyIdMap(){
        Map<PlayerId, ObjectProperty<Integer>> temp = Map.of();
        for(PlayerId id : PlayerId.ALL){
            temp.put(id, new SimpleObjectProperty<Integer>(0));
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
    private static Map<Card, ObjectProperty<Integer>> initCardsOfInHand() {
        Map<Card, ObjectProperty<Integer>> map = Map.of();
        for (Card card :Card.ALL) {
            map.put(card, new SimpleObjectProperty<Integer>(0));
        }
        return map;
    }
    private Map<Route, ObjectProperty<Boolean>> initCanClaimRoute(){
        Map<Route, ObjectProperty<Boolean>> map = Map.of();
        for(Route route : ChMap.routes()) {
            map.put(route, new SimpleObjectProperty<Boolean>(false));
        }
        return map;
    }

        /**
         *  méthodes statiques privées  pour la modification
         */
    private static SimpleObjectProperty<Integer> createPercentTicketsLeft(){
        Integer output = pourcentage(publicGameState.ticketsCount(), ChMap.tickets().size() );
        return new SimpleObjectProperty<>(output);
    }
    
    private static SimpleObjectProperty<Integer> createPercentCardsLeft(){
        Integer output = pourcentage(publicGameState.cardState().deckSize(), Constants.ALL_CARDS.size());
        return new SimpleObjectProperty<>(output);
    }
    
    private static List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> temp = new ArrayList<ObjectProperty<Card>>();
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            SimpleObjectProperty<Card> card = new SimpleObjectProperty<>(publicGameState.cardState().faceUpCard(i)); //TODO  -> au début on ne sait pas les cartes faces visibles
            temp.add(card);
        }
        return temp;
    }
    
//    private static List<ObjectProperty<Route>> createRouteOwners(){   //TODO SimpleObjectProperty<Route> or without "Simple"
//        List<ObjectProperty<Route>> liste = new ArrayList<ObjectProperty<Route>>(); //TODO maybe putting them into a map would be a better idea, we don't know if the routes orders will change
//        for(Route route : ChMap.routes()) {
//            if(publicGameState)
//            liste.add(new SimpleObjectProperty<>(publicGameState.)) ;
//        }
//    }
    private static Map<ObjectProperty<Route>, PlayerId> createRouteOwners(){
        Map<ObjectProperty<Route>, PlayerId> map = Map.of();
        for(Route route : ChMap.routes()) {
            if(publicGameState.playerState(PlayerId.PLAYER_1).routes().contains(route)){
                map.put(new SimpleObjectProperty<Route>(route), PlayerId.PLAYER_1);
            }else if(publicGameState.playerState(PlayerId.PLAYER_1).routes().contains(route)){
                map.put(new SimpleObjectProperty<Route>(route), PlayerId.PLAYER_2);
            }else {map.put(new SimpleObjectProperty<Route>(route), null);}
        }
        return map;
    }

    /**
     *  -   -   -   -   -       -   -   -   -   -       -   -   -   -   -   -   -
     */
    
    private static List<SimpleObjectProperty<Integer>> createNbTicketsInHand(){
        List<SimpleObjectProperty<Integer>> output = List.of(new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_1).ticketCount()),
                new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_2).ticketCount()));
        return output;
    }
    private static List<SimpleObjectProperty<Integer>> createNbCardsInHand(){
        List<SimpleObjectProperty<Integer>> output = List.of(new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_1).cardCount()),
                new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_2).cardCount()));
        return output;
    }
    private static List<SimpleObjectProperty<Integer>> createNbCarsInHand(){
        List<SimpleObjectProperty<Integer>> output = List.of(new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_1).carCount()),
                new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_2).carCount()));
        return output;
        }
    private static List<SimpleObjectProperty<Integer>> createNbConstructionPoints(){
        List<SimpleObjectProperty<Integer>> output = List.of(new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_1).claimPoints()),
                new SimpleObjectProperty<>(publicGameState.playerState(PlayerId.PLAYER_2).claimPoints()));
        return output;
        }
    
//    private static List<ObjectProperty<Ticket>> createPlayerTickets(){
//        List<ObjectProperty<Ticket>> output = new ArrayList<>();
//        for(Ticket ticket : playerState.tickets()) {
//            output.add(new SimpleObjectProperty<>(ticket));
//        }
//        return output;
//    }
    private static List<Ticket> createPlayerTickets(){  //sans objectproperty pour pouvoir utiliser dans setState
            List<Ticket> output = new ArrayList<>();
            for(Ticket ticket : playerState.tickets()) {
                output.add(ticket);
            }
            return output;
        }
    
    /**neuf propriétés contenant, pour chaque type de carte wagon/locomotive, 
     * le nombre de cartes de ce type que le joueur a en main
     * les retourne dans l ordre de l enumeration Card avec locomotive à la fin
     * */
//    private static List<SimpleObjectProperty<Card>> createCardsOfInHand(){
//        List<SimpleObjectProperty<Card>> output = new ArrayList<>();
//        for(Card card : Card.ALL){
//
//        }
//    }
//    private Map<Card, ObjectProperty<Integer>> createCardsOfInHand(){
//        Map<Card, ObjectProperty<Integer>> output = Map.of();
//        for(Card card : Card.ALL){
//            int count = 0;
//            output.put(card, new SimpleObjectProperty<>(playerState.cards().countOf(card)));  //TODO verify
//        }
//        return output;
//    }
    private Map<Card, Integer> createCardsOfInHand(){
        Map<Card, Integer> output = Map.of();
        for(Card card : Card.ALL){
            int count = 0;
            output.put(card, playerState.cards().countOf(card));  //TODO verify
        }
        return output;
    }
    
//    //liste dans l'ordre des routes donné par ChMap
//    private static List<SimpleObjectProperty<Ticket>> createCanClaimRoute(){
//        List<SimpleObjectProperty<Boolean>> output = new ArrayList<>();
//
//        for (Route route : ChMap.routes()) {
//            if(publicGameState.currentPlayerId() == playerId && route appartient a personne
//                    && playerState.canClaimRoute(route)) {
//                output.add(new SimpleObjectProperty(true));
//            }
//            else output.add(new SimpleObjectProperty(false));
//        }
//        return output;
//    }
//    private static Map<Route, ObjectProperty<Boolean>> createCanClaimRoute(){
//        Map<Route, ObjectProperty<Boolean>> output = Map.of();
//        for(Route route : ChMap.routes()) {
//            if(!publicGameState.playerState(PlayerId.PLAYER_1).routes().contains(route) && !publicGameState.playerState(PlayerId.PLAYER_2).routes().contains(route) && playerState.canClaimRoute(route)) {
//                output.put(route, new SimpleObjectProperty<Boolean>(true));
//            }else {output.put(route, new SimpleObjectProperty<Boolean>(false));}
//        }
//        return output;
//    }
    private static Map<Route, Boolean> createCanClaimRoute(){
            Map<Route, Boolean> output = Map.of();
            for(Route route : ChMap.routes()) {
                if(!publicGameState.playerState(PlayerId.PLAYER_1).routes().contains(route) && !publicGameState.playerState(PlayerId.PLAYER_2).routes().contains(route) && playerState.canClaimRoute(route)) {
                    output.put(route, true);
                }else {output.put(route, false);}
            }
            return output;
        }
    
    /**
     *  Getters des propriétés
     */

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }
    
    /**
     * méthode privée permettant de calculer un pourcentage*/
    private static Integer pourcentage(int a, int b) {
        return (a * 100) / b;
    }   //TODO pourrait-on garder entre 0 et 1 ? -enlever le *100 ?
}
