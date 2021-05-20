package ch.epfl.tchu.game.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.*;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

//public class RemoteProxyTest {
//
//    @Test
//    void randomTest(){
//        player.initPlayers(
//                PlayerId.PLAYER_1,
//                Map.of(PlayerId.PLAYER_1, "Ada", PlayerId.PLAYER_2, "Charles"));
//    }
//
//    assertEquals("INIT_PLAYERS", MessageId.INIT_PLAYERS.name());
//}
