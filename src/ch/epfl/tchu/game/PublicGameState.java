package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.tchu.Preconditions;

public class PublicGameState {
    private int ticketsCount;
    private PublicCardState cardState;
    private PlayerId currentPlayerId;
    private PlayerId lastPlayer;
    private Map<PlayerId, PublicPlayerState> playerState;
  
    public PublicGameState(int ticketsCount, PublicCardState cardState, 
            PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, 
            PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount>=0);
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);
        if((cardState==null)||(currentPlayerId==null)) {
            throw new NullPointerException();
        }
        
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer;
    }
    
    public int ticketsCount() {return ticketsCount;}
    public boolean canDrawTickets() {return (ticketsCount != 0);}
    public PublicCardState cardState() {return cardState;}
    public boolean canDrawCards() {
        return (cardState.discardsSize() + cardState.deckSize()>=5);
    }
    public PlayerId currentPlayerId() {return currentPlayerId;}
    public PublicPlayerState playerState(PlayerId playerId) {return playerState.get(playerId);}
    public PublicPlayerState currentPlayerState() {return playerState.get(currentPlayerId);}
    
    /* je ne sais pas si ça va fonctionner comme les listes sont immuable*/
    public List<Route> claimedRoutes(){
        List<Route> routeNextPlayer = List.copyOf(playerState.get(PlayerId.ALL.get(0)).routes());
        List<Route> routeCurrentPlayer = List.copyOf(playerState.get(PlayerId.ALL.get(1)).routes());
        routeCurrentPlayer.addAll(routeNextPlayer);
        return routeCurrentPlayer;
    }
    
    public PlayerId lastPlayer() {return lastPlayer;}
}
