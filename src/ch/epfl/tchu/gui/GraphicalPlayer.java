package ch.epfl.tchu.gui;

import java.awt.*;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.text.html.ListView;

public final class GraphicalPlayer {
    private ObservableGameState observableGame;
    private ObservableList<Text> messageList;
    private final PlayerId playerId;
    private final Map<PlayerId, String> mapPlayerNames;
    
    //propriétés contenant les gestionnaires d'action
    //si elle contient null, alors l'action en question est actuellement interdite
    private ObjectProperty<DrawCardHandler> drawCardProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ClaimRouteHandler> claimRouteProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ChooseTicketsHandler> chooseTicketsProperty = new SimpleObjectProperty<>();
    
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> mapPlayerNames) {
        this.playerId = playerId;
        this.mapPlayerNames = mapPlayerNames;
    }
    
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        observableGame.setState(newGameState, newPlayerState);
    }
    
    public void receiveInfo(String message) {
        messageList.add(new Text(message));
        if(messageList.size()==6) {
            messageList.remove(0);
        }
    }
    
    public void startTurn(DrawCardHandler drawCardHandler, ClaimRouteHandler claimRouteHandler,
            ChooseTicketsHandler chooseTicketsHandler) {
        if(observableGame.getPublicGameState().canDrawTickets()) {
            chooseTicketsProperty.set((tickets) ->{
                chooseTicketsHandler.onChooseTickets(tickets);
                drawCardProperty.set(null);
                claimRouteProperty.set(null);
                chooseTicketsProperty.set(null);//je ne suis pas sur de ça
            });
        }
        
        if(observableGame.getPublicGameState().canDrawCards()) {
            drawCardProperty.set((a) ->{
                drawCardHandler.onDrawCard(a);
                chooseTicketsProperty.set(null);
                claimRouteProperty.set(null);
                drawCardProperty.set(null);//je ne suis pas sur de ça
            });
        }
        //La propriété correspondant à la prise de possession d'une route doit toujours être remplie 
        //lorsque le tour commence (ce qui est le cas lors de l'appel à startTurn)
        claimRouteProperty.set((route, cards) -> {
            claimRouteHandler.onClaimRoute(route, cards);
            chooseTicketsProperty.set(null);
            claimRouteProperty.set(null);
            drawCardProperty.set(null);
        });
    }
    
    public void chooseTickets(SortedBag<Ticket> ticketsToChoose, ChooseTicketsHandler chooseTicketsHandler) {
        Preconditions.checkArgument(ticketsToChoose.size()==3 || ticketsToChoose.size()==5);
        //TODO
       
    }
    
    public void drawCard(DrawCardHandler drawCardHandler) {
        drawCardProperty.set((a) -> {
            drawCardHandler.onDrawCard(a);
            drawCardProperty.set(null);  
        });
    }
    
    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ChooseCardsHandler chooseCardsHandler) {
        
    }
    
    public void choosedAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ChooseCardsHandler chooseCardsHandler) {}

    private Stage mainSceneGraph(Node mapView, Node drawView, Node handView, Node infoView){
        BorderPane borderPane = new BorderPane(mapView, null, drawView, handView, infoView);
        Stage root = new Stage();
        root.setScene(new Scene(borderPane));
        root.setTitle("tCHu" + " - " + mapPlayerNames.get(playerId));
        root.show();    //todo show-void or return ?
        return root;
    }


    public static Stage chooseGraph(String title, String introText, String number, ObservableList list){  // titre est donné soit par la constante TICKETS_CHOICE, soit par la constante CARDS_CHOICE de StringsFr
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner();  //todo ? soit la fenêtre principale de l'interface (créée par le constructeur de GraphicalPlayer)
        stage.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();

        Text text = new Text(number == null ? introText : String.format(StringsFr.CHOOSE_TICKETS, number)); //toDo mieux de formater en dehors ou dedans ?
        TextFlow textFlow = new TextFlow(text);


        ListView listView = new ListView(list);
        listView.

        Button button = new Button();

        vBox.getChildren().addAll(textFlow, listView, button);

        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setTitle(title);      //depends
        stage.setScene(scene);


        return stage;
    }

}
