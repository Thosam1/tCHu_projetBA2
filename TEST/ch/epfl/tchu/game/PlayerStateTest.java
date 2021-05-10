package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {
    @Test
    public void ConstructorTest(){
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
        }

    @Test
    public void InitialTest(){
        SortedBag<Card> initialCards = SortedBag.of(1, Card.VIOLET, 3, Card.GREEN);
        PlayerState test = PlayerState.initial(initialCards);
        assertTrue((test.ticketCount() == 0) && (test.routes().size() == 0));
    }
    @Test
    public void InitialArgExceptionLessTest(){
        SortedBag<Card> initialCards = SortedBag.of(3, Card.GREEN);

        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState test = PlayerState.initial(initialCards);
        });
    }
    @Test
    public void InitialArgExceptionMoreTest(){
        SortedBag<Card> initialCards = SortedBag.of(2, Card.VIOLET, 3, Card.GREEN);

        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState test = PlayerState.initial(initialCards);
        });
    }

    @Test
    public void ticketsTest(){
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

        assertEquals(test.tickets(), tickets);
    }
    @Test
    public void withAddedTicketsTest(){
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
        List<Ticket> toAdd = new ArrayList<>();
        toAdd.add(ChMap.tickets().get(1));
        toAdd.add(ChMap.tickets().get(2));
        test = test.withAddedTickets(SortedBag.of(toAdd));

        assertTrue((test.ticketCount() == 3) && (test.routes().size() == 2) && test.cardCount() == 6);
    }

    @Test
    public void cardsTest(){
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

        assertEquals(test.cards(), cards);
    }

    @Test
    public void withAddedCardTest(){
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

        test = test.withAddedCard(Card.LOCOMOTIVE);

        assertTrue((test.ticketCount() == 1) && (test.routes().size() == 2) && test.cardCount() == 7);
    }

    @Test
    public void canClaimRouteFalseWagon(){  // FUCKING FORGOT THIS ONE !!!

    }
    @Test
    public void canClaimRouteTrueEnoughBasicCaseTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.VIOLET)); // VIOLET
        cardList.add(Card.of(Color.VIOLET));
        cardList.add(Card.of(Color.VIOLET));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
//        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertTrue(test.canClaimRoute(ChMap.routes().get(3)));  // need 2 violets
    }
    @Test
    public void canClaimRouteTrueMoreCaseTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.VIOLET)); // 3 RED
        cardList.add(Card.of(Color.VIOLET));
        cardList.add(Card.of(Color.VIOLET));
        cardList.add(Card.of(Color.VIOLET));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
