package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.StringsFr;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class InfoTest {
    @Test
    public void constructor() {
        Info player = new Info("Player");
        assertTrue(player.getPlayerName() == "Player");
    }

    @Test
    public void cardNameWithSingularBlue() {
        Card card = Card.of(Color.BLUE);
        String cardName = Info.cardName(card, 1);
        assertTrue(cardName == "bleue");
    }

    @Test
    public void cardNameWithMultipleBlue() {
        Card card = Card.of(Color.BLUE);
        String cardName = Info.cardName(card, 2);
        assertEquals(cardName, "bleues");
    }

    @Test
    public void cardNameWithSingularLocomotive() {
        Card card = Card.of(null);
        String cardName = Info.cardName(card, 1);
        assertEquals(cardName, "locomotive");
    }

    @Test
    public void cardNameWithMultipleLocomotive() {
        Card card = Card.of(null);
        String cardName = Info.cardName(card, 2);
        assertEquals(cardName, "locomotives");
    }

    @Test
    public void drawTest() {
        List<String> playerNames = new ArrayList<>();
        playerNames.add("Player1");
        playerNames.add("Player2");

        String draw = Info.draw(playerNames, 97);
        System.out.println(draw);
        assertEquals(draw , "\nPlayer1 et Player2 sont ex æqo avec 97 points !\n");
    }

    @Test
    public void willPlayFirstTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.willPlayFirst();
        assertEquals(test, "Player1 jouera en premier.\n\n");
    }

    @Test
    public void keptTicketsSingleTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.keptTickets(0);
        assertEquals(test , "Player1 a gardé 0 billets.\n");
    }
    @Test
    public void keptTicketsMultipleTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.keptTickets(5);
        assertEquals(test , "Player1 a gardé 5 billets.\n");
    }

    @Test
    public void canPlayTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.canPlay();
        assertEquals(test , "\nC'est à Player1 de jouer.\n");
    }

    @Test
    public void drewTicketsSingleTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.drewTickets(1);
        assertEquals(test , "Player1 a tiré 1 billet...\n" );
    }
    @Test
    public void drewTicketsMultipleTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.drewTickets(4);
        assertEquals(test , "Player1 a tiré 4 billets...\n" );
    }


    @Test
    public void drewBlindCardTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.drewBlindCard();
        assertEquals(test , "Player1 a tiré une carte de la pioche.\n");
    }

    @Test
    public void drewVisibleCardBLUETest() {
        Info playerInfo = new Info("Player1");
        Card card = Card.of(Color.BLUE);
        String test = playerInfo.drewVisibleCard(card);
        assertEquals(test , "Player1 a tiré une carte bleue visible.\n");
    }
    @Test
    public void drewVisibleCardLocomotiveTest() {
        Info playerInfo = new Info("Player1");
        Card card = Card.of(null);
        String test = playerInfo.drewVisibleCard(card);
        assertEquals(test , "Player1 a tiré une carte locomotive visible.\n");

    }

    @Test
    public void claimedRouteTest() {
        Info playerInfo = new Info("Player1");
        List<Card> cards = new ArrayList<>();
        cards.add(Card.of(Color.BLUE));
        cards.add(Card.of(Color.BLUE));
        cards.add(Card.of(Color.YELLOW));
        SortedBag<Card> initialCards = SortedBag.of(cards);
        String test = playerInfo.claimedRoute(ChMap.routes().get(16), initialCards);
        assertEquals(test , "Player1 a pris possession de la route Berne" + StringsFr.EN_DASH_SEPARATOR + "Lucerne au moyen de 2 bleues et 1 jaune.\n");
    }

    @Test
    public void attemptsTunnelClaimTest() {
        Info playerInfo = new Info("Player1");
        List<Card> cards = new ArrayList<>();
        cards.add(Card.of(Color.BLUE));
        cards.add(Card.of(Color.BLUE));
        cards.add(Card.of(Color.YELLOW));
        SortedBag<Card> initialCards = SortedBag.of(cards);

        String test = playerInfo.attemptsTunnelClaim(ChMap.routes().get(16), initialCards);
        assertEquals(test , "Player1 tente de s'emparer du tunnel Berne" + StringsFr.EN_DASH_SEPARATOR + "Lucerne au moyen de 2 bleues et 1 jaune !\n");
    }

    @Test
    public void drewAdditionalCardsNOADDITIONALTest() {
        Info playerInfo = new Info("Player1");
        List<Card> cards = new ArrayList<>();
        cards.add(Card.of(Color.BLUE));
        cards.add(Card.of(Color.BLUE));
        cards.add(Card.of(Color.RED));
        cards.add(Card.of(Color.GREEN));
        SortedBag<Card> drawnCards = SortedBag.of(cards);
        String test = playerInfo.drewAdditionalCards(drawnCards, 0);
        assertEquals(test , "Les cartes supplémentaires sont 2 bleues, 1 verte et 1 rouge. " + "Elles n'impliquent aucun coût additionnel.\n");
    }
    @Test
    public void drewAdditionalCardsADDITIONALCOSTSTest() {
        Info playerInfo = new Info("Player1");
        List<Card> cards = new ArrayList<>();
        cards.add(Card.of(Color.BLUE));
        cards.add(Card.of(Color.BLUE));
        cards.add(Card.of(Color.RED));
        cards.add(Card.of(Color.GREEN));
        SortedBag<Card> drawnCards = SortedBag.of(cards);
        String test = playerInfo.drewAdditionalCards(drawnCards, 3);
        assertEquals(test , "Les cartes supplémentaires sont 2 bleues, 1 verte et 1 rouge. " + "Elles impliquent un coût additionnel de 3 cartes.\n");
    }

    @Test
    public void didNotClaimRouteTest() {
        Info playerInfo = new Info("Player1");

        String test = playerInfo.didNotClaimRoute(ChMap.routes().get(16));
        assertEquals(test , "Player1 n'a pas pu (ou voulu) s'emparer de la route Berne" + StringsFr.EN_DASH_SEPARATOR + "Lucerne.\n");
    }

    @Test
    public void lastTurnBeginsSingleTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.lastTurnBegins(1);
        assertEquals(test , "\nPlayer1 n'a plus que 1 wagon, le dernier tour commence !\n");
    }
    @Test
    public void lastTurnBeginsTwoTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.lastTurnBegins(2);
        assertEquals(test, "\nPlayer1 n'a plus que 2 wagons, le dernier tour commence !\n");
    }
    @Test
    public void lastTurnBeginsExceptionTest() {
        Info playerInfo = new Info("Player1");

        assertThrows(IllegalArgumentException.class, () -> {
            playerInfo.lastTurnBegins(5);
        });
    }


    @Test
    public void getsLongestTrailBonusTest() {
        Info playerInfo = new Info("Player1");

        List<Route> allRoutes = ChMap.routes();
        Route a = allRoutes.get(66);
        Route b = allRoutes.get(65);
        Route d = allRoutes.get(19);
        Route e = allRoutes.get(16);

        List<Route> testedRoutes = new ArrayList<>();
        testedRoutes.add(a);
        testedRoutes.add(b);
        testedRoutes.add(d);
        testedRoutes.add(e);

        Trail longestTrail = Trail.longest(testedRoutes);

        String test = playerInfo.getsLongestTrailBonus(longestTrail);
        assertEquals(test , "\nPlayer1 reçoit un bonus de 10 points pour le plus long trajet (Yverdon" + StringsFr.EN_DASH_SEPARATOR + "Lucerne).\n");
    }

    @Test
    public void wonTest() {
        Info playerInfo = new Info("Player1");
        String test = playerInfo.won(57, 42);
        assertEquals(test , "\nPlayer1 remporte la victoire avec 57 points, contre 42 points !\n");
    }





//    @Test
//    public void Test() {
//        Info playerInfo = new Info("Player1");
//        assertTrue();
//    }



}
