package ch.epfl.tchu.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;

class PublicGameStateTest {

    @Test
    void PublicGameStateExceptionTest() {
        Map<PlayerId, PublicPlayerState> validPlayerState = new EnumMap<>(PlayerId.class);
        int ticketCountPlayer1 = 1;
        int ticketCountPlayer2 = 1;
        int cardCountPlayer1 = 1;
        int cardCountPlayer2 = 1;
        Route route1 = new Route("", new Station(1,""), new Station(1,""), 2, Level.OVERGROUND, Color.BLUE);
        List<Route> listeDeRoutes1 = List.of(route1);
        Route route2 = new Route("", new Station(1,""), new Station(1,""), 2, Level.OVERGROUND, Color.GREEN);
        List<Route> listeDeRoutes2 = List.of(route2);
       
        PublicPlayerState playerState1 = new PublicPlayerState(ticketCountPlayer1, cardCountPlayer1, listeDeRoutes1);
        PublicPlayerState playerState2 = new PublicPlayerState(ticketCountPlayer2, cardCountPlayer2, listeDeRoutes2);
        
        validPlayerState.put(PlayerId.PLAYER_1, playerState1);
        validPlayerState.put(PlayerId.PLAYER_2, playerState2);
  
        assertThrows(IllegalArgumentException.class, () -> {
            
            new PublicGameState(-1, new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 0, 0), PlayerId.PLAYER_1, validPlayerState, null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            Map<PlayerId, PublicPlayerState> notValidPlayerState = new EnumMap<>(PlayerId.class);
            notValidPlayerState.put(PlayerId.PLAYER_1, new PublicPlayerState(ticketCountPlayer1, cardCountPlayer1, listeDeRoutes1));
            new PublicGameState(0, new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 0, 0), PlayerId.PLAYER_1, notValidPlayerState, null);
        });
        
        assertThrows(NullPointerException.class, () -> {
            
            new PublicGameState(0, new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 0, 0), null, validPlayerState, null);
        });
        
        assertThrows(NullPointerException.class, () -> {
    
            new PublicGameState(0, null, PlayerId.PLAYER_1, validPlayerState, null);
});
        assertThrows(NullPointerException.class, () -> {
            
            new PublicGameState(0, new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 0, 0), PlayerId.PLAYER_1, null, null);
        });
        
        //Ici il ne devrait pas y avoir d exception lancé
        PublicCardState cardStateTest1 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(),0,0);
        PublicCardState cardStateTest2 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 5, 0);
        PublicCardState cardStateTest3 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 0, 5);

        PublicGameState test = new PublicGameState(0, cardStateTest1, PlayerId.PLAYER_1, validPlayerState, PlayerId.PLAYER_1);
        PublicGameState test2 = new PublicGameState(5, cardStateTest2, PlayerId.PLAYER_1, validPlayerState, null);
        PublicGameState test3 = new PublicGameState(1, cardStateTest3, PlayerId.PLAYER_2, validPlayerState, null);

        assertEquals(0,test.ticketsCount());
        assertEquals(5,test2.ticketsCount());

        assertEquals(false, test.canDrawTickets());
        assertEquals(true, test2.canDrawTickets());
        assertEquals(true, test3.canDrawTickets());

        assertEquals(cardStateTest1, test.cardState());
        assertEquals(cardStateTest2, test2.cardState());
        assertEquals(cardStateTest3, test3.cardState());

        assertEquals(false, test.canDrawCards());
        assertEquals(true, test2.canDrawCards());
        assertEquals(true, test3.canDrawCards());

        assertEquals(PlayerId.PLAYER_1, test.currentPlayerId());
        assertEquals(PlayerId.PLAYER_1, test2.currentPlayerId());
        assertEquals(PlayerId.PLAYER_2, test3.currentPlayerId());

        assertEquals(playerState2, test.playerState(PlayerId.PLAYER_2));
        assertEquals(playerState1, test2.playerState(PlayerId.PLAYER_1));
        assertEquals(playerState2, test3.playerState(PlayerId.PLAYER_2));

        assertEquals(playerState1, test.currentPlayerState());
        assertEquals(playerState1, test2.currentPlayerState());
        assertEquals(playerState2, test3.currentPlayerState());

        assertEquals(List.of(route1,route2), test.claimedRoutes());
        assertEquals(List.of(route1,route2), test2.claimedRoutes());
        assertEquals(List.of(route1,route2), test3.claimedRoutes());

        assertEquals(PlayerId.PLAYER_1, test.lastPlayer());
        assertEquals(null,  test2.lastPlayer());
        assertEquals(null, test3.lastPlayer());
    }
    }
