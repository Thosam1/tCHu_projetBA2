package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.ActionHandlers;
import ch.epfl.tchu.gui.GraphicalPlayer;
import ch.epfl.tchu.gui.ObservableGameState;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public final class GraphicalPlayerTest extends Application {
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
        GraphicalPlayer p = new GraphicalPlayer(PlayerId.PLAYER_1, playerNames);    //toDo comment faire pour créer la vue ?
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
