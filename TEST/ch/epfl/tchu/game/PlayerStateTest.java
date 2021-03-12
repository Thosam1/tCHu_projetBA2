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

    }
    @Test
    public void InitialArgExceptionTest(){

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

    }

    @Test
    public void withAddedCardsTest(){

    }

    @Test
    public void canClaimRouteTrueEnoughCaseTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 3 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(Color.RED));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertTrue(test.canClaimRoute(ChMap.routes().get(2)));  // need 3 red
    }
    @Test
    public void canClaimRouteTrueMoreCaseTest(){
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
        assertTrue(test.canClaimRoute(ChMap.routes().get(2)));  // need 3 red
    }

    @Test
    public void canClaimRouteFalseCaseTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 2 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertFalse(test.canClaimRoute(ChMap.routes().get(2)));  // need 3 red
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
        assertTrue(test.canClaimRoute(ChMap.routes().get(11)));  // need 4 "null" and Overground uhhh
    }
    @Test
    public void canClaimRouteAlreadyClaimedTest(){  // not sure ?

    }

    @Test
    public void possibleClaimCardsArgExceptionTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 2 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertFalse(test.canClaimRoute(ChMap.routes().get(2)));  // need 3 red
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleClaimCards(ChMap.routes().get(2));
        });
    }
    @Test
    public void possibleClaimCardsArgExceptionNullColorTest(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));

        List<Card> cardList = new ArrayList<>();
        cardList.add(Card.of(Color.RED)); // 2 RED
        cardList.add(Card.of(Color.RED));
        cardList.add(Card.of(null));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));
        cardList.add(Card.of(Color.GREEN));

        cardList.add(Card.of(null));    // locomotive
        SortedBag<Card> cards = SortedBag.of(cardList);

        List<Route> routes = new ArrayList<>();
        routes.add(ChMap.routes().get(2));
        routes.add(ChMap.routes().get(8));

        PlayerState test = new PlayerState(tickets, cards, routes);
        assertFalse(test.canClaimRoute(ChMap.routes().get(2)));  // need 3 red
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleClaimCards(ChMap.routes().get(11));
        });
    }

    @Test
    public void possibleClaimCardsTest(){
        
    }

    // à partir de là stp

    
    @Test
    public void possibleAdditionalCardsTest(){  // !!! ordre
        //a mettre dans une méthode main
        SortedBag<Ticket> tickets = SortedBag.of();
        List<Route> routes = new ArrayList<>();
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.LOCOMOTIVE);
        builder.add(Card.LOCOMOTIVE);
        builder.add(Card.LOCOMOTIVE);
        builder.add(Card.GREEN);
        builder.add(Card.YELLOW);
        SortedBag<Card> cards = builder.build();
        
        PlayerState test = new PlayerState(tickets, cards, routes);
        
        int additionalCardsCount = 2;
        SortedBag<Card> initialCards = SortedBag.of(3, Card.BLACK, 2, Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = SortedBag.of(3,Card.RED);
        
        List<SortedBag<Card>> output = test.possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);
        for(SortedBag<Card> bag : output) {
            System.out.println(bag.toString());
        }
        assertEquals("je met ça parcequ il y a un probleme avec cette méthode", "");
    }

    @Test
    public void possibleAdditionalCardsArgException1Test(){ 
        SortedBag<Ticket> tickets = SortedBag.of();
        List<Route> routes = new ArrayList<>();
        SortedBag<Card> cards = SortedBag.of();
        PlayerState test = new PlayerState(tickets, cards, routes);
        
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(0, SortedBag.of(Card.BLUE), SortedBag.of(3,Card.GREEN));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(4, SortedBag.of(Card.BLUE), SortedBag.of(3,Card.GREEN)) ;
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(3, null, SortedBag.of(3,Card.GREEN)) ;
        });
        assertThrows(IllegalArgumentException.class, () -> {
            SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
            builder.add(Card.BLACK);
            builder.add(Card.LOCOMOTIVE);
            builder.add(Card.GREEN);
            builder.add(Card.YELLOW);
            SortedBag<Card> cardsTest = builder.build();
            test.possibleAdditionalCards(3, cardsTest, SortedBag.of(3,Card.GREEN)) ;
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(3, SortedBag.of(Card.BLUE), SortedBag.of(4,Card.GREEN)) ;
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test.possibleAdditionalCards(3, SortedBag.of(Card.BLUE), SortedBag.of(2,Card.GREEN)) ;
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
        assertEquals(8,state.ticketPoints());
    }
}
