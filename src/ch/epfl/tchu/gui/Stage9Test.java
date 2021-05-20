package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class Stage9Test extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {


        /**
         *  tests etapes 9
         */
      ObservableGameState gameState = new ObservableGameState(PlayerId.PLAYER_1);

      ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRoute =
        new SimpleObjectProperty<>(Stage9Test::claimRoute);
      ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets =
        new SimpleObjectProperty<>(Stage9Test::drawTickets);
      ObjectProperty<ActionHandlers.DrawCardHandler> drawCard =
        new SimpleObjectProperty<>(Stage9Test::drawCard);

      Node mapView = MapViewCreator
        .createMapView(gameState, claimRoute, Stage9Test::chooseCards);
      Node cardsView = DecksViewCreator
        .createCardsView(gameState, drawTickets, drawCard);
      Node handView = DecksViewCreator
        .createHandView(gameState);

      // ---------------- début des tests etapes 10 -----------------
      Map<PlayerId, String> playerNames =
              Map.of(PlayerId.PLAYER_1, "Thösam", PlayerId.PLAYER_2, "Aymeric");
            ObservableList<Text> infos = FXCollections.observableArrayList(
              new Text("Première information.\n"),
              new Text("\nSeconde information.\n"));
      Node infoView = InfoViewCreator
              .createInfoView(PlayerId.PLAYER_1, playerNames, gameState, infos);
            
      BorderPane mainPane =
        new BorderPane(mapView, null, cardsView, handView, infoView);

      // ----------------- fin des tests etape 10   ----------------------
      primaryStage.setScene(new Scene(mainPane));


      primaryStage.show();
//      setState(gameState);
    }

    private void setState(ObservableGameState gameState) {
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
  }
