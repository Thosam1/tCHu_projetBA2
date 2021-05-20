package ch.epfl.tchu.gui;

import static javafx.application.Platform.isFxApplicationThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

/**
 * La classe instanciable GraphicalPlayer du paquetage ch.epfl.tchu.gui
 * représente l'interface graphique d'un joueur de tCHu
 * 
 * @author Thösam Norlha-Tsang (330163)
 * @author Aymeric de chillaz (326617)
 */

public final class GraphicalPlayer {
    private ObservableGameState observableGame;
    private ObservableList<Text> messageList = FXCollections
            .observableList(new ArrayList<>());
    private final PlayerId playerId;
    private final Map<PlayerId, String> mapPlayerNames;
    private final Stage main;

    // propriétés contenant les gestionnaires d'action
    // si elle contient null, alors l'action en question est actuellement
    // interdite
    private final ObjectProperty<DrawCardHandler> drawCardProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ClaimRouteHandler> claimRouteProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawTicketsHandler> drawTicketsProperty = new SimpleObjectProperty<>();

    public GraphicalPlayer(PlayerId playerId,
            Map<PlayerId, String> mapPlayerNames) {
        assert isFxApplicationThread();
        this.playerId = playerId;
        this.mapPlayerNames = mapPlayerNames;
        observableGame = new ObservableGameState(playerId);

        // créé le CardChooser que l'on passe à createMapView
        MapViewCreator.CardChooser chooseCard = (list, handler) -> {
            chooseClaimCards(list, handler);
        };

        /**
         * créé les 4 différentes vues et les passe en paramètre à la méthode
         * mainSceneGraph qui retourne un Stage que l'on stocke dans l'attribut
         * main
         */
        Node mapView = MapViewCreator.createMapView(observableGame,
                claimRouteProperty, chooseCard);
        Node cardsView = DecksViewCreator.createCardsView(observableGame,
                drawTicketsProperty, drawCardProperty);
        Node handView = DecksViewCreator.createHandView(observableGame);
        Node infoView = InfoViewCreator.createInfoView(playerId, mapPlayerNames,
                observableGame, messageList);

        main = mainSceneGraph(mapView, cardsView, handView, infoView);
        main.show();
    }

    /**
     * méthode prenant les mêmes arguments que la méthode setState de
     * ObservableGameState et ne faisant rien d'autre que d'appeler cette
     * méthode sur l'état observable du joueur
     */
    public void setState(PublicGameState newGameState,
            PlayerState newPlayerState) {
        assert isFxApplicationThread();
        observableGame.setState(newGameState, newPlayerState);
    }

    /**
     * méthode prenant un message — de type String — et l'ajoutant au bas des
     * informations sur le déroulement de la partie, qui sont présentées dans la
     * partie inférieure de la vue des informations ; cette vue ne doit contenir
     * que les cinq derniers messages reçus (ce qui correspond à la constante
     * MAXIMUM_NUMBER_VISIBLE_INFO de Constants)
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        messageList.add(new Text(message));
        if (messageList.size() >= Constants.MAXIMUM_NUMBER_VISIBLE_INFO) {
            messageList.remove(0);
        }
    }

    /**
     * méthode qui prend en arguments trois gestionnaires d'action, un par types
     * d'actions que le joueur peut effectuer lors d'un tour, et qui permet au
     * joueur d'en effectuer une
     * 
     * si une propriété contient null alors l'action est interdite il est donc
     * important de changer le contenu des propriétés après avoir utilisé un des
     * Handler, ceci est fait par la méthode makeHandlerPropertiesNull
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler,
            DrawCardHandler drawCardHandler,
            ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        if (observableGame.getPublicGameState().canDrawTickets()) {
            drawTicketsProperty.set(() -> {
                drawTicketsHandler.onDrawTickets();
                makeHandlerPropertiesNull();
            });
        }

        if (observableGame.getPublicGameState().canDrawCards()) {
            drawCardProperty.set((a) -> {
                drawCardHandler.onDrawCard(a);
                makeHandlerPropertiesNull();
            });
        }

        // La propriété correspondant à la prise de possession d'une route doit
        // toujours être remplie
        // lorsque le tour commence (ce qui est le cas lors de l'appel à
        // startTurn)
        claimRouteProperty.set((route, cards) -> {
            claimRouteHandler.onClaimRoute(route, cards);
            makeHandlerPropertiesNull();
        });
    }

    /**
     * méthode qui prend en arguments un multiensemble contenant cinq ou trois
     * billets que le joueur peut choisir et un gestionnaire de choix de
     * billets — de type ChooseTicketsHandler —, et qui ouvre une fenêtre
     * permettant au joueur de faire son choix ; une fois celui-ci confirmé, le
     * gestionnaire de choix est appelé avec ce choix en argument
     */
    public void chooseTickets(SortedBag<Ticket> ticketsToChoose,
            ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(ticketsToChoose
                .size() == Constants.IN_GAME_TICKETS_COUNT
                || ticketsToChoose.size() == Constants.INITIAL_TICKETS_COUNT);
        int min = ticketsToChoose.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        // TODO mettre un type à ListView
        ListView listView = listViewTicket(ticketsToChoose, true);

        // TODO que ce passe-t-il s'il ne reste que 2 tickets -> 2-2 = 0
        Button chooseTicketsButton = chooseTicketsButton(listView, min,
                chooseTicketsHandler);
        Stage chooseWindow = chooseGraph(main, StringsFr.TICKETS_CHOICE, String
                .format(StringsFr.CHOOSE_TICKETS, min, StringsFr.plural(min)),
                listView, chooseTicketsButton);
        chooseWindow.show();
    }

