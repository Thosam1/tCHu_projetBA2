package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    @Test
    void allTest(){
        List<Player.TurnKind> all = new ArrayList<>();

        all.add(Player.TurnKind.DRAW_TICKETS);
        all.add(Player.TurnKind.DRAW_CARDS);
        all.add(Player.TurnKind.CLAIM_ROUTE);

        assertEquals(all, Player.TurnKind.ALL);
    }
}
