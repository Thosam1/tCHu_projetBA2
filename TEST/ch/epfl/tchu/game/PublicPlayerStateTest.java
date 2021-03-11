package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PublicPlayerStateTest {
    @Test
    public void ConstructorTicketArgExceptionTest(){
        Route route = ChMap.routes().get(0);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);

        assertThrows(IllegalArgumentException.class, () -> {
            PublicPlayerState test = new PublicPlayerState(-1, 5, routeList);
        });
    }
    @Test
    public void ConstructorCardsArgExceptionTest(){
        Route route = ChMap.routes().get(0);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);

        assertThrows(IllegalArgumentException.class, () -> {
            PublicPlayerState test = new PublicPlayerState(1, -20, routeList);
        });
    }
    @Test
    public void ConstructorCardsTest(){
        Route route = ChMap.routes().get(0);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);

        assertDoesNotThrow( () -> {
            PublicPlayerState test = new PublicPlayerState(0, 0, null);
        });
    }

    @Test
    public void ticketCountTest(){
        Route route = ChMap.routes().get(0);
        List<Route> routeList = new ArrayList<>();

        PublicPlayerState test = new PublicPlayerState(2, 0, routeList);
        assertEquals(test.ticketCount(), 2);
    }

    @Test
    public void cardCountTest(){
        Route route = ChMap.routes().get(0);
        List<Route> routeList = new ArrayList<>();

        PublicPlayerState test = new PublicPlayerState(2, 55, routeList);
        assertEquals(test.cardCount(), 55);
    }

    @Test
    public void routesSingleTest(){
        Route route = ChMap.routes().get(0);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route);

        PublicPlayerState test = new PublicPlayerState(2, 55, routeList);
        assertEquals(test.routes(), Collections.singletonList(route));
    }
    @Test
    public void routesMultipleTest(){
        Route route1 = ChMap.routes().get(0);
        Route route2 = ChMap.routes().get(5);
        Route route3 = ChMap.routes().get(10);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route1);
        routeList.add(route2);
        routeList.add(route3);

        List<Route> routeTest = new ArrayList<>();
        routeList.add(route2);
        routeList.add(route3);
        routeList.add(route1);


        PublicPlayerState test = new PublicPlayerState(2, 55, routeList);
//        assertEquals(test.routes(), routeTest);
        assertTrue(test.routes().equals(routeTest));

    }
    @Test
    public void routesNullTest(){   // QUESTION WHAT IF

        PublicPlayerState test = new PublicPlayerState(2, 55, null);
        assertEquals(test.routes(), null);
    }
    @Test
    public void routesZeroTest(){   // QUESTION WHAT IF
        PublicPlayerState test = new PublicPlayerState(2, 55, new ArrayList<Route>());
        assertEquals(test.routes(), new ArrayList<Route>());
    }

    @Test
    public void carCountNoRoutesNullTest(){ // ? with null or zero ?
        PublicPlayerState test = new PublicPlayerState(2, 55, null);
        assertEquals(test.carCount(), Constants.INITIAL_CAR_COUNT);
    }
    @Test
    public void carCountNoRoutesEmptyTest(){ // ? with null or zero ?
        PublicPlayerState test = new PublicPlayerState(2, 55, new ArrayList<Route>());
        assertEquals(test.carCount(), Constants.INITIAL_CAR_COUNT);
    }
    @Test
    public void carCountManyRoutesTest(){
        Route route1 = ChMap.routes().get(0);
        Route route2 = ChMap.routes().get(5);
        Route route3 = ChMap.routes().get(10);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route1);
        routeList.add(route2);
        routeList.add(route3);

        int shouldBe = Constants.INITIAL_CAR_COUNT - route1.length() - route2.length() - route3.length();
        PublicPlayerState test = new PublicPlayerState(2, 55, routeList);
        assertEquals(test.carCount(), shouldBe);
    }

    @Test
    public void claimPointsZeroTest(){
        PublicPlayerState test = new PublicPlayerState(2, 55, new ArrayList());
        assertEquals(test.claimPoints(), 0);
    }
    @Test
    public void claimPointsNullTest(){
        PublicPlayerState test = new PublicPlayerState(2, 55, null);
        assertEquals(test.claimPoints(), 0);
    }
    @Test
    public void claimPointsTest(){
        Route route1 = ChMap.routes().get(0);
        Route route2 = ChMap.routes().get(5);
        Route route3 = ChMap.routes().get(10);
        List<Route> routeList = new ArrayList<>();
        routeList.add(route1);
        routeList.add(route2);
        routeList.add(route3);

        int shouldBe = route1.claimPoints() + route2.claimPoints() + route3.claimPoints();
        PublicPlayerState test = new PublicPlayerState(2, 55, routeList);
        assertEquals(test.claimPoints(), shouldBe);
    }
}
