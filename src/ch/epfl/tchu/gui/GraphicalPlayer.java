package ch.epfl.tchu.gui;

import java.util.*;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.AddToChatHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
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

/**
 * La classe instanciable GraphicalPlayer du paquetage ch.epfl.tchu.gui
 * représente l'interface graphique d'un joueur de tCHu
 * 
 * @author Thösam Norlha-Tsang (330163)
 * @author Aymeric de chillaz (326617)
 */

public final class GraphicalPlayer {
    /**
     * Nombre maximum de messages qui peuvent etre affichés dans le
     * InfoViewCreator
     */
    private static final int MAXIMUM_NUMBER_VISIBLE_MESSAGES = 5;

    private static final int TEXT_INPUT_MAX_CAPACITY = 100;
    private static final String DEFAULT_TEXT_INPUT = "Enter Text Here";

    private final ObservableGameState observableGame;
    private final ObservableList<Text> messageList = FXCollections
            .observableList(new ArrayList<>());
    private final ObservableList<String> chatList = FXCollections // partie 12
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

    // cette propriété n'est jamais null pendant le jeu car nous voulons pouvoir
    // envoyer un chat à tout moment
    private final ObjectProperty<AddToChatHandler> addToChatProperty = new SimpleObjectProperty<>();

    /**
     * Constructeur de la classe GraphicalPlayer
     * 
     * @param playerId
     *            l'id du joueur
     * @param mapPlayerNames
     *            la map contenant les id des joueurs et leur map correspondant
     */
    public GraphicalPlayer(PlayerId playerId,
            Map<PlayerId, String> mapPlayerNames,
            AddToChatHandler addToChatHandler) {
        // la propriété du addToChatHandler ne change jamais, ainsi il faut
        // trouver un moment ou GraphicalPlayerAdapter passe le Handler à
        // GraphicalPlayer

        assert isFxApplicationThread();
        this.playerId = playerId;
        this.mapPlayerNames = mapPlayerNames;
        observableGame = new ObservableGameState(playerId);

        addToChatProperty.set(addToChatHandler);

        // créé le CardChooser que l'on passe à createMapView
        MapViewCreator.CardChooser chooseCard = this::chooseClaimCards;

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
                observableGame, messageList, this);

