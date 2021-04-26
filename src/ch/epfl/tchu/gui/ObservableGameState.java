package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.*;
import javafx.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    
    private final ObjectProperty<Integer> percentTicketsLeft =
    createPercentTicketsLeft();
    
    private final ObjectProperty<Integer> percentCardsLeft =
            createPercentCardsLeft();

    private final List<ObjectProperty<Card>> faceUpCards =
            createFaceUpCards();       // TODO check -> mieux de stocket dans une array[5] de taille 5 ?

    private final List<ObjectProperty<Route>> routeOwners =
            createRouteOwners(); 
    
    /*private PlayerId route0 = null; //TODO en faire èplus de 70 ou plûtot faire une méthode : PLayerId route(Route route)
    // ... ---*/

   // private Map<Route, PlayerId> routePossession;   // meilleur idée d'utiliser une map et l'initialiser dans le constructeur

    /**
     *  Propriétés concernant l'état public des de chacun des joueurs
     *  propriété du Player1 à l index 0 et du Player2 à l index 1
     */
    
    private final List<ObjectProperty<Integer>> nbTicketsInHand = createNbTicketsInHand();
    private final List<ObjectProperty<Integer>> nbCardsInHand = createNbCardssInHand();
    private final List<ObjectProperty<Integer>> nbCarsInHand = createNbCarsInHand();
    private final List<ObjectProperty<Integer>> nbConstructionPoints = createNbConstructionPoints();
    
    /*
    private Map<PlayerId, Integer> nbTickets_inHand = Map.of(PlayerId.PLAYER_1, 0, PlayerId.PLAYER_2, 0);  //TODO comme ça ou faudrait-t-il en faire une variable différente pour chaque joueur
    private Map<PlayerId, Integer> nbCards_inHand = Map.of(PlayerId.PLAYER_1, 0, PlayerId.PLAYER_2, 0);     //TODO ou bien initialiser les 3 dans le constructeur (seulement une for loop)
    private Map<PlayerId, Integer> nbCars = Map.of(PlayerId.PLAYER_1, 0, PlayerId.PLAYER_2, 0);
    private Map<PlayerId, Integer> nbConstructionPoints = Map.of(PlayerId.PLAYER_1, 0, PlayerId.PLAYER_2, 0);
*/
    /**
     *  Propriétés concernant l'état complet du joueur auquel l'instance correspond
     */
    private List<ObjectProperty<Ticket>> playerTickets = createPlayerTickets();
    private List<ObjectProperty<Integer>> cardsOfInHand = createCardsOfInHand();
    private List<ObjectProperty<Boolean>> canClaimRoute = createCanClaimRoute();
    
    /*private List<Ticket> playerTickets = new ArrayList<Ticket>(); //TODO faut-il initialiser à 0 ou null ?
    private Map<Card, Integer> cardsOf_inHand = Map.of(Card.BLACK, 0, Card.VIOLET, 0, Card.BLUE, 0, Card.GREEN, 0, Card.YELLOW, 0, Card.ORANGE, 0, Card.RED, 0, Card.WHITE, 0, Card.ORANGE, 0, Card.LOCOMOTIVE, 0);
    private Map<Route, Boolean> canClaimRoute;  //TODO good idea to initialise it in constructor
*/

    public ObservableGameState(PlayerId playerId){
        this.playerId = playerId;
        /*for(PlayerId id : PlayerId.ALL){
            nbTickets_inHand.put(id, 0);
            nbCards_inHand.put(id, 0);
            nbCars.put(id, 0);
            nbConstructionPoints.put(id, 0);
        }
        for(Route route : ChMap.routes()){
            routePossession.put(route, null);
            canClaimRoute.put(route, false);
        }
        for(Card card : Card.ALL){
            cardsOf_inHand.put(card, 0);
        }*/
    }

    /**
     * Pour mettre à jour l'état que la classe contient + les propriétés
     * @param publicGameState la partie publique du jeu
     * @param playerState l'état complet du joueur auquel la classe correspond
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState){
        this.publicGameState = newGameState;
        this.playerState = newPlayerState;
        
        percentTicketsLeft.set(pourcentage(newGameState.ticketsCount(), 46));//est ce qu il existe une constante pour 46 et 110?
        percentCardsLeft.set(pourcentage(newGameState.cardState().deckSize(), 110));
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
          }
        
        //TODO modif routeOwners
        
        
        for(int i = 0; i <= 1; ++i) {
            PublicPlayerState state = publicGameState.playerState(PlayerId.ALL.get(i));
            nbTicketsInHand.get(i).set(state.ticketCount());
            nbCardsInHand.get(i).set(state.cardCount());
            nbCarsInHand.get(i).set(state.carCount());
            nbConstructionPoints.get(i).set(state.claimPoints());
        }
        
        for(Ticket ticket : playerState.tickets()) {
            playerTickets.get //comment on fait pour ne pas créer des nouvelles instances et seulement modifier celles qui existent deja?
        }
        
        //TODO modif cardsOfInHand
        
        for (int i = 0; i < ChMap.routes().size(); ++i) {
            if(publicGameState.currentPlayerId() == playerId && route appartient a personne //méthode privé à créer
                    && playerState.canClaimRoute(ChMap.routes().get(i))) {
                canClaimRoute.get(i).set(true);
            }
            else canClaimRoute.get(i).set(false);
        }
        
        
        
    }

    
    

    /**
     *  méthodes statiques privées
     */
    private static SimpleObjectProperty<Integer> createPercentTicketsLeft(){
        Integer output = pourcentage(publicGameState.ticketsCount(), 46 );
        return new SimpleObjectProperty<>(output);
    }
    
    private static SimpleObjectProperty<Integer> createPercentCardsLeft(){
        Integer output = pourcentage(publicGameState.cardState().deckSize(), 110);
        return new SimpleObjectProperty<>(output);
    }
    
    private static List<SimpleObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> temp = new ArrayList<ObjectProperty<Card>>();
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            SimpleObjectProperty<Card> card = ; //TODO
        }
    }
    
    private static List<SimpleObjectProperty<Route>> createRouteOwners(){
        List<ObjectProperty<Route>> liste = new ArrayList<ObjectProperty<Route>>();
        for(Route route : ChMap.routes()) {
            liste.add(new SimpleObjectProperty<>(publicGameState.)) //TODO
        }
    }
    
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
    
    private static List<SimpleObjectProperty<Ticket>> createPlayerTickets(){
        List<SimpleObjectProperty<Ticket>> output = new ArrayList<>();
        for(Ticket ticket : playerState.tickets()) {
            output.add(new SimpleObjectProperty<>(ticket));
        }
        return output;
    }
    
    /**neuf propriétés contenant, pour chaque type de carte wagon/locomotive, 
     * le nombre de cartes de ce type que le joueur a en main
     * les retourne dans l ordre de l enumeration Card avec locomotive à la fin
     * */
    private static List<SimpleObjectProperty<Ticket>> createCardsOfInHand(){
        //TODO
    }
    
    //liste dans l'ordre des routes donné par ChMap
    private static List<SimpleObjectProperty<Ticket>> createCanClaimRoute(){
        List<SimpleObjectProperty<Boolean>> output = new ArrayList<>();
        
        for (Route route : ChMap.routes()) {
            if(publicGameState.currentPlayerId() == playerId && route appartient a personne
                    && playerState.canClaimRoute(route)) {
                output.add(new SimpleObjectProperty(true));
            }
            else output.add(new SimpleObjectProperty(false));
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
    }
}
