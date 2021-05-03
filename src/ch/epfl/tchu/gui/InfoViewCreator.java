package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 * la partie de l'interface montrant :
 *  en haut: pour chaque joueur, un disque de la couleur de ses wagons, suivi des statistiques le concernant—
 *  nombre de billets et de cartes en main, wagons restants et points de construction obtenus,
 *  en bas: les cinq dernières informations sur le déroulement de la partie.
 */

class InfoViewCreator {
    public VBox vBox;
    
    /**
     * Le seul attribut de la classe est une instance de VBox
     * */
    private InfoViewCreator(VBox vBox) {
        this.vBox = vBox;
    }
    
    
    public static VBox createInfoView(PlayerId playerId, 
            Map<PlayerId, String> mapPlayerNames,
            ObservableGameState observableGame,
            ObservableList<Text> messageList) {
        
        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("info.css", "colors.css");
        
        Separator separator = new Separator();//séparateur orienté horizontalement
        
        /**Statistiques joueurs*/
        VBox vBoxPlayerStats = new VBox();
        vBoxPlayerStats.setId("player-stats");
        
        List<PlayerId> playerIdList = List.of(playerId, playerId.next());
        
        for(PlayerId player : playerIdList) {
            TextFlow textFlowPlayerStatistics = new TextFlow();
            vBoxPlayerStats.getChildren().add(textFlowPlayerStatistics);//est ce qu il faut rajouter le joueur courant en premier pour qu il soit placé en haut
            
            textFlowPlayerStatistics.getStyleClass()
            .add(player.toString());
            
            Circle circle = new Circle(5);//il faudra peut etre le placer
            circle.getStyleClass().add("filled");
            
            Text textPlayerStatistics = new Text();
            
            StringExpression stringExpression = Bindings.format(StringsFr.PLAYER_STATS, mapPlayerNames.get(player), observableGame.nbTicketsInHand(player),
                    observableGame.nbCardsInHand(player), observableGame.nbCarsInHand(player), observableGame.nbConstructionPoints(player));
            
            textPlayerStatistics.textProperty().bind(stringExpression);
            
            textFlowPlayerStatistics.getChildren().addAll(circle, textPlayerStatistics);
        }
        
        
        /*for (int i=0; i<mapPlayers.size(); ++i) {
            TextFlow textFlowPlayerStatistics = new TextFlow();
            vBoxPlayerStats.getChildren().add(textFlowPlayerStatistics);//est ce qu il faut rajouter le joueur courant en premier pour qu il soit placé en haut
            
            String playerIndex = String.valueOf(i+1);
            textFlowPlayerStatistics.getStyleClass()
            .add(String.join("_", List.of("PLAYER", playerIndex)));
            
            Circle circle = new Circle(5);//il faudra peut etre le placer
            circle.getStyleClass().add("filled");
            
            Text textPlayerStatistics = new Text();
            
            StringExpression stringExpression = Bindings.format(StringsFr.PLAYER_STATS, );
            //Comment accéder aux valeurs à passer? probablement a partir de la map
            textPlayerStatistics.textProperty().bind(stringExpression);
            
            textFlowPlayerStatistics.getChildren().addAll(circle, textPlayerStatistics);
        }*/
        
        /**Messages*/
        TextFlow textGameInfo = new TextFlow();
        textGameInfo.setId("game-info");
        Bindings.bindContent(textGameInfo.getChildren(), messageList);
        
        
        
        vBox.getChildren().addAll(vBoxPlayerStats, separator, textGameInfo);
        
        return vBox;
    }
   }
