package tCHu_projetBA2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.game.Station;

class TestRoute {

    @Test
    public void constructorSameStation(){
        assertThrows(IllegalArgumentException.class, () -> {
      //  Station station = new Station(0,"test");
    //    new Route("id", station, station, 2, Level.OVERGROUND, Color.BLUE);
            Station station = new Station(0,"test");
            Route test = new Route("id", station, station, 2, Level.OVERGROUND, Color.BLUE);});
        }

    @Test
    public void constructorLengthAbove0(){
        assertThrows(IllegalArgumentException.class, () -> {
        //si ça ne fonctionnne pas essaye de ne pas mettre le message pour les deux
            new Route("id", new Station(0,"test"), new Station(1,"test"), 0, Level.OVERGROUND, Color.BLUE);
      });
    }

    @Test
    public void constructorLengthUnder7(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("id", new Station(0,"test"), new Station(1,"test"), 7, Level.OVERGROUND, Color.BLUE);
      });
    }

    @Test
    public void constructorIDNotNull(){
        assertThrows(NullPointerException.class, () -> {
            new Route(null, new Station(0,"test"), new Station(1,"test"), 2, Level.OVERGROUND, Color.BLUE);
      });
    }

    @Test
    public void constructorStation1NotNull(){
        assertThrows(NullPointerException.class, () -> {
            new Route("id", null , new Station(1,"test"), 3, Level.UNDERGROUND, Color.BLUE);
      });
    }

    @Test
    public void constructorStation2NotNull(){
        assertThrows(NullPointerException.class, () -> {
            new Route("id", new Station(0,"test"), null, 4, Level.OVERGROUND, Color.BLUE);
      });
    }

    @Test
    public void constructorLevelNotNull(){
        assertThrows(NullPointerException.class, () -> {
            new Route("id", new Station(0,"test"), new Station(1,"test"), 5, null, Color.BLUE);
      });
    }

    @Test
    public void constructorWorksWithNullColor(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 6, Level.OVERGROUND, null);
        assertEquals(null,routeTest.color());
    }
    @Test
    public void idWorks(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 6, Level.OVERGROUND, Color.BLUE);
        assertEquals("id",routeTest.id());
    }
    @Test
    public void station1Works(){
        Station station = new Station(0, "test");
        Route routeTest = new Route("id", station, new Station(1,"test"), 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(station, routeTest.station1());
    }
    @Test
    public void station2Works(){
        Station station = new Station ( 1, "Test");
        Route routeTest = new Route("id", new Station(0,"test"), station, 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(station, routeTest.station2());
    }
    @Test
    public void lengthWorks(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(6, routeTest.length());
    }
    @Test
    public void levelWorks(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(Level.OVERGROUND, routeTest.level());
    }
    @Test
    public void colorWorks(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(Color.BLUE, routeTest.color());
    }

    @Test
    public void stationsWorks(){
        Station station1 = new Station(0,"test1");
        Station station2 = new Station(1,"test2");
        Route routeTest = new Route("id", station1, station2, 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(List.of(station1, station2), routeTest.stations());
    }

    @Test
    public void stationOppositeWorksStation1(){
        Station station1 = new Station(0,"test1");
        Station station2 = new Station(1,"test2");
        Route routeTest = new Route("id", station1, station2, 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(station1, routeTest.stationOpposite(station2));
    }

    @Test
    public void stationOppositeWorksStation2(){
        Station station1 = new Station(0,"test1");
        Station station2 = new Station(1,"test2");
        Route routeTest = new Route("id", station1, station2, 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(station2, routeTest.stationOpposite(station1));
    }
    @Test
    public void stationOppositeNeitherFirstOrSecondStation(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 1, Level.UNDERGROUND, Color.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            routeTest.stationOpposite(new Station(3,"Different Station"));});
      }
    @Test
    public void claimPointsWorks(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 6, Level.OVERGROUND, Color.BLUE);
        assertEquals(15, routeTest.claimPoints());
    }

    @Test
    public void additionalClaimCardsErrorOVERGROUND(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 1, Level.OVERGROUND, Color.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            routeTest.additionalClaimCardsCount(SortedBag.of(Card.BLUE), SortedBag.of(3, Card.BLUE));});
      }
    @Test
    public void additionalClaimCardsErrorMoreThan3Cards(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 1, Level.UNDERGROUND, Color.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            routeTest.additionalClaimCardsCount(SortedBag.of(3,Card.BLUE), SortedBag.of(4,Card.BLUE));});
      }
    @Test
    public void additionalClaimCardsErrorLessThan3Cards(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 1, Level.UNDERGROUND, Color.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            routeTest.additionalClaimCardsCount(SortedBag.of(3,Card.BLUE), SortedBag.of(2,Card.BLUE));});
      }
    @Test
    public void possibleClaimCards(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 2, Level.UNDERGROUND, Color.BLUE);
            SortedBag<Card> bag1 = SortedBag.of(2,Card.BLUE);
            SortedBag<Card> bag2 = SortedBag.of(1,Card.BLUE, 1, Card.LOCOMOTIVE);
            SortedBag<Card> bag3 = SortedBag.of(2, Card.LOCOMOTIVE);
            assertEquals(List.of(bag1,bag2,bag3),routeTest.possibleClaimCards());
      }

    @Test
    public void possibleClaimCardsNeutralRoad(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 2, Level.UNDERGROUND, null);
            SortedBag<Card> bag1 = SortedBag.of(2,Card.BLACK);
            SortedBag<Card> bag2 = SortedBag.of(2,Card.VIOLET);
            SortedBag<Card> bag3 = SortedBag.of(2,Card.BLUE);
            SortedBag<Card> bag4 = SortedBag.of(2,Card.GREEN);
            SortedBag<Card> bag5 = SortedBag.of(2,Card.YELLOW);
            SortedBag<Card> bag6 = SortedBag.of(2,Card.ORANGE);
            SortedBag<Card> bag7 = SortedBag.of(2,Card.RED);
            SortedBag<Card> bag8 = SortedBag.of(2,Card.WHITE);

            SortedBag<Card> bag9 = SortedBag.of(1,Card.BLACK, 1, Card.LOCOMOTIVE);
            SortedBag<Card> bag10 = SortedBag.of(1,Card.VIOLET, 1, Card.LOCOMOTIVE);
            SortedBag<Card> bag11 = SortedBag.of(1,Card.BLUE, 1, Card.LOCOMOTIVE);
            SortedBag<Card> bag12 = SortedBag.of(1,Card.GREEN, 1, Card.LOCOMOTIVE);
            SortedBag<Card> bag13 = SortedBag.of(1,Card.YELLOW, 1, Card.LOCOMOTIVE);
            SortedBag<Card> bag14 = SortedBag.of(1,Card.ORANGE, 1, Card.LOCOMOTIVE);
            SortedBag<Card> bag15 = SortedBag.of(1,Card.RED, 1, Card.LOCOMOTIVE);
            SortedBag<Card> bag16 = SortedBag.of(1,Card.WHITE, 1, Card.LOCOMOTIVE);
            
            SortedBag<Card> bag17 = SortedBag.of(2, Card.LOCOMOTIVE);
            assertEquals(List.of(bag1,bag2,bag3, bag4, bag5, bag6, bag7, bag8, bag9, bag10, bag11, bag12, bag13, bag14, bag15, bag16, bag17), routeTest.possibleClaimCards());
      }
    
    @Test //drawn Cards trois locomotive
    public void additionalClaimCardsCountWorksTest1(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 2, Level.UNDERGROUND, Color.BLUE);
        SortedBag<Card> claimCards = SortedBag.of(2,Card.BLUE);
        SortedBag<Card> drawnCards = SortedBag.of(3, Card.LOCOMOTIVE);
        assertEquals(3, routeTest.additionalClaimCardsCount(claimCards,drawnCards));
    }

    @Test //pose deux locomotives et pioche 3 locomotives
    public void additionalClaimCardsCountWorksTest2(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 2, Level.UNDERGROUND, Color.BLUE);
        SortedBag<Card> claimCards = SortedBag.of(2, Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = SortedBag.of(3, Card.LOCOMOTIVE);
        assertEquals(3, routeTest.additionalClaimCardsCount(claimCards,drawnCards));
    }
    @Test //pose deux locomotives et pioche une locomotive
    public void additionalClaimCardsCountWorksTest3(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 2, Level.UNDERGROUND, Color.BLUE);
        SortedBag<Card> claimCards = SortedBag.of(2, Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = SortedBag.of(2,Card.BLUE,1, Card.LOCOMOTIVE);
        assertEquals(1, routeTest.additionalClaimCardsCount(claimCards,drawnCards));
    }
    
    @Test //pose deux bleu et pioche trois verte
    public void additionalClaimCardsCountWorksTest4(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 2, Level.UNDERGROUND, Color.BLUE);
        SortedBag<Card> claimCards = SortedBag.of(2,Card.BLUE);
        SortedBag<Card> drawnCards = SortedBag.of(3,Card.GREEN);
        assertEquals(0, routeTest.additionalClaimCardsCount(claimCards,drawnCards));
    }
    @Test //teste avec une route neutre
    public void additionalClaimCardsCountWorksTest5(){
        Route routeTest = new Route("id", new Station(0,"test"), new Station(1,"test"), 2, Level.UNDERGROUND, null);
        SortedBag<Card> claimCards = SortedBag.of(2, Card.GREEN);
        SortedBag<Card> drawnCards = SortedBag.of(2,Card.BLUE,1, Card.LOCOMOTIVE);
        assertEquals(1, routeTest.additionalClaimCardsCount(claimCards,drawnCards));
    }
}
