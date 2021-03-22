package ch.epfl.tchu.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;

class GameStateTest {

    @Test
    void GameState() {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        
        GameState gameState = GameState.initial(tickets, new Random());
        PlayerState playerState1 = gameState.playerState(PlayerId.PLAYER_1);
        PlayerState playerState2 = gameState.playerState(PlayerId.PLAYER_2);
        
        assertEquals(0, playerState1.tickets().size());
        assertEquals(0, playerState2.tickets().size());
        assertEquals(4, playerState1.cards().size());
        assertEquals(4, playerState2.cards().size());
        /*Great it is random
        System.out.println(gameState.topCard());*/
        
        assertEquals(false, gameState.lastTurnBegins());
        
        /*
         test with carCount set to 2 (it works)
        assertEquals(true, gameState.lastTurnBegins());*/
        
        GameState nextState = gameState.forNextTurn();
        assertEquals(gameState.currentPlayerId().next(), nextState.currentPlayerId());
        
        //only works if lastPlayer is public
        //assertEquals(gameState.lastPlayer, null);
        
        /*
        Je vérifie que les cartes ont été mélangé
        for(Card card : playerState1.cards()) {
            System.out.println(card);
        }
        for(Card card : playerState2.cards()) {
            System.out.println(card);
        }*/
        
        
        
        
        
        
    }

}
