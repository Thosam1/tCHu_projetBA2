package ch.epfl.tchu.game;
import ch.epfl.tchu.SortedBag;

import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import ch.epfl.tchu.net.Serdes;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerClientTest {

    public static final class TestServer {
        public static void main(String[] args) throws IOException {
            System.out.println("Starting server!");
            try (ServerSocket serverSocket = new ServerSocket(5110);
                 Socket socket = serverSocket.accept()) {
                Player playerProxy = new RemotePlayerProxy(socket);
                var playerNames = Map.of(PlayerId.PLAYER_1, "Thösam",
                        PlayerId.PLAYER_2, "Aymeric");
                playerProxy.initPlayers(PlayerId.PLAYER_1, playerNames);
            }
            System.out.println("Server done!");
        }
    }

    public static final class TestClient {
        public static void main(String[] args) {
            System.out.println("Starting client!");
            RemotePlayerClient playerClient =
                    new RemotePlayerClient(new TestPlayer(),
                            "localhost",
                            5110);
            playerClient.run();
            System.out.println("Client done!");
        }

        //les noms ils sont bien en String avant de les encoder
        //les noms décodés sont décodés en leur références et pas en String

        private final static class TestPlayer implements Player {
            @Override
            public void initPlayers(PlayerId ownId,
                                    Map<PlayerId, String> names) {
                System.out.printf("ownId: %s\n", ownId);
                System.out.printf("playerNames: %s\n", names);
                System.out.println(" Teacher Tests Finished -  -  -  -  -  -");
            }

            @Override
            public void receiveInfo(String info) {

            }

            @Override
            public void updateState(PublicGameState newState, PlayerState ownState) {

            }

            @Override
            public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

            }

            @Override
            public SortedBag<Ticket> chooseInitialTickets() {
                return null;
            }

            @Override
            public TurnKind nextTurn() {
                return null;
            }

            @Override
            public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
                return null;
            }

            @Override
            public int drawSlot() {
                return 0;
            }

            @Override
            public Route claimedRoute() {
                return null;
            }

            @Override
            public SortedBag<Card> initialClaimCards() {
                return null;
            }

            @Override
            public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
                return null;
            }

            // … autres méthodes de Player
        }
    }

}
