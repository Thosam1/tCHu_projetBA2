package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author Thösam Norlha-Tsang (330163)
 *
 */
class DecksViewCreator{
    Pane pane;
    Node node;

    //constructeur privé
    private DecksViewCreator(){}

    public HBox createHandView(ObservableGameState game){

//        Map<Card, StackPane> allCardsPane = Map.of();

        /**
         *  carte + compteur
         */
        HBox cards = new HBox();
        cards.setId("hand-pane");

        for(Card card : Card.ALL){
            StackPane pane = cardAndTextLayout(card.name(), game.cardsOfInHand(card));
            pane.visibleProperty().bind(Bindings.greaterThan(game.cardsOfInHand(card), 0));  //TODO cette ligne ou la ligne en bas ?
//            pane.setVisible(game.cardsOfInHand(card).greaterThan(0).get());

//            allCardsPane.put(card, pane);
            cards.getChildren().add(pane);
//            allCardsPane.add(pane);
        }
        ListView tickets = new ListView(game.playerTickets());    //TODO comment ?
        tickets.setId("tickets");
        /**
         *  Construction du plus bas haut plus haut de la pyramide/hiérarchie
         */
        HBox root = new HBox();//new HBox(tickets, cards);
        root.getStylesheets().addAll("deck.css", "color.css");
        root.getChildren().addAll(tickets, cards);

        return new HBox(pane);
    }

    public VBox createCardsView(ObservableGameState game, ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketHandler, ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHandler){  //ToDo faut-il mettre le ObjectProperty<> ?
        VBox cardPane = new VBox(); //new VBox(gaugedTickets, faceUpCardsPane, gaugedDeck); //ToDo quel est le "related problem" ?
        cardPane.getStylesheets().addAll("deck.css", "colors.css");
        cardPane.setId("card-pane");

        /**
         *  carte
         */
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            StackPane pane = cardLayout(game.faceUpCardName(i));
            if(pane.isPressed()){ drawCardsHandler.onDrawCard(i); } //ToDo je ne suis vrmt pas sûr de commentfaire fonctionner ceci.
            cardPane.getChildren().add(pane);
//            pane.getStyleClass().addListener(la propriété qui a changé, l'ancienne valeur, la nouvelle valeur); //ToDo ici je ne vois pas l'intérêt du Listener parce que je la recrée dans tous les cas juste en haut...
        }
        /**
         *  Pioche billets et cartes
         */
        Button gaugedTickets = gaugedButtonLayout(0.0f, (ObjectProperty<ActionHandlers>) drawTicketHandler);
        if(gaugedTickets.isPressed()){
            drawTicketHandler.onDrawTickets();
        }

        Button gaugedDeck = gaugedButtonLayout(0.0f, (ActionHandlers) drawCardsHandler);
        if(gaugedDeck.isPressed()){
            drawCardsHandler.onDrawCard(-1);    //ToDo est-ce qu'ici on assume que c'est seulement le bouton pour la pioche
        }
//        if(cardPane.getChildren())

        //  ---
        cardPane.getChildren().addAll(gaugedTickets, gaugedDeck);
        return cardPane;
    }

    private StackPane cardLayout(String cardName){
        cardName = (cardName == Card.LOCOMOTIVE.name()) ? "NEUTRAL" : cardName;
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");

        Rectangle filledInside = new Rectangle(40, 70);
        filledInside.getStyleClass().addAll("filled", "inside");

        Rectangle trainImage = new Rectangle(40, 70);
        trainImage.getStyleClass().add("train-image");

        StackPane pane = new StackPane();
        pane.getChildren().addAll(outside, filledInside, trainImage);
        pane.getStyleClass().addAll(cardName, "card");

        return pane;
    }
    private StackPane cardAndTextLayout(String cardName, ReadOnlyIntegerProperty integer){       // TODO y aurait-il moyen de faire le binding, plus haut pour rendre cette méthode plus réutilisable ?
        Text count = new Text();
        count.getStyleClass().add("count");
        count.textProperty().bind(Bindings.convert(integer));
        count.visibleProperty().bind(Bindings.greaterThan(integer, 0));

        StackPane pane = cardLayout(cardName);
        pane.getChildren().add(count);
        return pane;
    }

    private Button gaugedButtonLayout(float percentage, ObjectProperty<ActionHandlers> handler){  //(beetween 0.00 and 1.00)
        Button button = new Button();
        Group group = new Group();
        Rectangle background = new Rectangle(50, 5);
        Rectangle foreground = new Rectangle((int) (50 * percentage), 5);
//        Group group = new Group(background, foreground);
        group.getChildren().addAll(background, foreground);
        button.setGraphic(group);

        button.disableProperty().bind(handler.isNull());
        return button;
    }
}
