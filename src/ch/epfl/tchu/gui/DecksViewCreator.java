package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static ch.epfl.tchu.game.Constants.*;

/**
 * @author Thösam Norlha-Tsang (330163) classe non-instanciable dont le but est
 *         de contenir 2 méthodes qui construisent un graphe de scène
 *         représentant des cartes
 */
final class DecksViewCreator {
    private static final int EXTERIOR_CARD_WIDTH = 60;
    private static final int EXTERIOR_CARD_HEIGHT = 90;
    private static final int INTERIOR_CARD_WIDTH = 40;
    private static final int INTERIOR_CARD_HEIGHT = 70;
    private static final int BACKGROUND_BUTTON_WIDTH = 50;
    private static final int BACKGROUND_BUTTON_HEIGHT = 5;


    // constructeur privé
    private DecksViewCreator() {
    }

    /**
     * Cette méthode prend un paramètre:
     * 
     * @param game
     *            qui est une instance de ObservableGameState (l'état du jeu
     *            observable) nous en avons besoin pour accéder à ses propriétés
     *            cardsOfInHand() et playerTickets -pour avoir le nombre de
     *            chaque type de carte et les billets en possession
     * @return la vue de la main
     *
     *         La hiérarchie du graphe de scène est la suivante: 1)HBox
     *         2.1)ListView contenant les billets en possession du joueur enfant
     *         de HBox 2.2)HBox enfant de HBox
     *
     *         2.2.1)StackPane enfant de HBox 2.2.1.1) Rectangle représentant le
     *         dehors de la carte 2.2.1.2) Rectangle représentant l'intérieur de
     *         la carte 2.2.1.3) Rectangle représentant le train-image de la
     *         carte 2.2.1.4) Text affichant le nombre de carte de la sorte
     *         actuelle (Stackpane) correspondant/en possession )Chaque
     *         groupCase a deux enfants de types différents
     *
     *         2.2.2) la même que StackPane 2.2.1) mais pour la deuxième couleur
     *         . . . (total 8 couleurs -> 8 StackPane)
     *
     */
    public static HBox createHandView(ObservableGameState game) {
        /**
         * cartes + tickets
         */
        HBox cardsBox = new HBox();
        cardsBox.setId("hand-pane");
        for (Card card : Card.ALL) {
            StackPane pane = cardAndTextLayout(card.name(),
                    game.cardsOfInHand(card));
            pane.visibleProperty()
                    .bind(Bindings.greaterThan(game.cardsOfInHand(card), 0));
            cardsBox.getChildren().add(pane);
        }

        ListView<Ticket> tickets = new ListView<>(game.playerTickets());
        tickets.setId("tickets");
        /**
         * Construction du plus bas haut plus haut de la pyramide/hiérarchie
         */
        HBox root = new HBox();// new HBox(tickets, cards);
        root.getStylesheets().addAll("decks.css", "colors.css");
        root.getChildren().addAll(tickets, cardsBox);
        return root;
    }

    /**
     *
     * @param game
     *            qui est une instance de ObservableGameState (l'état de jeu
     *            observable)
     * @param drawTicketHandler
     *            un gestionnaire d'action: la première contient celui gérant le
     *            tirage de billets
     * @param drawCardsHandler
     *            un gestionnaire d'action: la seconde contient celui gérant le
     *            tirage de cartes
     * @return la vue sur les cartes visibles, la pioche des cartes et la pioche
     *         des billets
     *
     *         La hiérarchie du graphe de scène est la suivante: 1)VBox
     *         1.1)Button représentant la pioche de billets 1.1.1)Group
     *         1.1.1.1)Rectangle pour l'arrière-plan 1.1.1.2)Rectangle pour
     *         l'avant-plan
     *
     *         1.2)StackPane (x5 cars 5 FaceUpCards) 1.2.1)Rectangle
     *         représentant le dehors de la carte 1.2.2)Rectangle représentant
     *         l'intérieur de la carte 1.2.3)Rectangle représentant le
     *         train-image de la carte . . . 1.7)Button représentant la pioche
     *         de cartes - le deck 1.7.1)Group 1.7.1.1)Rectangle pour
     *         l'arrière-plan 1.7.1.2)Rectangle pour l'avant-plan
     */
    public static VBox createCardsView(ObservableGameState game,
            ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketHandler,
            ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHandler) {
        VBox cardPaneRoot = new VBox();
        cardPaneRoot.getStylesheets().addAll("decks.css", "colors.css");
        cardPaneRoot.setId("card-pane");

        /**
         * carte
         */
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            StackPane pane = cardEmptyLayout();
            final int I = i;
            pane.setOnMouseClicked(e -> {
                ActionHandlers.DrawCardHandler drawCards = drawCardsHandler
                        .get();
                if (drawCards != null) {
                    drawCards.onDrawCard(I);
                }
            });
            game.faceUpCard(i).addListener((p, o, n) -> {
                if ((pane.getStyleClass().size() == 2)) {
                    pane.getStyleClass().set(0, assignCardStyle(n.name()));
                } else {
                    pane.getStyleClass().add(0, assignCardStyle(n.name()));
                }
            });
            cardPaneRoot.getChildren().add(pane);
        }
        /**
         * Pioche billets et cartes
         */
        Button gaugedTickets = gaugedButtonLayout(StringsFr.TICKETS,
                game.percentTicketsLeft());
        gaugedTickets.disableProperty().bind(drawTicketHandler.isNull());
        gaugedTickets.setOnMouseClicked(e -> { // ou bien setOnAction
            ActionHandlers.DrawTicketsHandler drawTickets = drawTicketHandler
                    .get();
            drawTickets.onDrawTickets();
        });

