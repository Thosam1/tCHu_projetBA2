package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers;
import ch.epfl.tchu.gui.GraphicalPlayer;
import ch.epfl.tchu.gui.ObservableGameState;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GraphicalPlayerTest extends Application {
    public static void main(String[] args) { launch(args); }

    private void setState(GraphicalPlayer player) {
        // … construit exactement les mêmes états que la méthode setState
        // du test de l'étape 9
        ObservableGameState gameState = new ObservableGameState(PlayerId.PLAYER_1);

        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 3));

        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PlayerId.PLAYER_1, p1State, PlayerId.PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PlayerId.PLAYER_1, pubPlayerStates, null);
        gameState.setState(publicGameState, p1State);

        player.setState(publicGameState, p1State);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<PlayerId, String> playerNames =
                Map.of(PlayerId.PLAYER_1, "Ada", PlayerId.PLAYER_2, "Charles");
        GraphicalPlayer p = new GraphicalPlayer(PlayerId.PLAYER_1, playerNames, null);    //toDo comment faire pour créer la vue ?

        setState(p);

        ActionHandlers.DrawTicketsHandler drawTicketsH =
                () -> p.receiveInfo("Je tire des billets !");
        ActionHandlers.DrawCardHandler drawCardH =
                s -> p.receiveInfo(String.format("Je tire une carte de %s !", s));
        ActionHandlers.ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.station1() + " - " + r.station2();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
                };

        p.startTurn(drawTicketsH, drawCardH, claimRouteH);

        ActionHandlers.ChooseTicketsHandler chooseTickets =
                s -> p.receiveInfo(String.format("Je choisis des tickets" ));
        List listTicket = new ArrayList();
        listTicket.add(ChMap.tickets().get(2));
        listTicket.add(ChMap.tickets().get(3));
        listTicket.add(ChMap.tickets().get(4));
        listTicket.add(ChMap.tickets().get(5));
        listTicket.add(ChMap.tickets().get(6));
        SortedBag<Ticket> tickets = SortedBag.of(listTicket);
        p.chooseTickets(tickets, chooseTickets);

        ActionHandlers.ChooseCardsHandler chooseCards =
                s -> p.receiveInfo(String.format("Je choisis des cartes" ));
        List listCards1 = new ArrayList();
        listCards1.add(Card.RED);
        listCards1.add(Card.RED);
        listCards1.add(Card.RED);
        listCards1.add(Card.RED);
        listCards1.add(Card.RED);
        SortedBag<Card> cards1 = SortedBag.of(listCards1);
        List listCards2 = new ArrayList();
        listCards2.add(Card.RED);
        listCards2.add(Card.RED);
        listCards2.add(Card.RED);
        listCards2.add(Card.RED);
        listCards2.add(Card.BLUE);
        SortedBag<Card> cards2 = SortedBag.of(listCards2);
        List listCards3 = new ArrayList();
        listCards3.add(Card.RED);
        listCards3.add(Card.RED);
        listCards3.add(Card.RED);
        listCards3.add(Card.GREEN);
        listCards3.add(Card.BLUE);
        SortedBag<Card> cards3 = SortedBag.of(listCards3);
        List listCards4 = new ArrayList();
        listCards4.add(Card.RED);
        listCards4.add(Card.RED);
        listCards4.add(Card.BLACK);
        listCards4.add(Card.GREEN);
        listCards4.add(Card.BLUE);
        SortedBag<Card> cards4 = SortedBag.of(listCards4);
        List<SortedBag<Card>> listcards = new ArrayList<>();
        listcards.add(cards1);
        listcards.add(cards2);
        listcards.add(cards3);
        listcards.add(cards4);
        p.chooseClaimCards(listcards, chooseCards);
        //ToDo quand on appuie sur choisir, le texte receive info ne s'affiche pas !!!

    }

    private void setState(ObservableGameState gameState) {
    }

    private static void claimRoute(Route route, SortedBag<Card> cards) {
        System.out.printf("Prise de possession d'une route : %s - %s %s%n",
                route.station1(), route.station2(), cards);
    }

    private static void chooseCards(List<SortedBag<Card>> options,
                                    ActionHandlers.ChooseCardsHandler chooser) {
        chooser.onChooseCards(options.get(0));
    }

    private static void drawTickets() {
        System.out.println("Tirage de billets !");
    }

    private static void drawCard(int slot) {
        System.out.printf("Tirage de cartes (emplacement %s)!\n", slot);
    }

    /*TODO
    vérifier le désactivage des deux boutons tickets et cartes lorsque le joueur s'empare de la route 3.2.1
     */
}
