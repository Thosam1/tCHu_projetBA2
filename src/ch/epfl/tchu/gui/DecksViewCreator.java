package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


/**
 * @author Thösam Norlha-Tsang (330163)
 */
class DecksViewCreator{
    //constructeur privé
    private DecksViewCreator(){}

    public static HBox createHandView(ObservableGameState game){
        /**
         *  cartes + tickets
         */
        HBox cardsBox = new HBox();
        cardsBox.setId("hand-pane");
        for(Card card : Card.ALL){
            StackPane pane = cardAndTextLayout(card.name(), game.cardsOfInHand(card));  //ToDo comment bind le text et le nombre de cartes
            pane.visibleProperty().bind(Bindings.greaterThan(game.cardsOfInHand(card), 0));
            cardsBox.getChildren().add(pane);
        }

        ListView tickets = new ListView(game.playerTickets());
        tickets.setId("tickets");
        /**
         *  Construction du plus bas haut plus haut de la pyramide/hiérarchie
         */
        HBox root = new HBox();//new HBox(tickets, cards);
        root.getStylesheets().addAll("decks.css", "colors.css");
        root.getChildren().addAll(tickets, cardsBox);
        return root;
    }

    public static VBox createCardsView(ObservableGameState game, ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketHandler, ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHandler){  //ToDo faut-il mettre le ObjectProperty<> ?
        VBox cardPaneRoot = new VBox();
        cardPaneRoot.getStylesheets().addAll("decks.css", "colors.css");
        cardPaneRoot.setId("card-pane");

        /**
         *  carte
         */
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            StackPane pane = cardLayout(/*game.faceUpCardName(i)*/ Card.YELLOW.name()); //TODO rechanger    !!!!!!!!!!!!!!!
            final int I = i;
            pane.setOnMouseClicked(e -> {
                ActionHandlers.DrawCardHandler drawCards = drawCardsHandler.get();
                drawCards.onDrawCard(I);
            });
            game.faceUpCard(i).addListener((p, o, n) -> {
                pane.getStyleClass().set(0, (n.name() == Card.LOCOMOTIVE.name()) ? "NEUTRAL" : n.name());
            });
            cardPaneRoot.getChildren().add(pane);
        }
        /**
         *  Pioche billets et cartes
         */
        Button gaugedTickets = gaugedButtonLayout("Billets", game.percentTicketsLeft()); //ToDo changer la taille dedans
        gaugedTickets.disableProperty().bind(drawTicketHandler.isNull());

        gaugedTickets.setOnMouseClicked(e -> {    // ou bien setOnAction
            ActionHandlers.DrawTicketsHandler drawTickets = drawTicketHandler.get();;
            drawTickets.onDrawTickets();
        });

        Button gaugedDeck = gaugedButtonLayout("Cartes", game.percentCardsLeft());
        gaugedDeck.disableProperty().bind(drawCardsHandler.isNull());
                //ToDo changer la taille
        gaugedDeck.setOnMouseClicked(e -> {
            ActionHandlers.DrawCardHandler drawCards = drawCardsHandler.get();
            drawCards.onDrawCard(-1);     //ToDo est-ce qu'ici on assume que c'est seulement le bouton pour la pioche
        });

        /**
         *  ajout facultatif d'un label bouton
         */
        cardPaneRoot.getChildren().add(0, gaugedTickets);   // has to be first
        cardPaneRoot.getChildren().add(gaugedDeck);     // has to be last
        return cardPaneRoot;
    }

    private static StackPane cardLayout(String cardName){
        cardName = (cardName == Card.LOCOMOTIVE.name()) ? "NEUTRAL" : cardName;
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");

        Rectangle filledInside = new Rectangle(40, 70);
        filledInside.getStyleClass().addAll("filled", "inside");

        Rectangle trainImage = new Rectangle(40, 70);
        trainImage.getStyleClass().add("train-image");

        StackPane pane = new StackPane(outside, filledInside, trainImage);
//        pane.getChildren().addAll(outside, filledInside, trainImage);
        pane.getStyleClass().addAll(cardName, "card");
        return pane;
    }

    private static StackPane cardAndTextLayout(String cardName, ReadOnlyIntegerProperty integer){       // TODO y aurait-il moyen de faire le binding, plus haut pour rendre cette méthode plus réutilisable ?  -> pour le "integer" accéder depuis plus bas
        Text count = new Text();
        count.getStyleClass().add("count");
        count.textProperty().bind(Bindings.convert(integer));
        count.visibleProperty().bind(Bindings.greaterThan(integer, 0));

        StackPane pane = cardLayout(cardName);
        pane.getChildren().add(count);
        return pane;
    }

    private static Button gaugedButtonLayout(String label, ReadOnlyIntegerProperty percentage){  //(beetween 0 and 100)    // bind
        Button button = new Button();
        button.getStyleClass().add("gauged");

        Rectangle background = new Rectangle(50, 5);
        background.getStyleClass().add("background");

        Rectangle foreground = new Rectangle(50, 5);
        foreground.getStyleClass().add("foreground");
        foreground.widthProperty().bind(percentage.multiply(50).divide(100));   //toDo

        Group group = new Group(background, foreground);  //-> mieux
        button.setGraphic(group);
        button.setText(label);
        return button;
    }
}

/*
    Questions :
    0) vérifier les setOnMouse vs setOnAction   // done
    1) création des 5 faceUpcards
        - lambda et index pour piocher
        - addListener pour le label
    2) Pour les boutons et leur pourcentage, comment accéder au rectangle "background"  // done
    3) CardAndTextLayout -> comment éviter de mettre l'integerProperty dans le constructeur ? comment y accéder depuis dehors ?
 */