package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
 * 
 * @author Aymeric de chillaz (326617)
 */
public final class RemotePlayerProxy implements Player {
    final BufferedWriter w;
    final BufferedReader r;

    public RemotePlayerProxy(Socket socket) throws IOException {
        w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
                StandardCharsets.US_ASCII));
        r = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                StandardCharsets.US_ASCII));
    }

    /**
     * méthode privée qui permet d'envoyer un message étant donné son identité
     * et les chaines de caractères correspondants à la sérialisation de ses
     * arguments
     *
     * catch à la fin de la méthode a pour but d'attraper les exceptions de type
     * IOException et de les lever à nouveau, en quelque sorte, sous forme
     * d'exceptions équivalentes mais de type UncheckedIOException. La
     * différence entre les deux types d'exception est que le premier est un
     * type d'exception checked, le second pas.
     *
     * le premier paramètre ne sera jamais nul, il correspond au MessageId de la
     * méthode qui appelle cette méthode les suivants (il n'y en a pas tout le
     * temps) sont les paramètres de la méthode qui appelle messageOut
     */
    private void messageOut(String... args) {
        List<String> list = new ArrayList<>();
        for (String string : args) {
            list.add(string);
        }
        list.add("\n");

        try {
            w.write(String.join(" ", list));
            w.flush();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * méthode privée qui permet de recevoir un message et de le retourner sous
     * la forme de String
     * 
     * catch à la fin de la méthode a pour but d'attraper les exceptions de type
     * IOException et de les lever à nouveau, en quelque sorte, sous forme
     * d'exceptions équivalentes mais de type UncheckedIOException. La
     * différence entre les deux types d'exception est que le premier est un
     * type d'exception checked, le second pas.
     */
    private String messageIn() {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Les seules méthodes publiques offertes par cette classe sont les mises en
     * œuvre concrètes des méthodes de l'interface Player.
     *
     * 1) les éventuels arguments de la méthode sont sérialisés individuellement
     * au moyen des serdes écrits à l'étape précédente 2) le texte du message
     * est construit en séparant au moyen du caractère d'espacement les éléments
     * suivants, dans l'ordre: MessageId, les arguments dans le meme ordre que
     * la méthode les accepte, un retour à la ligne 3) le message est envoyé sur
     * le réseau via la méthode messageOut qui passe par la "prise" 4) si la
     * méthode retourne une valeur, alors une ligne est lue grace à la méthode
     * messageIn puis elle est déserialisée et retournée
     */

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String argument1 = Serdes.SERDE_PLAYER_ID.serialize(ownId);
        String argument2 = Serdes.SERDE_LIST_OF_STRING
                .serialize(List.of(playerNames.get(PlayerId.PLAYER_1),
                        playerNames.get(PlayerId.PLAYER_2)));
        messageOut(MessageId.INIT_PLAYERS.name(), argument1, argument2);
    }

    @Override
    public void receiveInfo(String info) {
        String argument1 = Serdes.SERDE_STRING.serialize(info);
        messageOut(MessageId.RECEIVE_INFO.name(), argument1);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String argument1 = Serdes.SERDE_PUBLIC_GAME_STATE.serialize(newState);
        String argument2 = Serdes.SERDE_PLAYER_STATE.serialize(ownState);
        messageOut(MessageId.UPDATE_STATE.name(), argument1, argument2);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String argument1 = Serdes.SERDE_SORTED_BAG_OF_TICKET.serialize(tickets);
        messageOut(MessageId.SET_INITIAL_TICKETS.name(), argument1);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        messageOut(MessageId.CHOOSE_INITIAL_TICKETS.name());
        return Serdes.SERDE_SORTED_BAG_OF_TICKET.deserialize(messageIn());
    }

    @Override
    public TurnKind nextTurn() {
        messageOut(MessageId.NEXT_TURN.name());
        return Serdes.SERDE_TURN_KIND.deserialize(messageIn());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String argument1 = Serdes.SERDE_SORTED_BAG_OF_TICKET.serialize(options);
        messageOut(MessageId.CHOOSE_TICKETS.name(), argument1);
        return Serdes.SERDE_SORTED_BAG_OF_TICKET.deserialize(messageIn());
    }

    @Override
    public int drawSlot() {
        messageOut(MessageId.DRAW_SLOT.name());
        return Serdes.SERDE_INTEGER.deserialize(messageIn());
    }

    @Override
    public Route claimedRoute() {
        messageOut(MessageId.ROUTE.name());
        return Serdes.SERDE_ROUTE.deserialize(messageIn());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        messageOut(MessageId.CARDS.name());
        return Serdes.SERDE_SORTED_BAG_OF_CARD.deserialize(messageIn());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(
            List<SortedBag<Card>> options) {
        String argument1 = Serdes.SERDE_LIST_OF_SORTED_BAG_OF_CARD
                .serialize(options);
        messageOut(MessageId.CHOOSE_ADDITIONAL_CARDS.name(), argument1);
        return Serdes.SERDE_SORTED_BAG_OF_CARD.deserialize(messageIn());
    }

}