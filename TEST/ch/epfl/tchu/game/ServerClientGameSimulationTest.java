package ch.epfl.tchu.game;
import ch.epfl.tchu.SmartBot;
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

public class ServerClientGameSimulationTest {

    public static final class TestServer {
        public static void main(String[] args) throws IOException {
            System.out.println("Starting server!");
            try (ServerSocket serverSocket = new ServerSocket(5108);        // Player1
                 Socket socket = serverSocket.accept()) {
                Player playerProxy = new RemotePlayerProxy(socket);
//                var playerNames = Map.of(PlayerId.PLAYER_1, "Thösam",
//                        PlayerId.PLAYER_2, "Aymeric");
//                playerProxy.initPlayers(PlayerId.PLAYER_1, playerNames);
            }
            try (ServerSocket serverSocket = new ServerSocket(5109);        //Player2
                 Socket socket = serverSocket.accept()) {
                Player playerProxy = new RemotePlayerProxy(socket);
//                var playerNames = Map.of(PlayerId.PLAYER_1, "Thösam",
//                        PlayerId.PLAYER_2, "Aymeric");
//                playerProxy.initPlayers(PlayerId.PLAYER_2, playerNames);
            }

            System.out.println("Server done!");
            System.out.println("Initializing the game  ...  ...");

            Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1, new SmartBot(1, ChMap.routes(), PlayerId.PLAYER_1, "Thösam"), PlayerId.PLAYER_2, new GameTest.TestPlayer(1, ChMap.routes(), PlayerId.PLAYER_2, "Aymeric"));
            Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "Thösam", PlayerId.PLAYER_2, "Aymeric");
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
            Random rng = new Random(1);

            System.out.println("Launching the game : ");
            Game.play(players, playerNames, tickets, rng);

        }
    }

    public static final class TestClient1 {
        public static void main(String[] args) {
            System.out.println("Starting client!");
            RemotePlayerClient playerClient =
                    new RemotePlayerClient(new TestPlayer(),    //TODO on devrait mettre la classe SmartBot à la place de TestPlayer() ???
                            "localhost",
                            5108);
            playerClient.run();
            System.out.println("Client done!");
        }
        private final static class TestPlayer implements Player {
            @Override
            public void initPlayers(PlayerId ownId,
                                    Map<PlayerId, String> names) {
                System.out.printf("ownId: %s\n", ownId);
                System.out.printf("playerNames: %s\n", names);
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
        public static final class TestClient2 {
            public static void main(String[] args) {
                System.out.println("Starting client!");
                RemotePlayerClient playerClient =
                        new RemotePlayerClient(new ServerClientGameSimulationTest.TestClient1.TestPlayer(),
                                "localhost",
                                5109);
                playerClient.run();
                System.out.println("Client done!");
            }
            private final static class TestPlayer implements Player {
                @Override
                public void initPlayers(PlayerId ownId,
                                        Map<PlayerId, String> names) {
                    System.out.printf("ownId: %s\n", ownId);
                    System.out.printf("playerNames: %s\n", names);
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

