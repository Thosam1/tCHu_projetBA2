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
        
        
        //Je vérifie que les cartes ont été mélangé
        for(Card card : playerState1.cards()) {
            System.out.println(card);
        }
        for(Card card : playerState2.cards()) {
            System.out.println(card);
        }
        System.out.println("number of cards in deck = "+ gameState.cardState.deck.size() + "   " + Constants.TOTAL_CARDS_COUNT);
        //TODO Ils n ont pas l'air mélangé
        System.out.println("nombre de tickets dans tickets = "+tickets.size());
        System.out.println("Debut test topTickets");
        //Je vérifie que topTicket fonctionne et que les tickets ont été mélangé
        for(Ticket ticket : gameState.topTickets(10)) {
            System.out.println(ticket.text());
        }
        
        System.out.println();
        System.out.println("Debut test withoutTopTickets");
        //Je vérifie que withoutTopTickets fonctionne et que les tickets ont été mélangé
        for(Ticket ticket : gameState.withoutTopTickets(5).topTickets(tickets.size()-5)){
            System.out.println(ticket);
        }
        
        
        
        
        
    }

}