        main = mainSceneGraph(mapView, cardsView, handView, infoView);
        main.show();
    }

    /**
     * méthode prenant les mêmes arguments que la méthode setState de
     * ObservableGameState et ne faisant rien d'autre que d'appeler cette
     * méthode sur l'état observable du joueur
     * 
     * @param newGameState
     *            nouveau gameState
     * @param newPlayerState
     *            nouveau playerState
     */
    public void setState(PublicGameState newGameState,
            PlayerState newPlayerState) {
        assert isFxApplicationThread();
        observableGame.setState(newGameState, newPlayerState);
    }

    /**
     * méthode prenant un message—de type String — et l'ajoutant au bas des
     * informations sur le déroulement de la partie, qui sont présentées dans la
     * partie inférieure de la vue des informations; cette vue ne doit contenir
     * que les cinq derniers messages reçus (ce qui correspond à la constante
     * MAXIMUM_NUMBER_VISIBLE_INFO de Constants)
     * 
     * @param message
     *            le message à afficher
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        messageList.add(new Text(message));
        if (messageList.size() > MAXIMUM_NUMBER_VISIBLE_MESSAGES) {
            messageList.remove(0);
        }
        observableGame.updateTicketListHandPoints();    // --- --- Extension tickets verts
    }

    /**
     * méthode qui rajoute le message d'un autre joueur au chat du joueur
     * courant
     * 
     * @param newChat
     *            le chat qui doit etre ajouté
     */
    public void addToChat(String newChat) {
        assert isFxApplicationThread();
        chatList.add(newChat);
    }

    /**
     * méthode qui prend en arguments trois gestionnaires d'action, un par types
     * d'actions que le joueur peut effectuer lors d'un tour, et qui permet au
     * joueur d'en effectuer une
     *
     * si une propriété contient null alors l'action est interdite il est donc
     * important de changer le contenu des propriétés après avoir utilisé un des
     * Handler, ceci est fait par la méthode makeHandlerPropertiesNull
     * 
     * @param drawTicketsHandler
     *            le handler du tirage de billets
     * @param drawCardHandler
     *            le handler du tirage de cartes
     * @param claimRouteHandler
     *            le handler qui se charge de la prise des routes
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
     * 
     * @param ticketsToChoose
     *            un multiensemble contenant cinq ou trois billets que le joueur
     *            peut choisir
     * @param chooseTicketsHandler
     *            gestionnaire de choix de billets
     */
    public void chooseTickets(SortedBag<Ticket> ticketsToChoose,
            ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(ticketsToChoose
                .size() == Constants.IN_GAME_TICKETS_COUNT
                || ticketsToChoose.size() == Constants.INITIAL_TICKETS_COUNT);

        int min = ticketsToChoose.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        ListView<Ticket> listView = listViewTicket(ticketsToChoose);
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
     * 
     * @param possibleClaimCards
     *            liste de multiensembles de cartes, qui sont les cartes
     *            initiales que le joueur peut utiliser pour s'emparer d'une
     *            route
     * @param chooseCardsHandler
     *            un gestionnaire de choix de cartes
     */
    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards,
            ChooseCardsHandler chooseCardsHandler) {
        chooseCards(possibleClaimCards, chooseCardsHandler, false,
                StringsFr.CHOOSE_CARDS);
    }

    /**
     * ouvre une fenêtre permettant au joueur de faire son choix; une fois que
     * celui-ci a été fait et confirmé, le gestionnaire de choix est appelé avec
     * le choix du joueur en argument
     * 
     * @param possibleAdditionalCards
     *            liste de multiensembles de cartes, qui sont les cartes
     *            additionnelles que le joueur peut utiliser pour s'emparer d'un
     *            tunnel
     * @param chooseCardsHandler
     *            gestionnaire de choix de cartes
     */
    public void choosedAdditionalCards(
            List<SortedBag<Card>> possibleAdditionalCards,
            ChooseCardsHandler chooseCardsHandler) {
        chooseCards(possibleAdditionalCards, chooseCardsHandler, true,
                StringsFr.CHOOSE_ADDITIONAL_CARDS);
    }

    private void chooseCards(List<SortedBag<Card>> cardsList,
            ChooseCardsHandler chooseCardsHandler, boolean emptyValid,
            String introText) {
        assert isFxApplicationThread();
        ObservableList<SortedBag<Card>> observableListCards = FXCollections
                .observableArrayList(cardsList);
        ListView<SortedBag<Card>> listView = listViewCard(observableListCards);
        Button button = chooseCardsButton(listView, chooseCardsHandler,
                emptyValid);
        Stage chooseWindow = chooseGraph(main, StringsFr.CARDS_CHOICE,
                introText, listView, button);
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
        root.setTitle(
                String.join("", "tCHu", " - ", mapPlayerNames.get(playerId)));
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
     * @return la fenêtre pour choisir les billets ou les cartes
     */
    private <T> Stage chooseGraph(Stage root, String title, String introText,
            ListView<T> listView, Button caseButton) {
        assert isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(root);
        stage.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();

        Text text = new Text(introText);
        TextFlow textFlow = new TextFlow(text);

        caseButton.getStyleClass().add("gauged");
        caseButton.setText(StringsFr.CHOOSE);

        caseButton.addEventHandler(ActionEvent.ACTION, e -> stage.hide());

        vBox.getChildren().addAll(textFlow, listView, caseButton);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setTitle(title); // depends
        stage.setScene(scene);
        stage.setOnCloseRequest(Event::consume);

        return stage;
    }

    /**
     * Pour les liste view
     */
    private ListView<SortedBag<Card>> listViewCard(
            ObservableList<SortedBag<Card>> list) {
        assert isFxApplicationThread();
        ListView<SortedBag<Card>> listView = new ListView<>(list);
        listView.setCellFactory(
                v -> new TextFieldListCell<>(new CardBagStringConverter()));
        return listView;
    }

    private ListView<Ticket> listViewTicket(SortedBag<Ticket> ticketsToChoose) {
        assert isFxApplicationThread();
        observableGame.setTicketListPopUp(ticketsToChoose); //on le set comme ça on calcule seulement 1 fois pour toute la liste le mettre avant la création de la liste                             // --- --- Extension
        ListView<Ticket> ticketList = new ListView<>(
                FXCollections.observableList(ticketsToChoose.toList()));
        if (observableGame.playerState() != null) { //if null, it means it is the start window where we choose tickets
            ticketList.setCellFactory(param -> new ticketListBG(observableGame));      //TODO why doesnt' work ?
        }

        ticketList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return ticketList;
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
        button.setOnAction(e -> handler.onChooseTickets(
                SortedBag.of(list.getSelectionModel().getSelectedItems())));
        return button;
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
        button.setOnAction(e -> {
            handler.onChooseCards(
                    (temp.isEmpty()) ? SortedBag.of() : temp.get(0));
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
     * méthode appelée dans createInfoView lorsque le bouton Chat est activé Son
     * role est de faire apparaitre le chat
     * 
     * Les messages sont contenu dans une ListView (chatListView) Les nouveaux
     * messages sont entrés dans textInput Lorsque le joueur active le bouton
     * sendMessage, le contenu de textInput est extrait et passé au handler dans
     * le cas ou il n'est pas blank
     * 
     * Nous avons décidé que toutes les actions sont seulement activées par
     * cliques de souris
     */
    public void openChat() {
        ListView<String> chatListView = new ListView<>(chatList);

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(main);
        stage.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();

        TextField textInput = new TextField(DEFAULT_TEXT_INPUT);
        // si le joueur click sur textInput, son contenu est vidé pour que le
        // joueur puisse entrer son message
        textInput.setOnMouseClicked(e -> textInput.clear());

        Button sendMessage = new Button();

        sendMessage.getStyleClass().add("gauged");
        sendMessage.setText("SEND");

        sendMessage.setOnMouseClicked(e -> {
            String input = textInput.getText(0,
                    Math.min(TEXT_INPUT_MAX_CAPACITY, textInput.getLength()));

            textInput.setText(DEFAULT_TEXT_INPUT);

            if (!input.isBlank() && !input.equals(DEFAULT_TEXT_INPUT)) {
                AddToChatHandler addToChatHandler = addToChatProperty.get();
                addToChatHandler.onNewChatMessage(input);
                addToChat(String.join(": ", "You", input));
                // "You: " avant un message indique au joueur que c'est son
                // message
            }
        });

        vBox.getChildren().addAll(chatListView, textInput, sendMessage);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setTitle(String.join("","Communique avec l'autre joueur (les messages ont une longueur maximum de ", String.valueOf(TEXT_INPUT_MAX_CAPACITY), ")"));
        stage.setScene(scene);

        stage.show();
    }

    /**
     * classe imbriquée qui hérite de StringConverter et redéfinit ses méthodes
     * toString et fromString
     */
    private static class CardBagStringConverter
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

    //  --- --- --- Extension
    private static class ticketListBG extends ListCell<Ticket> { //also used in graphicalPlayer //todo
        private ObservableGameState game;
        public ticketListBG(ObservableGameState game){
            this.game = game;
        }
        @Override
        public void updateItem(Ticket item, boolean empty){
            super.updateItem(item, empty);
            Map<Ticket, Integer> map = game.getTicketListPopUp();

            Map<Ticket, Integer> pointsIfHadAllAvailableRoute = PlayerState.ticketPointStatic(game.playerState().tickets(), game.allAvailableRoutesPlayer(game.playerState().routes()));

            if(!map.isEmpty() && item != null){
                if(map.get(item) > 0){    //pour réduire on pourrait mettre, si xx ne contient pas le ticket alors ...mais peut être plus long
                    setStyle("-fx-control-inner-background: \"#92db98\";");
                }else{
                    if(pointsIfHadAllAvailableRoute.get(item) < 0){
                        setStyle("-fx-control-inner-background: \"#e9787d\";");
                        setText(item.toString() + "      ; " + pointsIfHadAllAvailableRoute.get(item) + " points");
                    }else{
                        setText(item.toString());
                        setStyle(null);
                    }
                }
            }
        }
    }
}