    /**
     * méthode qui prend en argument un gestionnaire de tirage de carte — de
     * type DrawCardHandler — et qui autorise le joueur a choisir une carte
     * wagon/locomotive, soit l'une des cinq dont la face est visible, soit
     * celle du sommet de la pioche ; une fois que le joueur a cliqué sur l'une
     * de ces cartes, le gestionnaire est appelé avec le choix du joueur ; cette
     * méthode est destinée à être appelée lorsque le joueur a déjà tiré une
     * première carte et doit maintenant tirer la seconde
     * 
     * si une propriété contient null alors l'action est interdite
     * 
     * @param drawCardHandler
     *            un gestionnaire de tirage de carte
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardProperty.set((a) -> {
            drawCardHandler.onDrawCard(a);
            makeHandlerPropertiesNull();
        });
    }

    /**
     * méthode qui prend en arguments une liste de multiensembles de cartes, qui
     * sont les cartes initiales qu'il peut utiliser pour s'emparer d'une route,
     * et un gestionnaire de choix de cartes — de type ChooseCardsHandler —, et
     * qui ouvre une fenêtre permettant au joueur de faire son choix ; une fois
     * que celui-ci a été fait et confirmé, le gestionnaire de choix est appelé
     * avec le choix du joueur en argument ; cette méthode n'est destinée qu'à
     * être passée en argument à createMapView en tant que valeur de type
     * CardChooser
     */
    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards,
            ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        ObservableList<SortedBag<Card>> observableListCards = FXCollections
                .observableArrayList(possibleClaimCards);
        ListView listView = listViewCard(observableListCards, false);
        Button chooseClaimCardsButton = chooseCardsButton(listView,
                chooseCardsHandler, false);
        Stage chooseWindow = chooseGraph(main, StringsFr.CARDS_CHOICE,
                StringsFr.CHOOSE_CARDS, listView, chooseClaimCardsButton);
        chooseWindow.show();
    }

    /**
     * méthode qui prend en arguments une liste de multiensembles de cartes, qui
     * sont les cartes additionnelles qu'il peut utiliser pour s'emparer d'un
     * tunnel et un gestionnaire de choix de cartes — de type ChooseCardsHandler
     * —, et qui ouvre une fenêtre permettant au joueur de faire son choix ; une
     * fois que celui-ci a été fait et confirmé, le gestionnaire de choix est
     * appelé avec le choix du joueur en argument
     */
    public void choosedAdditionalCards(
            List<SortedBag<Card>> possibleAdditionalCards,
            ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        ObservableList<SortedBag<Card>> observableListCards = FXCollections
                .observableArrayList(possibleAdditionalCards);
        ListView listView = listViewCard(observableListCards, false);
        Button choosedAdditionalCardsButton = chooseCardsButton(listView,
                chooseCardsHandler, true);
        Stage chooseWindow = chooseGraph(main, StringsFr.CARDS_CHOICE,
                StringsFr.CHOOSE_ADDITIONAL_CARDS, listView,
                choosedAdditionalCardsButton);
        chooseWindow.show();
    }

    /**
     * méthode appelée dans le constructeur, pour mettre ensemble les
     * différentes parties de la fenêtre du jeu
     * 
     * @param mapView
     * @param drawView
     * @param handView
     * @param infoView
     * @return une stage de la fenêtre principale
     */
    private Stage mainSceneGraph(Node mapView, Node drawView, Node handView,
            Node infoView) {
        assert isFxApplicationThread();
        BorderPane borderPane = new BorderPane(mapView, null, drawView,
                handView, infoView);
        Stage root = new Stage();
        root.setScene(new Scene(borderPane));
        root.setTitle("tCHu" + " - " + mapPlayerNames.get(playerId));
        return root;
    }

    /**
     * méthode pour créer la fenêtre pour choisir les billets ou les cartes
     * titre est donné soit par la constante TICKETS_CHOICE, soit par la
     * constante CARDS_CHOICE de StringsFr
     * 
     * @param root
     * @param title
     * @param introText
     * @param listView
     * @param caseButton
     * @return
     */
    private Stage chooseGraph(Stage root, String title, String introText,
            ListView listView, Button caseButton) {
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(root);
        stage.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();

        Text text = new Text(introText);

        TextFlow textFlow = new TextFlow(text);

        caseButton.getStyleClass().add("gauged");

        caseButton.setText(StringsFr.CHOOSE);

        caseButton.addEventHandler(ActionEvent.ACTION, e -> {
            stage.hide();
        });

        vBox.getChildren().addAll(textFlow, listView, caseButton);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setTitle(title); // depends
        stage.setScene(scene);
        stage.setOnCloseRequest(Event::consume);

        return stage;
    }

    /**
     * retourne la listView des Card
     */
    private <E> ListView listViewCard(ObservableList<E> list,
            boolean multiple) {
        assert isFxApplicationThread();
        ListView listView = new ListView(list);
        listView.setCellFactory(
                v -> new TextFieldListCell<>(new CardBagStringConverter()));
        // iff cards à cause de sortedBag
        if (multiple) {
            listView.getSelectionModel()
                    .setSelectionMode(SelectionMode.MULTIPLE);
        }
        return listView;
    } // iff multiple
      // return listView;

    private <E> ListView listViewTicket(SortedBag<Ticket> ticketsToChoose,
            boolean multiple) {
        assert isFxApplicationThread();
        ListView<Ticket> ticketList = new ListView<>(
                FXCollections.observableList(ticketsToChoose.toList()));
        if (multiple) {
            ticketList.getSelectionModel()
                    .setSelectionMode(SelectionMode.MULTIPLE);
        }
        return ticketList;
    }

    /**
     * pour choisir les cartes, empty vaut false
     *
     * empty est vrai pour dans le cas du tirage de cartes additionnels : le
     * bouton est toujours actif, une sélection vide permettant au joueur de
     * déclarer qu'il désire abandonner sa tentative de prise de possession du
     * tunnel.
     */
    private Button chooseCardsButton(ListView<SortedBag<Card>> list,
            ChooseCardsHandler handler, boolean emptyValid) {
        assert isFxApplicationThread();
        Button button = new Button();
        ObservableList<SortedBag<Card>> temp = list.getSelectionModel()
                .getSelectedItems();
        if (emptyValid) {
            button.disableProperty().bind(Bindings.greaterThan(
                    Bindings.size(list.getSelectionModel().getSelectedItems()),
                    1));
        } else {
            button.disableProperty().bind(Bindings
                    .isEmpty(list.getSelectionModel().getSelectedItems()));
        }
        return button;
    }

    /**
     * pour choisir les billets
     */
    private Button chooseTicketsButton(ListView<Ticket> list, int min,
            ChooseTicketsHandler handler) {
        assert isFxApplicationThread();
        Button button = new Button();
        button.disableProperty()
                .bind(Bindings.lessThan(
                        Bindings.size(
                                list.getSelectionModel().getSelectedItems()),
                        min));
        button.setOnAction(e -> {
            handler.onChooseTickets(
                    SortedBag.of(list.getSelectionModel().getSelectedItems()));
        });
        return button;
    }

    /**
     * méthode privée qui rend null le contenu des propriétés contenant les
     * Handlers. Ce qui signifie que les actions des Handlers ne peuvent pas
     * être effectuées
     */
    private void makeHandlerPropertiesNull() {
        drawCardProperty.set(null);
        claimRouteProperty.set(null);
        drawTicketsProperty.set(null);
    }

    /**
     * classe imbriquée qui hérite de StringConverter et redéfinit ses méthodes
     * toString et fromString
     */
    private class CardBagStringConverter
            extends StringConverter<SortedBag<Card>> {
        @Override
        public String toString(SortedBag<Card> cardSortedBag) {
            return Info.cardListString(cardSortedBag);
        }

        @Override
        public SortedBag<Card> fromString(String s) {
            throw new UnsupportedOperationException();
        }
    }
}
