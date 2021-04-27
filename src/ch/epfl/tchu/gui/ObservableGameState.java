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

    private final Map<Route, ObjectProperty<PlayerId>> routeOwners; //TODO can we change values if we declare it final, casse l'immuabilité?  Oui // vérifier que ce n'est pas ObjectProperty<PlayerId> effectivement -> seulement le deuxième


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
    private final Map<Card, ObjectProperty<Integer>> cardsOfInHand;
    private final Map<Route, ObjectProperty<Boolean>> canClaimRoute;   //TODO vérifer que c'est bien les deux ObjectProperty<Route> ? ou pas nécessaire pour route

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

        this.playerTickets = new ArrayList<>(); //TODO question
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

        //TODO modif cardsOfInHand
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
            SimpleObjectProperty<Card> card = new SimpleObjectProperty<>(); //TODO comme ça qu'il faut faire
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
    private Map<Card, Integer> createCardsOfInHand(){
        Map<Card, Integer> output = Map.of();
        for(Card card : Card.ALL){
            int count = 0;
            output.put(card, playerState.cards().countOf(card));  //TODO verify
        }
        return output;
    }

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
