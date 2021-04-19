package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * représente un mandataire (proxy en anglais) de joueur distant
 * @author Aymeric de chillaz (326617)
 * */
public final class RemotePlayerProxy implements Player{
    private Socket socket;
    
    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;
    }
    
    /**
     * méthode privée qui permet d'envoyer un message étant donné son identité
     * et les chaines de caractères correspondants à la sérialisation de ses arguments
     * 
     * catch à la fin de la méthode a pour but d'attraper les exceptions de type IOException 
     * et de les lever à nouveau, en quelque sorte, sous forme d'exceptions équivalentes 
     * mais de type UncheckedIOException. La différence entre les deux types d'exception 
     * est que le premier est un type d'exception checked, le second pas.
     * 
     * @param messageId : l'identité du message (de type MessageId)
     * @param string : la chaine de caractère à transmettre
     * */
    private void messageOut(String string) {
        try (BufferedWriter w =
                new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(),
                               StandardCharsets.US_ASCII))) {
            
            w.write(string);
            w.write('\n');
            w.flush();
            } catch (IOException e) {
            
                throw new UncheckedIOException(e);
               } 
    }
    
    /**
     * méthode privée qui permet de recevoir un message et de le retourner sous la forme de String
     * 
     * catch à la fin de la méthode a pour but d'attraper les exceptions comme décrit dans la méthode messageOut.
     * */
    private String messageIn() {
        try (BufferedReader r =
                new BufferedReader(
                    new InputStreamReader(socket.getInputStream(),
                            StandardCharsets.US_ASCII))){
            
            return r.readLine();
            
            } catch (IOException e) {
                throw new UncheckedIOException(e);
               }
        }
    
 /**
  * Les seules méthodes publiques offertes par cette classe 
  * sont les mises en œuvre concrètes des méthodes de l'interface Player.
  * 
  * 1) les éventuels arguments de la méthode sont sérialisés individuellement au moyen des serdes écrits à l'étape précédente
  * 2) le texte du message est construit en séparant au moyen du caractère d'espacement les éléments suivants, dans l'ordre :
  *     MessageId, les arguments dans le meme ordre que la méthode les accepte, un retour à la ligne
  * 3) le message est envoy sur le réseau via la méthode messageOut qui passe par la "prise"
  * 4) si la méthode retourne une valeur, alors une ligne est lue grace à la méthode messageIn
  *     puis elle est déserialisée et retournée
  * */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String argument1 = Serdes.serdePlayerId.serialize(ownId);
        String argument2 = Serdes.serdeListeOfString
                .serialize(List.of(playerNames.get(PlayerId.PLAYER_1), 
                        playerNames.get(PlayerId.PLAYER_2)));
        
        String output = MessageId.INIT_PLAYERS.name() + " " + argument1 + " " + argument2;
        
        this.messageOut(output);
        //est ce que je doit remettre un retour à la ligne si je le fait dans la méthode messageOut?
    }

    @Override
    public void receiveInfo(String info) {
        String argument1 = Serdes.serdeString.serialize(info);
        
        String output = MessageId.RECEIVE_INFO.name() + " " + argument1;
        
        this.messageOut(output);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String argument1 = Serdes.serdePublicGameState.serialize(newState);
        String argument2 = Serdes.serdePlayerState.serialize(ownState);
        
        String output = MessageId.UPDATE_STATE.name() + " " + argument1 + " " + argument2;
        
        this.messageOut(output);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String argument1 = Serdes.serdeSortedBagOfTicket.serialize(tickets);
        
        String output = MessageId.SET_INITIAL_TICKETS.name() + " " + argument1;
        
        this.messageOut(output);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {  
        this.messageOut(MessageId.CHOOSE_INITIAL_TICKETS.name());
        
        String input = this.messageIn();
        
        return Serdes.serdeSortedBagOfTicket.deserialize(input);
    }

    @Override
    public TurnKind nextTurn() {
        this.messageOut(MessageId.NEXT_TURN.name());
        
        String input = this.messageIn();
        
        return Serdes.serdeTurnKind.deserialize(input);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String argument1 = Serdes.serdeSortedBagOfTicket.serialize(options);
        String output = MessageId.CHOOSE_TICKETS.name() + " " + argument1;
        
        this.messageOut(output);
        
        String input = this.messageIn();
        
        return Serdes.serdeSortedBagOfTicket.deserialize(input);
    }

    @Override
    public int drawSlot() {
        this.messageOut(MessageId.DRAW_SLOT.name());
        
        String input = this.messageIn();
        
        return Serdes.serdeInt.deserialize(input);
    }

    @Override
    public Route claimedRoute() {
        this.messageOut(MessageId.ROUTE.name()); //est ce que ROUTE correspond à claimedRoute?

        String input = this.messageIn();
        
        return Serdes.serdeRoute.deserialize(input);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        this.messageOut(MessageId.CARDS.name()); //est ce que CARDS correspond au initialClaimCards?
        
        String input = this.messageIn();
        
        return Serdes.serdeSortedBagOfCard.deserialize(input);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(
            List<SortedBag<Card>> options) {
        String argument1 = Serdes.serdeListeOfSortedBagOfCard.serialize(options);
        
        String output = MessageId.CHOOSE_ADDITIONAL_CARDS.name() + " " + argument1;
        this.messageOut(output);
        
        String input = this.messageIn();
        
        return Serdes.serdeSortedBagOfCard.deserialize(input);
    }
    
}
