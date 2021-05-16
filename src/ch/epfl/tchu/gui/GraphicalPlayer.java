package ch.epfl.tchu.gui;

import java.util.*;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableNumberValue;
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

import static javafx.application.Platform.isFxApplicationThread;


public final class GraphicalPlayer {
    private ObservableGameState observableGame;
    private ObservableList<Text> messageList = FXCollections.observableList(new ArrayList<>());
    private final PlayerId playerId;
    private final Map<PlayerId, String> mapPlayerNames;
    private Stage main;

    //propriétés contenant les gestionnaires d'action
    //si elle contient null, alors l'action en question est actuellement interdite
    private ObjectProperty<DrawCardHandler> drawCardProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ClaimRouteHandler> claimRouteProperty = new SimpleObjectProperty<>();
    private ObjectProperty<DrawTicketsHandler> drawTicketsProperty = new SimpleObjectProperty<>();

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> mapPlayerNames) {
        assert isFxApplicationThread();
        this.playerId = playerId;
        this.mapPlayerNames = mapPlayerNames;
        observableGame = new ObservableGameState(playerId);


        MapViewCreator.CardChooser chooseCard = (list, handler) -> {
            chooseClaimCards(list, handler);
        };

        Node mapView = MapViewCreator
                .createMapView(observableGame, claimRouteProperty, chooseCard);
        Node cardsView = DecksViewCreator
                .createCardsView(observableGame, drawTicketsProperty, drawCardProperty);
        Node handView = DecksViewCreator
                .createHandView(observableGame);
        Node infoView = InfoViewCreator.createInfoView(playerId, mapPlayerNames, observableGame, messageList);

        main = mainSceneGraph(mapView, cardsView, handView, infoView);
        main.show();
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();
        observableGame.setState(newGameState, newPlayerState);
    }

    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        messageList.add(new Text(/*"\n" + */message));
        if (messageList.size() == 6) {
            messageList.remove(0);
        }
    }


    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler,
                          ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        if (observableGame.getPublicGameState().canDrawTickets()) {
            drawTicketsProperty.set(() -> {
                drawTicketsHandler.onDrawTickets();
                drawCardProperty.set(null);
                claimRouteProperty.set(null);
                drawTicketsProperty.set(null);      //je ne suis pas sur de ça
            });
        }

        if (observableGame.getPublicGameState().canDrawCards()) {
            drawCardProperty.set((a) -> {
                drawCardHandler.onDrawCard(a);
                drawTicketsProperty.set(null);
                claimRouteProperty.set(null);   // todo parfois ça beug et on arrive à choper une route au lieu de piocher la seconde carte
                drawCardProperty.set(null);//je ne suis pas sur de ça
                //this.drawCard(drawCardHandler);
            });
        }

        //La propriété correspondant à la prise de possession d'une route doit toujours être remplie 
        //lorsque le tour commence (ce qui est le cas lors de l'appel à startTurn)
        claimRouteProperty.set((route, cards) -> {
            claimRouteHandler.onClaimRoute(route, cards);
            drawTicketsProperty.set(null);
            claimRouteProperty.set(null);
            drawCardProperty.set(null);
        });
    }

    public void chooseTickets(SortedBag<Ticket> ticketsToChoose, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(ticketsToChoose.size() == 3 || ticketsToChoose.size() == 5);
        //TODO
        int min = ticketsToChoose.size() - Constants.DISCARDABLE_TICKETS_COUNT;
        ListView listView = listViewTicket(ticketsToChoose, true);  //toDo utiliser des constantes ici ?      //que ce passe-t-il s'il ne reste que 2 tickets -> 2-2 = 0
        Button chooseTicketsButton = chooseTicketsButton(listView, min, chooseTicketsHandler);
        Stage chooseWindow = chooseGraph(main, StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS, min, StringsFr.plural(min)), listView, chooseTicketsButton);
        chooseWindow.show();
    }

    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardProperty.set((a) -> {
            drawCardHandler.onDrawCard(a);
            drawCardProperty.set(null);
            claimRouteProperty.set(null);
            drawTicketsProperty.set(null);
        });
    }

    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        ObservableList<SortedBag<Card>> observableListCards = FXCollections.observableArrayList(possibleClaimCards);    //toDo checker que ça fonctionne
        ListView listView = listViewCard(observableListCards, false);
        Button chooseClaimCardsButton = chooseCardsButton(listView, chooseCardsHandler, false);
        Stage chooseWindow = chooseGraph(main, StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, listView, chooseClaimCardsButton);
        chooseWindow.show();
    }

    //ToDo où faut-il appeler cette méthode ?
    public void choosedAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        ObservableList<SortedBag<Card>> observableListCards = FXCollections.observableArrayList(possibleAdditionalCards);    //toDo checker que ça fonctionne
        ListView listView = listViewCard(observableListCards, false);
        Button choosedAdditionalCardsButton = chooseCardsButton(listView, chooseCardsHandler, true);
        Stage chooseWindow = chooseGraph(main, StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, listView, choosedAdditionalCardsButton);
        chooseWindow.show();
    }


    private Stage mainSceneGraph(Node mapView, Node drawView, Node handView, Node infoView) {
        assert isFxApplicationThread();
        BorderPane borderPane = new BorderPane(mapView, null, drawView, handView, infoView);
        Stage root = new Stage();
        root.setScene(new Scene(borderPane));
        root.setTitle("tCHu" + " - " + mapPlayerNames.get(playerId));
        return root;
    }


    private Stage chooseGraph(Stage root, String title, String introText, /*ObservableList<E> list*/ ListView listView, Button caseButton) {  // titre est donné soit par la constante TICKETS_CHOICE, soit par la constante CARDS_CHOICE de StringsFr
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(root);
        stage.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();

        Text text = new Text(introText); //toDo mieux de formater en dehors mieux
        TextFlow textFlow = new TextFlow(text);


        caseButton.getStyleClass().add("gauged");
        caseButton.setText(StringsFr.CHOOSE);
//        caseButton.setOnAction(e -> { caseButton
//            stage.hide();   // toDo c'est bien sur le stage // et que ça fait bien les deux avec le setOnAction qui lui est appelé dans la méthode spécifique
//        });
        caseButton.addEventHandler(ActionEvent.ACTION, e -> {
            stage.hide();
        });


        vBox.getChildren().addAll(textFlow, listView, caseButton);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setTitle(title);      //depends
        stage.setScene(scene);
//        stage.setOnCloseRequest(e -> {e.consume();});   //todo vérifier que c'est juste ça
        stage.setOnCloseRequest(Event::consume);

        return stage;
    }

    /**
     * Pour la liste view
     */
    private <E> ListView listViewCard(ObservableList<E> list, boolean multiple) {
        assert isFxApplicationThread();
        ListView listView = new ListView(list);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));
        // iff cards à cause de sortedBag
        if (multiple) {
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        return listView;
    }    //iff multiple
    // return listView;

    private <E> ListView listViewTicket(SortedBag<Ticket> ticketsToChoose, boolean multiple) {
        assert isFxApplicationThread();
        ListView<Ticket> ticketList = new ListView<>(FXCollections.observableList(ticketsToChoose.toList()));
        if (multiple) {
            ticketList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        return ticketList;
    }

        /**
         *  pour choisir les billets
         */
        private Button chooseTicketsButton (ListView<Ticket> list,int min, ChooseTicketsHandler handler)
        {
            assert isFxApplicationThread();
            Button button = new Button();
            button.disableProperty().bind(Bindings.lessThan(Bindings.size(list.getSelectionModel().getSelectedItems()), min));
            button.setOnAction(e -> {
                //handler.onChooseTickets(SortedBag.of(list.getSelectionModel().getSelectedItems().get(0)));    //toDo c'est bien l'index 0 ? non parceque sinon tu gardes juste un ticket
                handler.onChooseTickets(SortedBag.of(list.getSelectionModel().getSelectedItems())); 
            });
            return button;
        }
        /**
         *  pour choisir les cartes, empty vaut false
         *
         *  empty est vrai pour dans le cas du tirage de cartes additionnels :
         *  le bouton est toujours actif, une sélection vide permettant au joueur
         *  de déclarer qu'il désire abandonner sa tentative de prise de possession du tunnel.
         */
        private Button chooseCardsButton (ListView<SortedBag<Card>> list, ChooseCardsHandler handler, boolean emptyValid)
        {
            assert isFxApplicationThread();
            Button button = new Button();
            ObservableList temp = list.getSelectionModel().getSelectedItems();
            if(emptyValid){
                button.disableProperty().bind(Bindings.greaterThan(Bindings.size(list.getSelectionModel().getSelectedItems()), 1));
            }else {
                button.disableProperty().bind(Bindings.isEmpty(list.getSelectionModel().getSelectedItems()));
            }
            button.setOnAction(e -> {
                handler.onChooseCards((temp.isEmpty()) ? SortedBag.of() : (SortedBag<Card>) temp.get(0));
            });
            return button;
        }

        private class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
            @Override
            public String toString(SortedBag<Card> cardSortedBag) {
                return Info.cardListString(cardSortedBag);
            }

            @Override
            public SortedBag<Card> fromString(String s) {
                throw new UnsupportedOperationException();
            } // StringConverter<SortedBag<Card>> quelle librairie ?

        }


}
