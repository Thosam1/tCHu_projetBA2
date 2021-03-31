package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.Implement;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
//    @Test
    @Test
    void testingIllegalExceptionFirstLessPlay(){

    }
    @Test
    void testingIllegalExceptionFirstMorePlay(){

    }
    @Test
    void testingIllegalExceptionSecondLessPlay(){

    }
    @Test
    void testingIllegalExceptionSecondMorePlay(){

    }
    @Test
    void testingIllegalExceptionBothLessPlay(){

    }
    @Test
    void testingIllegalExceptionBothMorePlay(){

    }


    @Test
    void testingPlay(){
        Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1, new Implement(PlayerId.PLAYER_1, "Thösam"), PlayerId.PLAYER_2, new Implement(PlayerId.PLAYER_2, "Aymeric"));
        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "Thösam", PlayerId.PLAYER_2, "Aymeric");
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Random rng = new Random();

        Game.play(players, playerNames, tickets, rng);
//        assertEquals();
//        assertEquals();
    }
}
