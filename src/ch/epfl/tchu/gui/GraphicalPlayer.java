package ch.epfl.tchu.gui;

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
import javafx.scene.text.Text;

public final class GraphicalPlayer {
    private ObservableGameState observableGame;
    private ObservableList<Text> messageList;
    
    //propriétés contenant les gestionnaires d'action
    //si elle contient null, alors l'action en question est actuellement interdite
    private ObjectProperty<DrawCardHandler> drawCardProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ClaimRouteHandler> claimRouteProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ChooseTicketsHandler> chooseTicketsProperty = new SimpleObjectProperty<>();
    
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> mapPlayerNames) {
        
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
}
