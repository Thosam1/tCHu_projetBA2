package ch.epfl.tchu.game.net;
import ch.epfl.tchu.SortedBag;

import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.Serdes;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SerdesTest {
    @Test
    void TestNullValuesDeserialize() {
        assertEquals(null, Serdes.serdePlayerId.deserialize(""));
        assertEquals(null, Serdes.serdeRoute.deserialize(""));
        assertEquals(null, Serdes.serdeCard.deserialize(""));
        assertEquals(null, Serdes.serdeTurnKind.deserialize(""));
        assertEquals(null, Serdes.serdeTicket.deserialize(""));
        
    }
    
    @Test
    void TestNullValuesSerialize() {
        assertEquals("", Serdes.serdePlayerId.serialize(null));
        assertEquals("", Serdes.serdeRoute.serialize(null));
        assertEquals("", Serdes.serdeCard.serialize(null));
        assertEquals("", Serdes.serdeTurnKind.serialize(null));
        assertEquals("", Serdes.serdeTicket.serialize(null));
        
    }
    
    @Test
    void PublicCardStateSerialize1(){
        List<Card> fu = List.of(Card.of(Color.RED), Card.of(Color.WHITE), Card.of(Color.BLUE), Card.of(Color.BLACK), Card.of(Color.RED));
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        String encoded = "6,7,2,0,6;30;31";
        assertEquals(Serdes.serdePublicCardState.serialize(cs), encoded);
    }
    @Test
    void PublicCardStateDeserialize1(){
        String encoded = "6,7,2,0,6;30;31";
        PublicCardState test = Serdes.serdePublicCardState.deserialize(encoded);

        assertEquals(test.faceUpCards().size(), 5);
        assertTrue(test.faceUpCards().contains(Card.of(Color.RED)));
        assertTrue(test.faceUpCards().contains(Card.of(Color.BLACK)));

        assertEquals(test.deckSize(), 30);
        assertEquals(test.discardsSize(), 31);

    }
    @Test
    void PublicCardStateSerialize2(){
        PublicCardState cardStateTest1 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(),0,0);
        PublicCardState cardStateTest2 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 5, 0);
        PublicCardState cardStateTest3 = new PublicCardState(SortedBag.of(5,Card.BLUE).toList(), 0, 5);

        String encoded1 = "2,2,2,2,2;0;0";
        String encoded2 = "2,2,2,2,2;5;0";
        String encoded3 = "2,2,2,2,2;0;5";

        assertEquals(Serdes.serdePublicCardState.serialize(cardStateTest1), encoded1);
        assertEquals(Serdes.serdePublicCardState.serialize(cardStateTest2), encoded2);
        assertEquals(Serdes.serdePublicCardState.serialize(cardStateTest3), encoded3);
    }
    @Test
    void PublicCardStateDeserialize2(){
        String encoded1 = "2,2,2,2,2;0;0";
        String encoded2 = "2,2,2,2,2;5;0";
        String encoded3 = "2,2,2,2,2;0;5";

        PublicCardState test1 = Serdes.serdePublicCardState.deserialize(encoded1);
        PublicCardState test2 = Serdes.serdePublicCardState.deserialize(encoded2);
        PublicCardState test3 = Serdes.serdePublicCardState.deserialize(encoded3);

        assertEquals(test1.faceUpCards().size(), 5);
        assertTrue(test1.faceUpCards().contains(Card.of(Color.BLUE)));
        assertTrue(!test1.faceUpCards().contains(Card.of(Color.GREEN)));
        assertEquals(test1.deckSize(), 0);
        assertEquals(test1.discardsSize(), 0);

        assertEquals(test2.faceUpCards().size(), 5);
        assertTrue(test2.faceUpCards().contains(Card.of(Color.BLUE)));
        assertTrue(!test2.faceUpCards().contains(Card.of(Color.GREEN)));
        assertEquals(test2.deckSize(), 5);
        assertEquals(test2.discardsSize(), 0);

        assertEquals(test3.faceUpCards().size(), 5);
        assertTrue(test3.faceUpCards().contains(Card.of(Color.BLUE)));
        assertTrue(!test3.faceUpCards().contains(Card.of(Color.GREEN)));
        assertEquals(test3.deckSize(), 0);
        assertEquals(test3.discardsSize(), 5);
    }

    @Test
    void PublicPlayerStateSerialize1(){
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        PublicPlayerState test1 = new PublicPlayerState(10, 11, rs1);
        PublicPlayerState test2 = new PublicPlayerState(20, 21, List.of());

        String encoded1 = "10;11;0,1";
       // String encoded2 = "20;21";     //TODO Ici, check comment le codé sera avec une liste vide en dernier    - ou rajouter ; à la fin ???
        String encoded2 = "20;21;";
        assertEquals(encoded1, Serdes.serdePublicPlayerState.serialize(test1));
        assertEquals(encoded2, Serdes.serdePublicPlayerState.serialize(test2));
    }
    @Test
    void PublicPlayerStateDeserialize1(){
        String encoded1 = "10;11;0,1";
        //String encoded2 = "20;21";     //TODO Ici, check comment le codé sera avec une liste vide en dernier
        String encoded2 = "20;21;"; //il faut mettre un ; a la fin meme si la liste est vide
        //ne pas mettre d'espace après le dernier ";"
        
        PublicPlayerState test1 = Serdes.serdePublicPlayerState.deserialize(encoded1);
        PublicPlayerState test2 = Serdes.serdePublicPlayerState.deserialize(encoded2);

        assertEquals(test1.ticketCount(), 10);
        assertEquals(test1.cardCount(), 11);
        assertEquals(test1.routes().size(), 2);

        assertEquals(test2.ticketCount(), 20);
        assertEquals(test2.cardCount(), 21);
        assertEquals(test2.routes().size(), 0); //comment faire la diff entre un 0 et un null ??? en bas pour lastPlayer null etc.. ou liste vide ou pas de route

    }
    @Test
    void PublicPlayerStateSerialize2(){
        String encoded1 = "1;1;15";   // on fait séparément et pas la map toute seule ici
        String encoded2 = "2;2;10,29";

        int ticketCountPlayer1 = 1;
        int cardCountPlayer1 = 1;
        Route route1 = ChMap.routes().get(15);
        List<Route> listeDeRoutes1 = List.of(route1);

        int ticketCountPlayer2 = 2;
        int cardCountPlayer2 = 2;
        Route route2 = ChMap.routes().get(10);
        List<Route> listeDeRoutes2 = new ArrayList<>();
        listeDeRoutes2.add(route2);
        listeDeRoutes2.add(ChMap.routes().get(29));

        PublicPlayerState test1 = new PublicPlayerState(ticketCountPlayer1, cardCountPlayer1, listeDeRoutes1);
        PublicPlayerState test2 = new PublicPlayerState(ticketCountPlayer2, cardCountPlayer2, listeDeRoutes2);

        assertEquals(encoded1, Serdes.serdePublicPlayerState.serialize(test1));
        assertEquals(encoded2, Serdes.serdePublicPlayerState.serialize(test2));
    }
    @Test
    void PublicPlayerStateDeserialize2(){
        String encoded1 = "1;1;15";   // on fait séparément et pas la map toute seule ici
        String encoded2 = "2;2;10,29";
        PublicPlayerState test1 = Serdes.serdePublicPlayerState.deserialize(encoded1);
        PublicPlayerState test2 = Serdes.serdePublicPlayerState.deserialize(encoded2);

        assertEquals(test1.ticketCount(), 1);
        assertEquals(test1.cardCount(), 1);
        assertEquals(test1.routes().size(), 1);
        
        
        assertEquals(ChMap.routes().get(15).id(), test1.routes().get(0).id());//on compare les ids sinon ça retourne faux
        //pourquoi ChMap.routes().get(0) et pas get(15)
        assertEquals(test2.ticketCount(), 2);
        assertEquals(test2.cardCount(), 2);
        assertEquals(test2.routes().size(), 2);
        assertEquals(ChMap.routes().get(10).id(), test2.routes().get(0).id());//on compare les ids sinon ça retourne faux
        assertEquals(ChMap.routes().get(29).id(), test2.routes().get(1).id());

    }

    @Test
    void PlayerStateSerialize(){
        String encoded1 = "0;0,2,3,3,3,8;2,8";

        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));
        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.GREEN)); // 3 greens
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.BLUE));  // 1 blue
        cardList.add(Card.of(Color.BLACK)); // 1 black
        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);
        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);

        assertEquals(encoded1, Serdes.serdePlayerState.serialize(test));
    }
    @Test
    void PlayerStateDeserialize(){
        String encoded1 = "0;0,2,3,3,3,8;2,8";

        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.GREEN)); // 3 greens
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.BLUE));  // 1 blue
        cardList.add(Card.of(Color.BLACK)); // 1 black
        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList); //-> in order so 0 2 333 8

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = Serdes.serdePlayerState.deserialize(encoded1);

        assertEquals(test.tickets().size(), 1);
        assertEquals(ChMap.tickets().get(0), test.tickets().get(0));

        assertEquals(6, cardList.size());
        assertTrue(test.cards().contains(cards));

        assertEquals(2, test.routes().size());
        /*assertEquals(ChMap.routes().get(2), test.routes().get(0));
        assertEquals(ChMap.routes().get(8), test.routes().get(8));*/
        assertEquals(ChMap.routes().get(2), test.routes().get(0));
        assertEquals(ChMap.routes().get(8), test.routes().get(1));
    }


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
        //String encodedTest3 = "1:2,2,2,2,2;0;5:1:1;1;15:2;2;10,29"; //Est ce que tu as fait expres de ne pas mettre : à la fin
        String encodedTest3 = "1:2,2,2,2,2;0;5:1:1;1;15:2;2;10,29:"; 
        assertEquals(encodedTest, Serdes.serdePublicGameState.serialize(test));
        assertEquals(encodedTest2, Serdes.serdePublicGameState.serialize(test2));
        assertEquals(encodedTest3, Serdes.serdePublicGameState.serialize(test3));
    }
    @Test
    void PublicGameStateTestDeserialise2(){
        String encodedTest = "0:2,2,2,2,2;0;0:0:1;1;15:2;2;10,29:0";
        String encodedTest2 = "5:2,2,2,2,2;5;0:0:1;1;15:2;2;10,29:";
        //String encodedTest3 = "1:2,2,2,2,2;0;5:1:1;1;15:2;2;10,29"; //il faut mettre : à la fin
        String encodedTest3 = "1:2,2,2,2,2;0;5:1:1;1;15:2;2;10,29:";
        
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
