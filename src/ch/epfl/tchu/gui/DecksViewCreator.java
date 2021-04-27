package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.gui.ObservableGameState;
import com.sun.javafx.font.directwrite.RECT;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 */
class DecksViewCreator{
    Pane pane;
    Node node;

    //constructeur privé
    private DecksViewCreator(){}

    public HBox createHandView(ObservableGameState game){    //noeud java fx, le numéro 3

//        final List<StackPane> allCardsPane = new ArrayList<StackPane>();

        /**
         *  carte + compteur
         */
        HBox cards = new HBox();
        cards.setId("hand-pane");

        for(Card card : Card.ALL){
            StackPane pane = cardAndTextLayout(card.name());
//            allCardsPane.add(pane);
        }
        ListView tickets = new ListView(game.playerTickets);
        tickets.setId("tickets");   //TODO check

        /**
         *  Construction du plus bas haut plus haut de la pyramide/hiérarchie
         */
        HBox root = new HBox();//new HBox(tickets, cards);
        root.getStylesheets().addAll("deck.css", "color.css");
        root.getChildren().addAll(tickets, cards);

        return new HBox(pane);
    }

    public createCardsView(ObservableGameState game, ObjectProperty<ClaimTicketHandler> claimTicketHandler, ObjectProperty<ClaimCardsHandler> claimCardsHandler){
        VBox cardPane = new VBox(); //new VBox(gaugedTickets, faceUpCardsPane, gaugedDeck);
        cardPane.getStylesheets().addAll("deck.css", "colors.css");
        cardPane.setId("card-pane");

        /**
         *  carte
         */
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            StackPane pane = cardLayout(game.faceUpCard(i).name());
            cardPane.getChildren().add(pane);
        }
        /**
         *  Pioche billets et cartes
         */
        Button gaugedTickets = gaugedButtonLayout(0.0f);
        Button gaugedDeck = gaugedButtonLayout(0.0f);

        //  ---
        cardPane.getChildren().addAll(gaugedTickets, gaugedDeck);

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
    private StackPane cardAndTextLayout(String cardName){
        Text count = new Text();
        count.getStyleClass().add("count");

        StackPane pane = cardLayout(cardName);
        pane.getChildren().add(count);
        return pane;
    }

    private Button gaugedButtonLayout(float percentage){  //(beetween 0.00 and 1.00)
        Button button = new Button();
        Group group = new Group();
        Rectangle background = new Rectangle(50, 5);
        Rectangle foreground = new Rectangle((int) (50 * percentage), 5);
//        Group group = new Group(background, foreground);
        group.getChildren().addAll(background, foreground);
        button.setGraphic(group);
        return button;
    }
}
