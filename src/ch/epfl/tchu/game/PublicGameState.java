package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.tchu.Preconditions;

/**
 * Classe GameState représente la partie publique de l'état d'une partie de tCHu.
 * @author Aymeric de chillaz (326617)
 * @author Thösam Norlha-Tsang (330163) (juste les commentaires)
 */
public class PublicGameState {
    private int ticketsCount;
    private PublicCardState cardState;
    private PlayerId currentPlayerId;
    private PlayerId lastPlayer;
    private Map<PlayerId, PublicPlayerState> playerState;

    /**
     * Constructeur public de publicGameState :
     *     construit la partie publique de l'état d'une partie de tCHu
     *     dans laquelle la pioche de billets a une taille de ticketsCount,
     *     l'état public des cartes wagon/locomotive est cardState,
     *     le joueur courant est currentPlayerId, l'état public des joueurs est contenu dans playerState,
     *     et l'identité du dernier joueur est lastPlayer (qui peut être null
     *     si cette identité est encore inconnue)
     * @param ticketsCount
     * @param cardState
     * @param currentPlayerId
     * @param playerState
     * @param lastPlayer
     * @throws IllegalArgumentException si la taille de la pioche est strictement négative ou si playerState ne contient pas exactement deux paires clef/valeur
     * @throws NullPointerException si l'un des autres arguments (lastPlayer excepté!) est nul
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, 
            PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, 
            PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount>=0);
        if((cardState==null)||(currentPlayerId==null)||(playerState==null)) {    
            throw new NullPointerException();
        }
        //Tres important que cette precondition soit après
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);
        
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer;
    }

    /**
     * @return la taille de la pioche de billets
     */
    public int ticketsCount() {return ticketsCount;}

    /**
     * @return vrai ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     */
    public boolean canDrawTickets() {return (ticketsCount != 0);}

    /**
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState() {return cardState;}

    /**
     * @return vrai ssi il est possible de tirer des cartes, c-à-d si la pioche et la défausse contiennent entre elles au moins 5 cartes
     */
    public boolean canDrawCards() {
        return (cardState.discardsSize() + cardState.deckSize()>=5);
    }

    /**
     * @return l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {return currentPlayerId;}

    /**
     * @param playerId
     * @return partie publique de l'état du joueur d'identité donnée
     */
    public PublicPlayerState playerState(PlayerId playerId) {return playerState.get(playerId);}

    /**
     * @return la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {return playerState.get(currentPlayerId);}



    /**
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    /* je ne sais pas si ça va fonctionner comme les listes sont immuable*/
    public List<Route> claimedRoutes(){
        List<Route> routeNextPlayer = List.copyOf(playerState.get(PlayerId.ALL.get(0)).routes());
        List<Route> routeCurrentPlayer = List.copyOf(playerState.get(PlayerId.ALL.get(1)).routes());
        List<Route> routeOutput = new ArrayList<>(routeNextPlayer);
        routeOutput.addAll(routeCurrentPlayer);
        return routeOutput;
    }

    /**
     * @return l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas commencé
     */
    public PlayerId lastPlayer() {return lastPlayer;}
}
