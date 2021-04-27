package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

class MapViewCreator {
    Pane pane;
    
    private MapViewCreator(Pane pane) {
        this.pane = pane;
    }
    
    public static void /*ou MapViewCreator ?*/ createMapView(ObservableGameState observableGame, 
            ObjectProperty<ClaimRouteHandler> objectProperty,
            CardChooser chooser) {
        Pane pane = new Pane();
        pane.getStylesheets().addAll("map.css","colors.css");
        ImageView view = new ImageView();
        pane.getChildren().add(view);
        
        for(Route route : ChMap.routes()) {
            //créé un group par route et en fait un enfant de pane
            Group groupRoute = new Group();
            groupRoute.setId(route.id());
            
            //Je ne suis pas sur des string passé à addAll
            groupRoute.getStyleClass().addAll("route", "UNDERGROUND", "NEUTRAL");
            //groupRoute.getStyleClass().addAll("route", route.level().toString(), route.color().toString());
            
            groupRoute.disableProperty().bind(
                    objectProperty.isNull().or(gameState.claimable(route).not()));
            //Je ne comprends pas bien la description de gameState ici
            
            
            
            pane.getChildren().add(groupRoute);
            
            //créé un group correspondant à chaque cases de la route (autant que la longueur de cette route)
            for(int i = 1; i <= route.length(); ++i) {
                Group groupCase = new Group();
                groupCase.setId(String.join("_", List.of(route.id(), String.valueOf(i))));
                
                groupRoute.getChildren().add(groupCase);
                
                Rectangle rec1 = new Rectangle(36, 12);
                rec1.getStyleClass().addAll("track", "filled");
                
                Group groupWagon = new Group();
                groupWagon.getStyleClass().add("car");
                
                //créé le lien de parenté entre groupeCase et ses enfants: rec, groupWagon
                groupCase.getChildren().addAll(rec1, groupWagon);
                
                //créé les enfants de groupCase
                Rectangle rec2 = new Rectangle(36, 12);
                Circle circ1 = new Circle(12, 6, 3);
                Circle circ2 = new Circle(24, 6, 3);
                
                rec2.getStyleClass().add("filled");
                
                //attribut les enfants de groupWagon
                groupWagon.getChildren().addAll(rec2, circ1, circ2);
            }
        }
    }
    
    /**
     * interface fonctionnelle imbriquée dans la classe
     * Il s'agit d'un sélectionneur de cartes
     * */
    @FunctionalInterface
    interface CardChooser {
      void chooseCards(List<SortedBag<Card>> options,
               ChooseCardsHandler handler);
    }
}
