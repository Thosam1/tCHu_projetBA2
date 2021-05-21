package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * RemotePlayerClient représente un client de joueur distant
 * 
 * @author Thösam Norlha-Tsang (330163)
 */
public final class RemotePlayerClient {
    final private Player player;
    final private String hostName; // name of the client server eg "localhost"
    final private int port;

    /**
     * Constructeur de la classe RemotePlayerClient
     * 
     * @param player
     *            le joueur (de type Player) auquel elle doit fournir un accès
     *            distant
     * @param name
     *            le nom (de type String)
     * @param port
     *            le port (de type int) à utiliser pour se connecter au
     *            mandataire
     */
    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.hostName = name;
        this.port = port;
    }

    /**
     * Cette méthode effectue une boucle durant laquelle elle : attend un
     * message en provenance du mandataire, le découpe en utilisant le caractère
     * d'espacement comme séparateur, détermine le type du message en fonction
     * de la première chaîne résultant du découpage, en fonction de ce type de
     * message, désérialise les arguments, appelle la méthode correspondante du
     * joueur si cette méthode retourne un résultat, le sérialise pour le
     * renvoyer au mandataire en réponse
     */
    public void run() {
        try (Socket s = new Socket(hostName, port);
                BufferedReader r = new BufferedReader(new InputStreamReader(
                        s.getInputStream(), StandardCharsets.US_ASCII));
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                        s.getOutputStream(), StandardCharsets.US_ASCII))) {
            String line = r.readLine();

            while (line != null) {
                String[] textSplit = line.split(Pattern.quote(" "), -1);
                MessageId message = MessageId.valueOf(textSplit[0]);
                String arg1 = (textSplit.length >= 2) ? textSplit[1] : null;
                String arg2 = (textSplit.length >= 3) ? textSplit[2] : null;
                String encoded = "";

                switch (message) {
                case INIT_PLAYERS:
                    PlayerId ownId = Serdes.serdePlayerId.deserialize(arg1);
                    List<String> names = Serdes.serdeListeOfString
                            .deserialize(arg2);
                    Map<PlayerId, String> playerNames = Map.of(
                            PlayerId.PLAYER_1, names.get(0), PlayerId.PLAYER_2,
                            names.get(1)); // logically first name is the id of
                                           // player number one, and the second
                                           // name is the player number two.
                    player.initPlayers(ownId, playerNames);
                    break;
                case RECEIVE_INFO:
                    String info = Serdes.serdeString.deserialize(arg1);
                    player.receiveInfo(info);
                    break;
                case UPDATE_STATE:
                    PublicGameState publicGameState = Serdes.serdePublicGameState
                            .deserialize(arg1);
                    PlayerState playerState = Serdes.serdePlayerState
                            .deserialize(arg2);
                    player.updateState(publicGameState, playerState);
                    break;
                case SET_INITIAL_TICKETS:
                    SortedBag<Ticket> initialTickets = Serdes.serdeSortedBagOfTicket
                            .deserialize(arg1);
                    player.setInitialTicketChoice(initialTickets);
                    break;
                case CHOOSE_INITIAL_TICKETS:
                    SortedBag<Ticket> initChosenTickets = player
                            .chooseInitialTickets();
                    encoded = Serdes.serdeSortedBagOfTicket
                            .serialize(initChosenTickets);
                    writeFlush(w, encoded);
                    break;
                case NEXT_TURN:
                    Player.TurnKind turn = player.nextTurn();
                    encoded = Serdes.serdeTurnKind.serialize(turn);
                    writeFlush(w, encoded);
                    break;
                case CHOOSE_TICKETS:
                    SortedBag<Ticket> options = Serdes.serdeSortedBagOfTicket
                            .deserialize(arg1);
                    SortedBag<Ticket> chosenTickets = player
                            .chooseTickets(options);
                    encoded = Serdes.serdeSortedBagOfTicket
                            .serialize(chosenTickets);
                    writeFlush(w, encoded);
                    break;
                case DRAW_SLOT:
                    int slot = player.drawSlot();
                    encoded = Serdes.serdeInteger.serialize(slot);
                    writeFlush(w, encoded);
                    break;
                case ROUTE:
                    Route claimed = player.claimedRoute();
                    encoded = Serdes.serdeRoute.serialize(claimed);
                    writeFlush(w, encoded);
                    break;
                case CARDS:
                    SortedBag<Card> initialCards = player.initialClaimCards();
                    encoded = Serdes.serdeSortedBagOfCard
                            .serialize(initialCards);
                    writeFlush(w, encoded);
                    break;
                case CHOOSE_ADDITIONAL_CARDS:
                    List<SortedBag<Card>> optionsCards = Serdes.serdeListeOfSortedBagOfCard
                            .deserialize(arg1);
                    SortedBag<Card> chosenAdditional = player
                            .chooseAdditionalCards(optionsCards);
                    encoded = Serdes.serdeSortedBagOfCard
                            .serialize(chosenAdditional);
                    writeFlush(w, encoded);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unknown message received");
                }
                line = r.readLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeFlush(BufferedWriter w, String s) throws IOException {
        w.write(s + '\n');
        w.flush();
    }

}