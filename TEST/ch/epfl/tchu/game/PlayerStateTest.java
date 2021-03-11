package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
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

    @Test
    public void possibleAdditionalCardsTest(){  // !!! ordre

    }

    @Test
    public void possibleAdditionalCardsArgException1Test(){ //2, 3, 4, ...

    }

    @Test
    public void withClaimedRouteTest(){

    }

    @Test
    public void ticketPointsTest(){

    }

    @Test
    public void finalPointsTest(){

    }
}