//        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertTrue(test.canClaimRoute(ChMap.routes().get(3)));  // need 2 violets
    }

    @Test
    public void canClaimRouteFalseCaseTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 2 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.VIOLET));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertFalse(test.canClaimRoute(ChMap.routes().get(3)));  // need 2 violets
    }
    @Test
    public void canClaimRouteTrueOvergroundColorNullTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 3 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertTrue(test.canClaimRoute(ChMap.routes().get(16)));  // need 4 "null" and Overground uhhh
    }
    @Test
    public void canClaimRouteTrueOvergroundColorNullFalseTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 3 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertFalse(test.canClaimRoute(ChMap.routes().get(16)));  // need 4 "null" and Overground uhhh
    }
    @Test
    public void canClaimRouteTrueUndergroundColorNullTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 3 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertTrue(test.canClaimRoute(ChMap.routes().get(22)));  // need 6 "null" and Overground uhhh
    }
    @Test
    public void canClaimRouteTrueUndergroundColorNullFalseTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 3 RED
        cardList.add(Card.of(null));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertFalse(test.canClaimRoute(ChMap.routes().get(22)));  // need 6 "null" and Overground uhhh
    }
    @Test
    public void canClaimRouteTrueUndergroundColorFalseTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(null));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertTrue(test.canClaimRoute(ChMap.routes().get(6)));  // need 2 "yellow" and under uhhh
    }
    @Test
    public void canClaimRouteAlreadyClaimedTest(){  // not sure ?   - this is not the place where we should check whether we already claimed it or not
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 3 RED                                                          // !!!!!!!!!!!!!!!!!!!!
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertTrue(test.canClaimRoute(ChMap.routes().get(2)));  // need 3 red
    }

    @Test
    public void possibleClaimCardsArgExceptionTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 2 RED
        cardList.add(Card.of(Color.RED));

        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));


        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        for(int i = 0; i < 17; i ++){
            routes.add(ChMap.routes().get(i));
        }    // until 16 (36)


        PlayerState test = new PlayerState(tickets, cards, routes);

        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleClaimCards(ChMap.routes().get(22));    // length 6
        });
    }
    @Test
    public void possibleClaimCardsArgException2Test(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 2 RED
        cardList.add(Card.of(Color.RED));

        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));


        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        for(int i = 0; i < 18; i ++){
            routes.add(ChMap.routes().get(i));
        }    // until 16 (40)


        PlayerState test = new PlayerState(tickets, cards, routes);

        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleClaimCards(ChMap.routes().get(22));    // length 6
        });
    }

    @Test
    public void possibleClaimCardsArgExceptionNullColorTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 2 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));

        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertFalse(test.canClaimRoute(ChMap.routes().get(2)));  // need 3 red
    }

    @Test
    public void possibleClaimCardsTestMore(){
//        assertEquals("je met une erreur pour que tu ne l oublies pas", ""); // Merci :D
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.LOCOMOTIVE);
        cardList.add(Card.LOCOMOTIVE);
        cardList.add(Card.LOCOMOTIVE);

        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);


        List<Card> temp1 = new ArrayList<>();
        temp1.add(Card.RED);temp1.add(Card.RED);temp1.add(Card.RED);
        List<SortedBag<Card>> cardBagList1 = new ArrayList<>();
        cardBagList1.add(SortedBag.of(temp1));

        List<Card> temp2 = new ArrayList<>();
        temp2.add(Card.LOCOMOTIVE);temp2.add(Card.RED);temp2.add(Card.RED);
        cardBagList1.add(SortedBag.of(temp2));

        List<Card> temp3 = new ArrayList<>();
        temp3.add(Card.LOCOMOTIVE);temp3.add(Card.LOCOMOTIVE);temp3.add(Card.RED);
        cardBagList1.add(SortedBag.of(temp3));

        List<Card> temp4 = new ArrayList<>();
        temp4.add(Card.LOCOMOTIVE);temp4.add(Card.LOCOMOTIVE);temp4.add(Card.LOCOMOTIVE);
        cardBagList1.add(SortedBag.of(temp4));


        assertEquals(test.possibleClaimCards(ChMap.routes().get(2)), cardBagList1);  // need 3 red
    }

    @Test
    public void possibleClaimCardsTest(){
//        assertEquals("je met une erreur pour que tu ne l oublies pas", ""); // Merci :D
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));

        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);


        List<Card> temp = new ArrayList<>();
        temp.add(Card.RED);temp.add(Card.RED);temp.add(Card.RED);

        List<SortedBag<Card>> cardBagList = new ArrayList<>();
        cardBagList.add(SortedBag.of(temp));

        assertEquals(test.possibleClaimCards(ChMap.routes().get(2)), cardBagList);  // need 3 red

    }

    @Test
    public void possibleClaimCardsEmptyTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertEquals(test.possibleClaimCards(ChMap.routes().get(2)), new ArrayList<>());
    }
    @Test
    public void possibleClaimCardsEmpty1Test(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 2 RED
        cardList.add(Card.of(Color.RED));

        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));


        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);

        assertEquals(test.possibleClaimCards(ChMap.routes().get(3)), new ArrayList<>());
    }
    @Test
    public void possibleClaimCardsEmpty2Test(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 1 RED - 1 loco
        cardList.add(Card.LOCOMOTIVE);

        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));


        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);

        assertEquals(test.possibleClaimCards(ChMap.routes().get(2)), new ArrayList<>());
    }
    @Test
    public void possibleClaimEmpty3Test(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.LOCOMOTIVE); // 2 loco


        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));


        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);

        assertEquals(test.possibleClaimCards(ChMap.routes().get(2)), new ArrayList<>());
    }
    // à partir de là stp

    
    @Test
    public void possibleAdditionalCardsTest(){  // !!! ordre
        SortedBag<Ticket> tickets = SortedBag.of();
        List<Route> routes = new ArrayList<>();
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(Card.BLACK); //utilisé
        builder.add(Card.BLACK); //utilisé
        builder.add(Card.BLACK); //utilisé
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.LOCOMOTIVE); //utilisé
        builder.add(Card.LOCOMOTIVE); //utilisé
        builder.add(Card.LOCOMOTIVE);
        builder.add(Card.LOCOMOTIVE);
        builder.add(Card.LOCOMOTIVE);
        builder.add(Card.GREEN);
        builder.add(Card.YELLOW);
        SortedBag<Card> cards = builder.build();
        
        PlayerState test = new PlayerState(tickets, cards, routes);
        
        int additionalCardsCount = 3;
        SortedBag<Card> initialCards = SortedBag.of(3, Card.BLACK, 2, Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = SortedBag.of(1,Card.RED, 2, Card.BLACK);
        
        List<SortedBag<Card>> output = test.possibleAdditionalCards(additionalCardsCount, initialCards);
        
        SortedBag<Card> bag1 = SortedBag.of(3,Card.BLACK);
        SortedBag<Card> bag2 = SortedBag.of(2,Card.BLACK,1,Card.LOCOMOTIVE);
        SortedBag<Card> bag3 = SortedBag.of(1,Card.BLACK,2,Card.LOCOMOTIVE);
        SortedBag<Card> bag4 = SortedBag.of(3,Card.LOCOMOTIVE);
        
        assertEquals(output, List.of(bag1, bag2, bag3, bag4));
    }

    @Test
    public void possibleAdditionalCardsArgException1Test(){ 
        SortedBag<Ticket> tickets = SortedBag.of();
        List<Route> routes = new ArrayList<>();
        SortedBag<Card> cards = SortedBag.of();
        PlayerState test = new PlayerState(tickets, cards, routes);
        
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(0, SortedBag.of(Card.BLUE));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(4, SortedBag.of(Card.BLUE)) ;
        });
        assertThrows(IllegalArgumentException.class, () -> {
            SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
            builder.add(Card.BLACK);
            builder.add(Card.LOCOMOTIVE);
            builder.add(Card.GREEN);
            builder.add(Card.YELLOW);
            SortedBag<Card> cardsTest = builder.build();
            test.possibleAdditionalCards(3, cardsTest) ;
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(3, SortedBag.of(Card.BLUE)) ;
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(3, SortedBag.of(Card.BLUE)) ;
        });
        
        
    }

    @Test
    public void withClaimedRouteTest(){
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.BLUE);
        builder.add(Card.BLUE);
        builder.add(Card.BLUE);
        builder.add(Card.LOCOMOTIVE);
        SortedBag<Card> cards = builder.build();
        SortedBag<Ticket> tickets = SortedBag.of();
        List<Route> routes = new ArrayList<>();
        PlayerState test = new PlayerState(tickets, cards, routes);
        
        Route route1 = new Route("", new Station(0,""),new Station(1,""), 2, Level.OVERGROUND, Color.BLUE);
        SortedBag<Card> claimCards = SortedBag.of(2,Card.BLUE);
        
        PlayerState output = test.withClaimedRoute(route1, claimCards);
        
        //for assertEquals
        SortedBag.Builder<Card> builderOutput = new SortedBag.Builder<>();
        builderOutput.add(Card.BLACK);
        builderOutput.add(Card.BLACK);
        builderOutput.add(Card.BLUE);
        builderOutput.add(Card.LOCOMOTIVE);
        SortedBag<Card> outputCards = builderOutput.build();
        assertEquals(output.cards(), outputCards);
        assertEquals(output.routes(), List.of(route1));
    }

    @Test
    public void ticketPointsTest(){
        Route route1 = new Route("", new Station(0,""),new Station(1,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route2 = new Route("", new Station(1,""),new Station(2,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route3 = new Route("", new Station(2,""),new Station(3,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route4 = new Route("", new Station(4,""),new Station(5,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route5 = new Route("", new Station(5,""),new Station(6,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route6 = new Route("", new Station(7,""),new Station(8,""), 2, Level.OVERGROUND, Color.BLUE);
        SortedBag<Card> cards = SortedBag.of();
        Ticket ticket1 = new Ticket(new Station(0,""), new Station(3,""),4); //+4
        Ticket ticket2 = new Ticket(new Station(1,""), new Station(3,""),4); //+4
        Ticket ticket3 = new Ticket(new Station(4,""), new Station(5,""),6); //+6
        Ticket ticket4 = new Ticket(new Station(4,""), new Station(3,""),8); //-8
        Ticket ticket5 = new Ticket(new Station(10,""), new Station(11,""),10); //-10
        
        Trip trip1 = new Trip(new Station(0,""), new Station(7, ""), 14);
        Trip trip2 = new Trip(new Station(3,""), new Station(4,""), 10);
        Ticket ticket6 = new Ticket(List.of(trip1, trip2)); //-10
        
        SortedBag.Builder<Ticket> builder = new SortedBag.Builder<>();
        builder.add(ticket1);
        builder.add(ticket2);
        builder.add(ticket3);
        builder.add(ticket4);
        builder.add(ticket5);
        builder.add(ticket6);
        
        List<Route> routes = List.of(route1,route2,route3,route4,route5,route6);
        PlayerState state = new PlayerState(builder.build(),cards,routes);
        assertEquals(-14,state.ticketPoints());
    }

    @Test
    public void finalPointsTest(){
        Route route1 = new Route("", new Station(0,""),new Station(1,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route2 = new Route("", new Station(1,""),new Station(2,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route3 = new Route("", new Station(2,""),new Station(3,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route4 = new Route("", new Station(4,""),new Station(5,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route5 = new Route("", new Station(5,""),new Station(6,""), 2, Level.OVERGROUND, Color.BLUE);
        Route route6 = new Route("", new Station(7,""),new Station(8,""), 2, Level.OVERGROUND, Color.BLUE);
        SortedBag<Card> cards = SortedBag.of();
        Ticket ticket1 = new Ticket(new Station(0,""), new Station(3,""),4); //+4
        Ticket ticket2 = new Ticket(new Station(1,""), new Station(3,""),4); //+4
        Ticket ticket3 = new Ticket(new Station(4,""), new Station(5,""),6); //+6
        Ticket ticket4 = new Ticket(new Station(4,""), new Station(3,""),8); //-8
        Ticket ticket5 = new Ticket(new Station(10,""), new Station(11,""),10); //-10
        SortedBag.Builder<Ticket> builder = new SortedBag.Builder<>();
        builder.add(ticket1);
        builder.add(ticket2);
        builder.add(ticket3);
        builder.add(ticket4);
        builder.add(ticket5);
        
        List<Route> routes = List.of(route1,route2,route3,route4,route5,route6);
        PlayerState state = new PlayerState(builder.build(),cards,routes);
        assertEquals(8,state.finalPoints());
    }
}
