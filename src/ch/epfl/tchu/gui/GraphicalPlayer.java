package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
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
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private ObservableList<Text> messageList = FXCollections.observableList(new ArrayList<>());  // toDo check avec la ligne 74
    private final PlayerId playerId;
    private final Map<PlayerId, String> mapPlayerNames;
    private Stage main;
    
    //propriétés contenant les gestionnaires d'action
    //si elle contient null, alors l'action en question est actuellement interdite
    private ObjectProperty<DrawCardHandler> drawCardProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ClaimRouteHandler> claimRouteProperty = new SimpleObjectProperty<>();
    private ObjectProperty<DrawTicketsHandler> drawTicketsProperty = new SimpleObjectProperty<>();
    
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> mapPlayerNames) {
        this.playerId = playerId;
        this.mapPlayerNames = mapPlayerNames;
        observableGame = new ObservableGameState(playerId);


        MapViewCreator.CardChooser chooseCard = (list, handler) -> {chooseClaimCards(list, handler);};

        Node mapView = MapViewCreator
                .createMapView(observableGame, claimRouteProperty, chooseCard);
        Node cardsView = DecksViewCreator
                .createCardsView(observableGame, drawTicketsProperty, drawCardProperty);
        Node handView = DecksViewCreator
                .createHandView(observableGame);
        Node infoView = InfoViewCreator.createInfoView(playerId, mapPlayerNames, observableGame, messageList);   //toDo check le new SimpleListProperty

        main = mainSceneGraph(mapView, cardsView, handView, infoView); // todo comment créer ceci sans prendre les nodes en arguments ?
        main.show();
    }
    
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert isFxApplicationThread();
        observableGame.setState(newGameState, newPlayerState);
    }

    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        messageList.add(new Text(message));
        if(messageList.size()==6) {
            messageList.remove(0);
        }
    }


    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler, DrawCardHandler drawCardHandler,
                          ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();
        if(observableGame.getPublicGameState().canDrawTickets()) {
            drawTicketsProperty.set(() -> {
                drawTicketsHandler.onDrawTickets();
                drawCardProperty.set(null);
                claimRouteProperty.set(null);
                drawTicketsProperty.set(null);      //je ne suis pas sur de ça
            });
        }
        
        if(observableGame.getPublicGameState().canDrawCards()) {
            drawCardProperty.set((a) ->{
                drawCardHandler.onDrawCard(a);
                drawTicketsProperty.set(null);
                claimRouteProperty.set(null);
               // drawCardProperty.set(null);//je ne suis pas sur de ça
                this.drawCard(drawCardHandler);
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
        Preconditions.checkArgument(ticketsToChoose.size()==3 || ticketsToChoose.size()==5);
        //TODO
        ObservableList<SortedBag<Ticket>> observableListTickets = FXCollections.observableArrayList(ticketsToChoose);
        int min = ticketsToChoose.size()-2;
        ListView listView = listView(observableListTickets, false, min != 1);  //toDo utiliser des constantes ici ?      //que ce passe-t-il s'il ne reste que 2 tickets -> 2-2 = 0
        Button chooseTicketsButton = chooseTicketsButton(listView, min);
        Stage chooseWindow = chooseGraph(main, StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS, min), listView, chooseTicketsButton);
        chooseWindow.show();
    }
    
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardProperty.set((a) -> {
            drawCardHandler.onDrawCard(a);
            drawCardProperty.set(null);  
        });
    }
    
    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        ObservableList<SortedBag<Card>> observableListCards = FXCollections.observableArrayList(possibleClaimCards);    //toDo checker que ça fonctionne
        ListView listView = listView(observableListCards, true, false);
        Button chooseClaimCardsButton = chooseCardsButton(listView);
        Stage chooseWindow = chooseGraph(main, StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, listView, chooseClaimCardsButton);
        chooseWindow.show();
    }

    //ToDo où faut-il appeler cette méthode ?
    public void choosedAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        ObservableList<SortedBag<Card>> observableListCards = FXCollections.observableArrayList(possibleAdditionalCards);    //toDo checker que ça fonctionne
        ListView listView = listView(observableListCards, true, false);
        Button choosedAdditionalCardsButton = chooseAdditionalCardsButton(listView);
        Stage chooseWindow = chooseGraph(main, StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, listView, choosedAdditionalCardsButton);
        chooseWindow.show();
    }



    private Stage mainSceneGraph(Node mapView, Node drawView, Node handView, Node infoView){
        BorderPane borderPane = new BorderPane(mapView, null, drawView, handView, infoView);
        Stage root = new Stage();
        root.setScene(new Scene(borderPane));
        root.setTitle("tCHu" + " - " + mapPlayerNames.get(playerId));
//        root.show();    //todo show-void or return ?
        return root;
    }


    public Stage chooseGraph(Stage root, String title, String introText, /*ObservableList<E> list*/ ListView listView, Button caseButton){  // titre est donné soit par la constante TICKETS_CHOICE, soit par la constante CARDS_CHOICE de StringsFr
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(root);  //todo ? soit la fenêtre principale de l'interface (créée par le constructeur de GraphicalPlayer)
        stage.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();

        Text text = new Text(/*number == null ? introText : String.format(StringsFr.CHOOSE_TICKETS, number)*/ introText); //toDo mieux de formater en dehors mieux
        TextFlow textFlow = new TextFlow(text);


        caseButton.getStyleClass().add("gauged");
        caseButton.setText("Choisir");
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
        stage.setOnCloseRequest(e -> {e.consume();});   //todo vérifier que c'est juste ça


        return stage;
    }
    /**
     *  Pour la liste view
     */
    private <E> ListView listView(ObservableList<E> list, boolean cards,boolean multiple){
        ListView listView = new ListView(list);
        if(cards){ listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));} // iff cards à cause de sortedBag
        if(multiple) {listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);}    //iff multiple
        return listView;
    }

    /**
     *  for chooseTickets
     */
    private Button chooseTicketsButton(ListView<SortedBag<Ticket>> list, int min){
        Button button = new Button();
        ObservableList temp = list.getSelectionModel().getSelectedItems();  //toDo est ce que ça fonctionne si il n'y a qu 1 element ?
        button.disableProperty().bind(Bindings.greaterThanOrEqual(Bindings.size(temp), min));
        button.setOnAction(e -> {
            ChooseTicketsHandler handler = null;    // faut-il initialiser à null ?
            handler.onChooseTickets((SortedBag<Ticket>) temp.get(0));    //toDo c'est bien l'index 0 ?
        });
        return button;
    }
    /**
     *  for chooseCards
     */
    private Button chooseCardsButton(ListView<SortedBag<Card>> list){    //todo ça représente quoi ? les cartes initiales ? on peut choisir plus d'une option ?
        Button button = new Button();
        ObservableList temp = list.getSelectionModel().getSelectedItems();
        BooleanBinding equals = Bindings.createBooleanBinding(() -> !Objects.equals(Bindings.size(temp),1));    // disable when selects 0 or more than 1
        button.disableProperty().bind(equals);

        button.setOnAction(e -> {
            ChooseCardsHandler handler = null;
            handler.onChooseCards((SortedBag<Card>) temp.get(0));
        });
        return button;
    }
    /**
     *  for chooseAdditionalCards
     *  le bouton est toujours actif, une sélection vide permettant au joueur
     *  de déclarer qu'il désire abandonner sa tentative de prise de possession du tunnel.
     */
    private Button chooseAdditionalCardsButton(ListView<SortedBag<Card>> list){
        Button button = new Button();
        ObservableList temp = list.getSelectionModel().getSelectedItems();

        BooleanBinding equals = Bindings.createBooleanBinding(() -> !Objects.equals(Bindings.size(temp),1));    // disable when selects 0 or more than 1    //not very important since we won't be able to select more than one
        button.disableProperty().bind(equals);

        button.setOnAction(e -> {
            ChooseCardsHandler handler = null;
            handler.onChooseCards((SortedBag<Card>) temp.get(0));
        });
        return button;
    }
    //todo retravailler les deux méthodes plus haut

    private class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
        @Override
        public String toString(SortedBag<Card> cardSortedBag) {
            System.out.println("En format ecrit");
            return Info.cardListString(cardSortedBag);  //toDo serait-il mieux de la recopier plus bas ?
        }

        @Override
        public SortedBag<Card> fromString(String s) {
            throw new UnsupportedOperationException();
        } // StringConverter<SortedBag<Card>> quelle librairie ?

    }

}
