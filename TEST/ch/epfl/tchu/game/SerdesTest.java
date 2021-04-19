package ch.epfl.tchu.game;
import ch.epfl.tchu.SortedBag;

import ch.epfl.tchu.net.Serdes;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SerdesTest {
    @Test
    void PublicCardState(){

    }
    @Test
    void PublicPlayerState(){

    }

    @Test
    void PlayerState(){

    }

    // j'ai pas fait ceux du haut parce que ça reprends à peu près tout ce qui est en dessous

    @Test
    void PublicGameStateTestSerialise(){
        List<Card> fu = List.of(Card.of(Color.RED), Card.of(Color.WHITE), Card.of(Color.BLUE), Card.of(Color.BLACK), Card.of(Color.RED));
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null);

        assertEquals(Serdes.serdePublicGameState.serialize(gs), "40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:");
    }

    @Test
    void PublicGameStateTestDeserialise(){
        String encoded = "40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:";
        PublicGameState test = Serdes.serdePublicGameState.deserialize(encoded);

        assertEquals(test.ticketsCount(), 40);

        assertEquals(test.cardState().faceUpCards().size(), 5);
        assertTrue(test.cardState().faceUpCards().contains(Card.of(Color.RED)));
        assertTrue(test.cardState().faceUpCards().contains(Card.of(Color.BLACK)));

        assertEquals(test.cardState().deckSize(), 30);
        assertEquals(test.cardState().discardsSize(), 31);

        assertEquals(test.currentPlayerId(), PlayerId.PLAYER_2);

        assertEquals(test.playerState(PlayerId.PLAYER_1).ticketCount(), 10);
        assertEquals(test.playerState(PlayerId.PLAYER_1).cardCount(), 11);
        assertEquals(test.playerState(PlayerId.PLAYER_1).routes().size(), 2);

        assertEquals(test.playerState(PlayerId.PLAYER_2).ticketCount(), 20);
        assertEquals(test.playerState(PlayerId.PLAYER_2).cardCount(), 21);
        assertEquals(test.playerState(PlayerId.PLAYER_2).routes().size(), 0);

        assertEquals(test.lastPlayer(), null);
    }

    @Test
    void PublicGameStateTestSerialise2(){
        Map<PlayerId, PublicPlayerState> validPlayerState = new EnumMap<>(PlayerId.class);
        int ticketCountPlayer1 = 1;
        int ticketCountPlayer2 = 2;
        int cardCountPlayer1 = 1;
        int cardCountPlayer2 = 2;
        Route route1 = ChMap.routes().get(15);
        List<Route> listeDeRoutes1 = List.of(route1);
        Route route2 = ChMap.routes().get(10);
        List<Route> listeDeRoutes2 = new ArrayList<>();
        listeDeRoutes2.add(route2);
        listeDeRoutes2.add(ChMap.routes().get(29));

        PublicPlayerState playerState1 = new PublicPlayerState(ticketCountPlayer1, cardCountPlayer1, listeDeRoutes1);
        PublicPlayerState playerState2 = new PublicPlayerState(ticketCountPlayer2, cardCountPlayer2, listeDeRoutes2);

        validPlayerState.put(PlayerId.PLAYER_1, playerState1);
        validPlayerState.put(PlayerId.PLAYER_2, playerState2);

        PublicCardState cardStateTest1 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(),0,0);
        PublicCardState cardStateTest2 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 5, 0);
        PublicCardState cardStateTest3 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 0, 5);

        PublicGameState test = new PublicGameState(0, cardStateTest1, PlayerId.PLAYER_1, validPlayerState, PlayerId.PLAYER_1);
        PublicGameState test2 = new PublicGameState(5, cardStateTest2, PlayerId.PLAYER_1, validPlayerState, null);
        PublicGameState test3 = new PublicGameState(1, cardStateTest3, PlayerId.PLAYER_2, validPlayerState, null);

        String encodedTest = "0:2,2,2,2,2;0;0:0:1;1;15:2;2;10,29:0";
        String encodedTest2 = "5:2,2,2,2,2;5;0:0:1;1;15:2;2;10,29:";
        String encodedTest3 = "1:2,2,2,2,2;0;5:1:1;1;15:2;2;10,29";

        assertEquals(encodedTest, Serdes.serdePublicGameState.serialize(test));
        assertEquals(encodedTest, Serdes.serdePublicGameState.serialize(test2));
        assertEquals(encodedTest, Serdes.serdePublicGameState.serialize(test3));
    }
    @Test
    void PublicGameStateTestDeserialise2(){
        String encodedTest = "0:2,2,2,2,2;0;0:0:1;1;15:2;2;10,29:0";
        String encodedTest2 = "5:2,2,2,2,2;5;0:0:1;1;15:2;2;10,29:";
        String encodedTest3 = "1:2,2,2,2,2;0;5:1:1;1;15:2;2;10,29";

        PublicGameState test = Serdes.serdePublicGameState.deserialize(encodedTest);
        PublicGameState test2 = Serdes.serdePublicGameState.deserialize(encodedTest2);
        PublicGameState test3 = Serdes.serdePublicGameState.deserialize(encodedTest3);



        assertEquals(test.ticketsCount(), 0);

        assertEquals(test.cardState().faceUpCards().size(), 5);
        assertTrue(test.cardState().faceUpCards().contains(Card.of(Color.BLUE)));
        assertTrue(!test.cardState().faceUpCards().contains(Card.of(Color.GREEN)));

        assertEquals(test.cardState().deckSize(), 0);
        assertEquals(test.cardState().discardsSize(), 0);

        assertEquals(test.currentPlayerId(), PlayerId.PLAYER_1);

        assertEquals(test.playerState(PlayerId.PLAYER_1).ticketCount(), 1);
        assertEquals(test.playerState(PlayerId.PLAYER_1).cardCount(), 1);
        assertEquals(test.playerState(PlayerId.PLAYER_1).routes().size(), 1);

        assertEquals(test.playerState(PlayerId.PLAYER_2).ticketCount(), 2);
        assertEquals(test.playerState(PlayerId.PLAYER_2).cardCount(), 2);
        assertEquals(test.playerState(PlayerId.PLAYER_2).routes().size(), 2);

        assertEquals(test.lastPlayer(), PlayerId.PLAYER_1);

        // ----

        assertEquals(test2.ticketsCount(), 5);

        assertEquals(test2.cardState().faceUpCards().size(), 5);
        assertTrue(test2.cardState().faceUpCards().contains(Card.of(Color.BLUE)));
        assertTrue(!test2.cardState().faceUpCards().contains(Card.of(Color.GREEN)));

        assertEquals(test2.cardState().deckSize(), 5);
        assertEquals(test2.cardState().discardsSize(), 0);

        assertEquals(test2.currentPlayerId(), PlayerId.PLAYER_1);

        assertEquals(test2.playerState(PlayerId.PLAYER_1).ticketCount(), 1);
        assertEquals(test2.playerState(PlayerId.PLAYER_1).cardCount(), 1);
        assertEquals(test2.playerState(PlayerId.PLAYER_1).routes().size(), 1);

        assertEquals(test2.playerState(PlayerId.PLAYER_2).ticketCount(), 2);
        assertEquals(test2.playerState(PlayerId.PLAYER_2).cardCount(), 2);
        assertEquals(test2.playerState(PlayerId.PLAYER_2).routes().size(), 2);

        assertEquals(test2.lastPlayer(), null);

        // ----
        assertEquals(test3.ticketsCount(), 1);

        assertEquals(test3.cardState().faceUpCards().size(), 5);
        assertTrue(test3.cardState().faceUpCards().contains(Card.of(Color.BLUE)));
        assertTrue(!test3.cardState().faceUpCards().contains(Card.of(Color.GREEN)));

        assertEquals(test3.cardState().deckSize(), 0);
        assertEquals(test3.cardState().discardsSize(), 5);

        assertEquals(test3.currentPlayerId(), PlayerId.PLAYER_2);

        assertEquals(test3.playerState(PlayerId.PLAYER_1).ticketCount(), 1);
        assertEquals(test3.playerState(PlayerId.PLAYER_1).cardCount(), 1);
        assertEquals(test3.playerState(PlayerId.PLAYER_1).routes().size(), 1);

        assertEquals(test3.playerState(PlayerId.PLAYER_2).ticketCount(), 2);
        assertEquals(test3.playerState(PlayerId.PLAYER_2).cardCount(), 2);
        assertEquals(test3.playerState(PlayerId.PLAYER_2).routes().size(), 2);

        assertEquals(test3.lastPlayer(), null);

    }
}
