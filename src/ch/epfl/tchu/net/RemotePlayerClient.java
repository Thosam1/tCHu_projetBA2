package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * RemotePlayerClient représente un client de joueur distant
 * @author Thösam Norlha-Tsang (330163)
 */
public final class RemotePlayerClient {
    final Player player;
    final String name;  //name of the client server eg "localhost"
    final int port;

//    final Socket s;

    /**
     *  Constructeur de la classe RemotePlayerClient
     * @param player  le joueur (de type Player) auquel elle doit fournir un accès distant
     * @param name  le nom (de type String)
     * @param port  le port (de type int) à utiliser pour se connecter au mandataire
     */
    public RemotePlayerClient(Player player, String name, int port){
        this.player = player;
        this.name = name;
        this.port = port;
//        s = new Socket("localhost_" + name, port);
    }

    /**
     *  Cette méthode effectue une boucle durant laquelle elle :
     *  attend un message en provenance du mandataire,
     *  le découpe en utilisant le caractère d'espacement comme séparateur,
     *  détermine le type du message en fonction de la première chaîne résultant du découpage,
     *  en fonction de ce type de message, désérialise les arguments, appelle la méthode correspondante du joueur
     *  si cette méthode retourne un résultat, le sérialise pour le renvoyer au mandataire en réponse
     */
    public void run(){

        while(true) { //TODO bonne façon ?  // loop quasi infinie   -> while après bufferedReader || Soit while(true) et if(line == null){break;} Soit on met la while avec la condition directement dans le try

            try (Socket s = new Socket(name, port);
                 BufferedReader r =
                         new BufferedReader(
                                 new InputStreamReader(s.getInputStream(),
                                         StandardCharsets.US_ASCII));
                 BufferedWriter w =
                         new BufferedWriter(
                                 new OutputStreamWriter(s.getOutputStream(),
                                         StandardCharsets.US_ASCII))) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }

                String[] textSplit = line.split(Pattern.quote(" "), -1);//TODO should we assume it won't be longer than a line ??? Yes
                MessageId message = MessageId.valueOf(textSplit[0]);
                String arg1 = (textSplit.length >= 2) ? textSplit[1] : null;
                String arg2 = (textSplit.length >= 3) ? textSplit[2] : null;    //ou les remettre un par un dans chaque cases -
//                System.out.println("message Id Updtate state example " + MessageId.valueOf("UPDATE_STATE"));
                System.out.println("messageId arg0 : " + message);
                System.out.println("arg 1 : " + arg1);
                System.out.println("arg 2 : " + arg2);

                switch (message) {
                    case INIT_PLAYERS:
                        PlayerId ownId = Serdes.serdePlayerId.deserialize(arg1);
                        List<String> names = Serdes.serdeListeOfString.deserialize(arg2);
                        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, names.get(0), PlayerId.PLAYER_2, names.get(1));   // logically first name is the id of player number one, and the second name is the player number two.
                        player.initPlayers(ownId, playerNames);
                    case RECEIVE_INFO:
                        String info = Serdes.serdeString.deserialize(arg1);
                        player.receiveInfo(info);
                    case UPDATE_STATE:
                        PublicGameState publicGameState = Serdes.serdePublicGameState.deserialize(arg1);
                        PlayerState playerState = Serdes.serdePlayerState.deserialize(arg2);
                        player.updateState(publicGameState, playerState);
                    case SET_INITIAL_TICKETS:
                        SortedBag<Ticket> initialTickets = Serdes.serdeSortedBagOfTicket.deserialize(arg1);
                        player.setInitialTicketChoice(initialTickets);
                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> initChosenTickets = player.chooseInitialTickets();
                        String encoded = Serdes.serdeSortedBagOfTicket.serialize(initChosenTickets);
                        w.write(encoded + '\n');    // TODO retour à la ligne nécessaire ? Oui
                        w.flush();
                    case NEXT_TURN:
                        Player.TurnKind turn = player.nextTurn();
                        String encoded1 = Serdes.serdeTurnKind.serialize(turn);
                        w.write(encoded1 + '\n');
                        w.flush();
                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> options = Serdes.serdeSortedBagOfTicket.deserialize(arg1);
                        SortedBag<Ticket> chosenTickets = player.chooseTickets(options);
                        String encoded2 = Serdes.serdeSortedBagOfTicket.serialize(chosenTickets);
                        w.write(encoded2 + '\n');
                        w.flush();
                    case DRAW_SLOT:
                        int slot = player.drawSlot();
                        String encoded3 = Serdes.serdeInt.serialize(slot);
                        w.write(encoded3 + '\n');
                        w.flush();
                    case ROUTE:
                        Route claimed = player.claimedRoute();
                        String encoded4 = Serdes.serdeRoute.serialize(claimed);
                        w.write(encoded4 + '\n');
                        w.flush();
                    case CARDS:
                        SortedBag<Card> initialCards = player.initialClaimCards();
                        String encoded5 = Serdes.serdeSortedBagOfCard.serialize(initialCards);
                        w.write(encoded5 + '\n');
                        w.flush();
                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> optionsCards = Serdes.serdeListeOfSortedBagOfCard.deserialize(arg1);
                        SortedBag<Card> chosenAdditional = player.chooseAdditionalCards(optionsCards);
                        String encoded6 = Serdes.serdeSortedBagOfCard.serialize(chosenAdditional);
                        w.write(encoded6 + '\n');
                        w.flush();

                    default:
                        throw new IllegalArgumentException("Unknown message");
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
