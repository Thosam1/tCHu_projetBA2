package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.*;
import javafx.*;
import javafx.beans.property.ObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * représente l'état observable d'une partie de tCHu - une instance de ObservableGameState est spécifique à un joueur
 */
public class ObservableGameState {

    private PlayerId playerId;
    private PublicGameState publicGameState = null;
    private PlayerState playerState = null;

    /**
     *  Propriétés concernant l'état public de la partie
     */
    private int percentTicketLeft_inDeck = 0;
    private int percentCardsLeft_inDeck = 0;
    private final List<ObjectProperty<Card>> faceUpCards =
            createFaceUpCards();       // TODO check -> mieux de stocket dans une array[5] de taille 5 ?


    private PlayerId route0 = null; //TODO en faire èplus de 70 ou plûtot faire une méthode : PLayerId route(Route route)
    // ... ---

    private Map<Route, PlayerId> routePossession;   // meilleur idée d'utiliser une map et l'initialiser dans le constructeur

    /**
     *  Propriétés concernant l'état public des de chacun des joueurs
     */

    private Map<PlayerId, Integer> nbTickets_inHand = Map.of(PlayerId.PLAYER_1, 0, PlayerId.PLAYER_2, 0);  //TODO comme ça ou faudrait-t-il en faire une variable différente pour chaque joueur
    private Map<PlayerId, Integer> nbCards_inHand = Map.of(PlayerId.PLAYER_1, 0, PlayerId.PLAYER_2, 0);     //TODO ou bien initialiser les 3 dans le constructeur (seulement une for loop)
    private Map<PlayerId, Integer> nbCars = Map.of(PlayerId.PLAYER_1, 0, PlayerId.PLAYER_2, 0);
    private Map<PlayerId, Integer> nbConstructionPoints = Map.of(PlayerId.PLAYER_1, 0, PlayerId.PLAYER_2, 0);

    /**
     *  Propriétés concernant l'état complet du joueur auquel l'instance correspond
     */
    private List<Ticket> playerTickets = new ArrayList<Ticket>(); //TODO faut-il initialiser à 0 ou null ?
    private Map<Card, Integer> cardsOf_inHand = Map.of(Card.BLACK, 0, Card.VIOLET, 0, Card.BLUE, 0, Card.GREEN, 0, Card.YELLOW, 0, Card.ORANGE, 0, Card.RED, 0, Card.WHITE, 0, Card.ORANGE, 0, Card.LOCOMOTIVE, 0);
    private Map<Route, Boolean> canClaimRoute;  //TODO good idea to initialise it in constructor


    public ObservableGameState(PlayerId playerId){
        this.playerId = playerId;
        for(PlayerId id : PlayerId.ALL){
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
        }
    }

    /**
     * Pour mettre à jour l'état que la classe contient + les propriétés
     * @param publicGameState la partie publique du jeu
     * @param playerState l'état complet du joueur auquel la classe correspond
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState){
        this.publicGameState = publicGameState;
        this.playerState = playerState;
        /**
         *  Propriétés concernant l'état public de la partie
         */

        /**
         *  Propriétés concernant l'état public des de chacun des joueurs
         */

        /**
         *  Propriétés concernant l'état complet du joueur auquel l'instance correspond
         */

    }

    /**
     *  Getters des propriétés
     */

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     *  méthodes statiques privées
     */
    private List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> temp = new ArrayList<ObjectProperty<Card>>();
        for(i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            ObjectProperty<Card> card = new
        }
    }
}
