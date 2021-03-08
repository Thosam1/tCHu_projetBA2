package tCHu_projetBA2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.game.PlayerId;


class TestPlayerId {
    
    @Test
    public void TestAll() {
        assertEquals(List.of(PlayerId.PLAYER_1, PlayerId.PLAYER_2),PlayerId.ALL);
    }
    
    @Test
    public void TestCOUNT() {
        assertEquals(2, PlayerId.COUNT);
    }
    
    @Test
    public void TestNext1() {
        assertEquals(PlayerId.PLAYER_1, PlayerId.PLAYER_2.next());
    }
    @Test
    public void TestNext2() {
        assertEquals(PlayerId.PLAYER_2, PlayerId.PLAYER_1.next());
    }
}
