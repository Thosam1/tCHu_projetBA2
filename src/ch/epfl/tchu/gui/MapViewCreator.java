package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * La classe MapViewCreator, non instanciable et package private (!), 
 * contient une unique méthode publique, nommée createMapView et permettant de créer la vue de la carte.
 * */
class MapViewCreator {
    Pane pane;
    
    private MapViewCreator(Pane pane) {
        this.pane = pane;
    }
    
    public static Pane createMapView(ObservableGameState observableGame, 
            ObjectProperty<ClaimRouteHandler> objectProperty,
            CardChooser cardChooser) {
        Pane pane = new Pane();
        pane.getStylesheets().addAll("map.css","colors.css");
        ImageView view = new ImageView();
        pane.getChildren().add(view);
        
        for(Route route : ChMap.routes()) {
            //créé un group par route et en fait un enfant de pane
            Group groupRoute = new Group();
            groupRoute.setId(route.id());
            
            String couleur = route.color()==null ? "NEUTRAL" : route.color().toString();
            groupRoute.getStyleClass().addAll("route", route.level().toString(), route.color().toString());
            
            observableGame.getRouteOwner(route).addListener(
                    (p, o, n) -> groupRoute.getStyleClass().add(n.toString()));
            
            //attacher un auditeurà la propriété de l'état de jeu observable contenant le propriétaire de la route
            //donc il me faut un accès à routeOwners
            
   
            //comment gérer l'activation?
            //ou alors par défaut la propriété est activé
            //et il suffit donc de lui donné une condition de désactivation

            
            //désactive le groupe représentant une route lorsque le joueur ne peut pas s'en emparer
            groupRoute.disableProperty().bind(
                    objectProperty.isNull().or(observableGame.claimable(route).not()));
            
            
            //dans la video de début d'année, la route change quand la souris passe dessus
            //ce n'est pas ce qu on fait là
            groupRoute.setOnMouseClicked(e -> {
                ClaimRouteHandler claimRouteH = objectProperty.get();
                List<SortedBag<Card>> possibleClaimCards = observableGame.getPossibleClaimCards(route);
                if(possibleClaimCards.size()==1) {
                    claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                }
                else if(possibleClaimCards.size()>1) {
                    ChooseCardsHandler chooseCardsH =
                            chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
                else{}// do nothing //je pense que ce cas est inutile car la  propiété est seulement activé si la route est claimable
            });
            
            //Ce qu on nous donne
            List<SortedBag<Card>> possibleClaimCards = observableGame.getPossibleClaimCards(route);
            ClaimRouteHandler claimRouteH = objectProperty.get();
            ChooseCardsHandler chooseCardsH =
              chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
            cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
            
            
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
        return pane;
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
