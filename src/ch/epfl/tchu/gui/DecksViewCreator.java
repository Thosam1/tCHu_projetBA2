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
import javafx.scene.text.Text;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.ArrayList;
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

        List<StackPane> allCardsPane = new ArrayList<StackPane>();

        /**
         *  carte + compteur
         */
        for(Card card : Card.ALL){
            StackPane pane = cardLayout();
            allCardsPane.add(pane);
        }
        // --- ---

        HBox cards = new HBox(allCardsPane);
        ListView tickets = new ListView(ObservableGameState.playerTickets);
        HBox root = new HBox(tickets, cards);

        return new HBox(pane);
    }

    public createCardsView(ObservableGameState game, ObjectProperty<ClaimTicketHandler> claimTicketHandler, ObjectProperty<ClaimCardsHandler> claimCardsHandler){
        List<StackPane> faceUpCardsPane = new ArrayList<StackPane>();
        /**
         *  carte
         */
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++){
            StackPane pane = cardLayout();
            faceUpCardsPane.add(pane);
        }
        /**
         *  Pioche billet
         */
        Button gaugedTickets = gaugedButtonLayout(0.0f);

        /**
         *  Pioche cartes
         */
        Button gaugedDeck = gaugedButtonLayout(0.0f);


        VBox cardPane = new VBox(gaugedTickets, faceUpCardsPane, gaugedDeck);
    }

    private StackPane cardLayout(){
        Rectangle outside = new Rectangle(60, 90);
        Rectangle filledInside = new Rectangle(40, 70);
        Rectangle trainImage = new Rectangle(40, 70);
        Text count = new Text();
        return new StackPane(outside, filledInside, trainImage, count);
    }

    private Button gaugedButtonLayout(float percentage){  //(beetween 0.00 and 1.00)
        Button button = new Button();
        Rectangle background = new Rectangle(50, 5);
        Rectangle foreground = new Rectangle((int) (50 * percentage), 5);
        Group group = new Group(background, foreground);
        button.setGraphic(group);
        return button;
    }
}
