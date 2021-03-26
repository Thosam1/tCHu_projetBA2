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
        assertEquals(Constants.INITIAL_CARDS_COUNT, playerState1.cards().size());
        assertEquals(Constants.INITIAL_CARDS_COUNT, playerState2.cards().size());
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
    @Test
    void GameFirstHalf() {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        GameState gameState = GameState.initial(tickets, new Random());

        PlayerState playerState1 = gameState.playerState(PlayerId.PLAYER_1);
        PlayerState playerState2 = gameState.playerState(PlayerId.PLAYER_2);

        //  -   -   -   -

        assertEquals(gameState, gameState.withCardsDeckRecreatedIfNeeded(new Random()));

        GameState gameState2 = GameState.initial(tickets, new Random());


        int temp = gameState2.cardState().deckSize();
        for(int i = 0; i < temp; i++){
            gameState2 = gameState2.withMoreDiscardedCards(SortedBag.of(gameState2.topCard()));
            gameState2 = gameState2.withoutTopCard();

            System.out.println(gameState2.cardState().deckSize());
        }

        System.out.println(gameState2.cardState().deckSize());

        assertEquals(gameState2.cardState().isDeckEmpty(), true);
        int expected = Constants.ALL_CARDS.size() - 2 * Constants.INITIAL_CARDS_COUNT - Constants.FACE_UP_CARDS_COUNT;
        assertEquals(gameState2.withCardsDeckRecreatedIfNeeded(new Random()).cardState().deckSize(), expected);
    }

    @Test
    void withInitiallyChosenTicketsIllegal() {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        GameState gameState = GameState.initial(tickets, new Random());

        PlayerState playerState1 = gameState.playerState(PlayerId.PLAYER_1);
        PlayerState playerState2 = gameState.playerState(PlayerId.PLAYER_2);

        gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(ChMap.tickets().get(3)));
        final GameState gameState2 = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            gameState2.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(ChMap.tickets().get(0)));
        });
    }
    @Test
    void withInitiallyChosenTickets() {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        GameState gameState = GameState.initial(tickets, new Random());

        PlayerState playerState1 = gameState.playerState(PlayerId.PLAYER_1);
        PlayerState playerState2 = gameState.playerState(PlayerId.PLAYER_2);

        gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(ChMap.tickets().get(3)));
        gameState = gameState.withInitiallyChosenTickets(PlayerId.PLAYER_2, SortedBag.of(ChMap.tickets().get(4)));

        final GameState gameState2 = gameState;
        assertThrows(IllegalArgumentException.class, () -> {
            gameState2.withChosenAdditionalTickets(SortedBag.of(ChMap.tickets().get(0)), SortedBag.of(ChMap.tickets().get(5)));
        });
//        System.out.println(gameState.currentPlayerId());
        SortedBag<Ticket> drawn = SortedBag.of(ChMap.tickets().get(0));
        drawn = drawn.union(SortedBag.of(ChMap.tickets().get(10)));
//        System.out.println(drawn.size());
        gameState = gameState.withChosenAdditionalTickets(drawn, SortedBag.of(ChMap.tickets().get(10)));

        assertEquals(gameState.playerState(gameState.currentPlayerId()).tickets().size(), 2);


    }

    @Test
    void lastTwos() {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        GameState gameState = GameState.initial(tickets, new Random());

        PlayerState playerState1 = gameState.playerState(PlayerId.PLAYER_1);
        PlayerState playerState2 = gameState.playerState(PlayerId.PLAYER_2);


        GameState nextState = gameState.forNextTurn();
        assertEquals(gameState.currentPlayerId().next(), nextState.currentPlayerId());
    }

}
