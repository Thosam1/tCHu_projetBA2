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
 * @author Aymeric de chillaz (326617)
 * La classe MapViewCreator, non instanciable et package private (!), 
 * contient une unique méthode publique et statique, nommée createMapView et permettant de créer la vue de la carte.
 * */
class MapViewCreator {
    public Pane pane;
    
    /**
     * Le seul attribut de la classe est une instance de Pane
     * */
    private MapViewCreator(Pane pane) {
        this.pane = pane;
    }
    
    /**
     * Cette méthode prend trois paramètres:
     * @param observableGame qui est une instance de ObservableGameState, 
     *      nous en avons besoin pour accéder à ses propriétés routeOwners et possibleClaimCards
     *      Mais aussi pour savoir si une route est "claimable"
     * @param objectProperty qui est une propiété contenant un ClaimRouteHandler 
     *      Nous voulons savoir si la propriété est null, et accéder à son contenu
     * @param cardChooser est utile pour sa méthode chooseCards qui provoque l'apparition d'un dialogue 
     *      demandant au joueur de choisir l'ensemble de cartes qu'il désire utiliser pour (tenter de) s'emparer d'une route
     *      dans le cas ou il a plusieurs options
     *      
     *      Cette méthode créé l'interface utilisateur (la vue et le controleur)
     *      La racine du graphe de scène est de type Pane. Nous lui plusieurs enfants qui eux meme ont des enfants etc.
     *      Finalement, nous nous retrouvons avec un affichage de la carte en fond d'écran, avec les routes affiché sur celle ci.
     *      
     *      Ces routes peuvent changer en fonction de l'interaction (ce qui est aussi géré par cette classe et est plus détaillé ci-dessous)
     *      
     *      La hiérarchie du graphe de scène est la suivante:
     *          1)Pane
     *          2.1)instance de ImageView contenant le fond de la carte (n'a pas d enfants)
     *          2.2)un Group par route dans le jeux (groupRoute)
     *          3)chacuns de ces groupRoute a autant d'enfants de type Group que sa longueur(son nombre de cases) ces enfants s'appellent groupCase  
     *          4)Chaque groupCase a deux enfants de types différents
     *          4.1)un enfant de type rectangle(rec1)
     *          4.2)un enfant de type Group qui a lui même trois enfants (groupWagon)
     *          5.1)un rectangle(rec2)
     *          5.2)deux cercles(circ1 et circ2)
     * */
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
            
            //la couleur d'une route peut etre null ce qui correspond à une route neutre, dans ce cas le String associé est "NEUTRAL"
            String color = route.color()==null ? "NEUTRAL" : route.color().toString();
            groupRoute.getStyleClass().addAll("route", route.level().toString(), color);
            
            //ici on attache un auditeur à la propriété de l'état de jeu observable contenant le propriétaire de la route
            //lorsque cette propriété change, on rajoute une classe de Style associé à l'identité de la nouvelle valeur i.e. n.toString()
            observableGame.getRouteOwner(route).addListener(
                    (p, o, n) -> groupRoute.getStyleClass().add(n.toString()));

            
            //désactive le groupe représentant une route lorsque le joueur ne peut pas s'en emparer
            groupRoute.disableProperty().bind(
                    objectProperty.isNull().or(observableGame.claimable(route).not()));
            
            //Lorsqu'un joueur clique sur un élément quelconque d'un groupe représentant une route, cela signifie qu'il désire s'en emparer — 
            //ou tenter de le faire dans le cas d'un tunnel.
            groupRoute.setOnMouseClicked(e -> {
                ClaimRouteHandler claimRouteH = objectProperty.get();
                List<SortedBag<Card>> possibleClaimCards = observableGame.getPossibleClaimCards(route);
                
                if(possibleClaimCards.size()==1) {
                    //possibleClaimCards ontient un seul ensemble de cartes, donc le joueur n'a pas le choix des cartes à utiliser pour s'emparer de la route
                    //la méthode onClaimRoute du gestionnaire d'action passé à createMapView peut être appelée
                    
                    claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                }
                
                else {//cas ou il y a plusieurs possibilité de cartes qui peuvent permettre de prendre pocession de la route
                    //provoque l'apparition d'un dialogue demandant au joueur de choisir l'ensemble de cartes qu'il désire utiliser pour essayer de  s'emparer de la route
                    ChooseCardsHandler chooseCardsH =
                            chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });
            
           
            
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