        Button gaugedDeck = gaugedButtonLayout(StringsFr.CARDS,
                game.percentCardsLeft());
        gaugedDeck.disableProperty().bind(drawCardsHandler.isNull());
        gaugedDeck.setOnMouseClicked(e -> {
            ActionHandlers.DrawCardHandler drawCards = drawCardsHandler.get();
            drawCards.onDrawCard(DECK_SLOT);
        });

        cardPaneRoot.getChildren().add(0, gaugedTickets); // has to be first
                                                          // (index)
        cardPaneRoot.getChildren().add(gaugedDeck); // has to be last (index)
        return cardPaneRoot;
    }

    /**
     * méthode pour donner un "style" au cartes, permets d'éviter de recopier du
     * code
     * 
     * @param cardName le nom de la carte (comme défini dans la classe Card)
     * @return le nom de la carte
     */
    private static String assignCardStyle(String cardName) {
        return (cardName.equals(Card.LOCOMOTIVE.name()) ? "NEUTRAL" : cardName);
    }

    /**
     * @return une pane/image de carte, vide dans le sens "couleur-style"
     *         utilisé pour l'initialisation (on ne sait pas encore quelle
     *         couleur la carte prendra)
     */
    private static StackPane cardEmptyLayout() {
        // les deux valeurs passées aux constructeur de Rectangle
        // correspondent au dimensions largeur et hauteur
        Rectangle outside = new Rectangle(EXTERIOR_CARD_WIDTH, EXTERIOR_CARD_HEIGHT);
        outside.getStyleClass().add("outside");

        Rectangle filledInside = new Rectangle(INTERIOR_CARD_WIDTH, INTERIOR_CARD_HEIGHT);
        filledInside.getStyleClass().addAll("filled", "inside");

        Rectangle trainImage = new Rectangle(INTERIOR_CARD_WIDTH, INTERIOR_CARD_HEIGHT);
        trainImage.getStyleClass().add("train-image");

        StackPane pane = new StackPane(outside, filledInside, trainImage);
        pane.getStyleClass().add("card");

        return pane;
    }

    /**
     * @param cardName
     *            le nom de la carte
     * @return une pane/image de carte dont le nom de la carte pris en argument
     */
    private static StackPane cardLayout(String cardName) {
        StackPane pane = cardEmptyLayout();
        cardName = assignCardStyle(cardName);
        pane.getStyleClass().add(0, cardName);
        return pane;
    }

    /**
     * @param cardName
     *            le nom de la carte
     * @param integer
     *            le nombre qui doit être affiché par dessus (utilisé dans le
     *            handview)
     * @return une pane/image de carte dont le nom de la carte et le nombre sont
     *         pris en argument
     */
    private static StackPane cardAndTextLayout(String cardName,
            ReadOnlyIntegerProperty integer) {
        Text count = new Text();
        count.getStyleClass().add("count");
        count.textProperty().bind(Bindings.convert(integer));
        count.visibleProperty().bind(Bindings.greaterThan(integer, 0));

        StackPane pane = cardLayout(cardName);
        pane.getChildren().add(count);
        return pane;
    }

    /**
     * @param label
     *            le titre/label/nom/texte du button
     * @param percentage
     *            le pourcentage de la barre/jauge qui doit être remplie
     * @return un bouton avec un titre et une barre remplie selon le pourcentage
     */
    private static Button gaugedButtonLayout(String label,
            ReadOnlyIntegerProperty percentage) {
        Button button = new Button();
        button.getStyleClass().add("gauged");

        // les deux valeurs passées aux constructeur de Rectangle correspondent
        // au dimensions largeur et hauteur
        Rectangle background = new Rectangle(BACKGROUND_BUTTON_WIDTH, BACKGROUND_BUTTON_HEIGHT);
        background.getStyleClass().add("background");

        Rectangle foreground = new Rectangle(BACKGROUND_BUTTON_WIDTH, BACKGROUND_BUTTON_HEIGHT);
        foreground.getStyleClass().add("foreground");
        foreground.widthProperty().bind(percentage.multiply(BACKGROUND_BUTTON_WIDTH).divide(100));

        Group group = new Group(background, foreground);
        button.setGraphic(group);
        button.setText(label);
        return button;
    }
}