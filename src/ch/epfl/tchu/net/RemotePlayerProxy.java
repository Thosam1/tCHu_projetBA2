package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
     * @param argument1 : la chaine de caractère correspondant au premier argument de la classe (peut etre null)
     * @param argument2 : la chaine de caractère correspondant au deuxieme argument de la classe (peut aussi etre null)
     * */
    private void messageOut(String messageId, String argument1, String argument2) {
        ArrayList<String> liste = new ArrayList<>();
        liste.add(messageId);
        liste.add(argument1);   //TODO je pense que si argument2 vaut null par exemple, il y aura une nullpointer exception
        liste.add(argument2);
        
        //créé un stream à partir des trois String, retire les valeurs null et les join en mettant un espace au mileu
        String string = liste.stream().filter(value -> value != null)
                         .collect(Collectors.joining(" "));
        
        try (BufferedWriter w =
                new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(),
                               StandardCharsets.US_ASCII))) {
            
            //rajoute le string avec un retour à la ligne
            w.write(string + '\n');
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
  * 3) le message est envoyé sur le réseau via la méthode messageOut qui passe par la "prise"
  * 4) si la méthode retourne une valeur, alors une ligne est lue grace à la méthode messageIn
  *     puis elle est déserialisée et retournée
  * */
    
    
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String argument1 = Serdes.serdePlayerId.serialize(ownId);
        String argument2 = Serdes.serdeListeOfString
                .serialize(List.of(playerNames.get(PlayerId.PLAYER_1),  //TODO à revérifier comment on passe la map
                        playerNames.get(PlayerId.PLAYER_2)));
//        System.out.println("player1 Id " + playerNames.get(PlayerId.PLAYER_1));
            //TODO à revérifier comment on passe la map

        this.messageOut(MessageId.INIT_PLAYERS.name(), argument1, argument2);
    }

    @Override
    public void receiveInfo(String info) {
        String argument1 = Serdes.serdeString.serialize(info);
        
        this.messageOut(MessageId.RECEIVE_INFO.name(), argument1, null);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String argument1 = Serdes.serdePublicGameState.serialize(newState); //TODO @1360 vérifier lastPlayer est ""
        String argument2 = Serdes.serdePlayerState.serialize(ownState);
        
        this.messageOut(MessageId.UPDATE_STATE.name(), argument1, argument2);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String argument1 = Serdes.serdeSortedBagOfTicket.serialize(tickets);
        
        this.messageOut(MessageId.SET_INITIAL_TICKETS.name(), argument1, null);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {  
        this.messageOut(MessageId.CHOOSE_INITIAL_TICKETS.name(), null, null);
        return Serdes.serdeSortedBagOfTicket.deserialize(this.messageIn());
    }

    @Override
    public TurnKind nextTurn() {
        this.messageOut(MessageId.NEXT_TURN.name(), null, null);
        return Serdes.serdeTurnKind.deserialize(this.messageIn());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String argument1 = Serdes.serdeSortedBagOfTicket.serialize(options);
        
        this.messageOut(MessageId.CHOOSE_TICKETS.name(), argument1, null);
        return Serdes.serdeSortedBagOfTicket.deserialize(this.messageIn());
    }

    @Override
    public int drawSlot() {
        this.messageOut(MessageId.DRAW_SLOT.name(), null, null);
        return Serdes.serdeInt.deserialize(this.messageIn());
    }

    @Override
    public Route claimedRoute() {
        this.messageOut(MessageId.ROUTE.name(), null, null);
        return Serdes.serdeRoute.deserialize(this.messageIn());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        this.messageOut(MessageId.CARDS.name(), null, null);
        return Serdes.serdeSortedBagOfCard.deserialize(this.messageIn());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(
            List<SortedBag<Card>> options) {
        String argument1 = Serdes.serdeListeOfSortedBagOfCard.serialize(options);
        
        this.messageOut(MessageId.CHOOSE_ADDITIONAL_CARDS.name(), argument1, null);
        return Serdes.serdeSortedBagOfCard.deserialize(this.messageIn());
    }
    
}
