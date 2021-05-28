package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @author Aymeric de chillaz (326617) La classe InfoViewCreator consiste en la
 *         partie de l'interface montrant : en haut: pour chaque joueur, un
 *         disque de la couleur de ses wagons, suivi des statistiques le
 *         concernant— nombre de billets et de cartes en main, wagons restants
 *         et points de construction obtenus, en bas: les cinq dernières
 *         informations sur le déroulement de la partie.
 */

final class InfoViewCreator {
    private static final int CIRCLE_RADIUS = 5;

    private InfoViewCreator() {
    }

    /**
     *  voir description au dessus-
     * @param playerId
     * @param mapPlayerNames
     * @param observableGame
     * @param messageList
     * @return une partie de l'interface contenant les statistiques des deux joueurs
     */
    public static VBox createInfoView(PlayerId playerId,
            Map<PlayerId, String> mapPlayerNames,
            ObservableGameState observableGame,
            ObservableList<Text> messageList) {

        // créé un VBox et lui attribut ses feuilles de style
        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("info.css", "colors.css");

        // créé un séparateur orienté horizontalement
        Separator separator = new Separator();

        /** Statistiques joueurs */
        VBox vBoxPlayerStats = new VBox();
        vBoxPlayerStats.setId("player-stats");

        List<PlayerId> playerIdList = List.of(playerId, playerId.next());

        for (PlayerId player : playerIdList) {
            TextFlow textFlowPlayerStatistics = new TextFlow();
            vBoxPlayerStats.getChildren().add(textFlowPlayerStatistics);
            // rajouter le joueur courant en premier permet de le placer en haut
            // du VBox
            textFlowPlayerStatistics.getStyleClass().add(player.toString());

            // Ce cercle de rayon 5 est placé en haut à gauche du
            // TextFlowPlayerStatistics par défaut
            Circle circle = new Circle(CIRCLE_RADIUS);
            circle.getStyleClass().add("filled");

            Text textPlayerStatistics = new Text();

            /**
             * L'instance de Text contenant les statistiques de chacun des
             * joueurs doit contenir la chaîne PLAYER_STATS de StringsFr,
             * formatée de manière à ce que les différentes occurrences de %s
             * soit remplacées par les bonnes valeurs. Ceci se fait avec la
             * méthode format de Bindings
             */
            StringExpression stringExpression = Bindings.format(
                    StringsFr.PLAYER_STATS, mapPlayerNames.get(player),
                    observableGame.nbTicketsInHand(player),
                    observableGame.nbCardsInHand(player),
                    observableGame.nbCarsInHand(player),
                    observableGame.nbConstructionPoints(player));

            textPlayerStatistics.textProperty().bind(stringExpression);

            textFlowPlayerStatistics.getChildren().addAll(circle,
                    textPlayerStatistics);
        }

        /** Messages */
      //créé le text flow qui contiendra les 5 derniers messages
        TextFlow textGameInfo = new TextFlow();
        textGameInfo.setId("game-info");
        
        // Le contenu de la liste d'enfants—retournée par getChildren — de
        // l'instance de TextFlow contenant les messages d'information doit être
        // lié à celui de la liste d'informations passée à la méthode
        // createInfoView.
        Bindings.bindContent(textGameInfo.getChildren(), messageList);

        vBox.getChildren().addAll(vBoxPlayerStats, separator, textGameInfo);

        return vBox;
    }
}
