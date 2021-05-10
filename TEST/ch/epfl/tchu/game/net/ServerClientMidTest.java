package ch.epfl.tchu.game.net;

import ch.epfl.tchu.SmartBot;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServerClientMidTest {

    public static final class TestServer {

//        Game.play(players, playerNames, tickets, rng);

        public static void main(String[] args) throws IOException {
            System.out.println("Starting server!");
            Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "Thösam", PlayerId.PLAYER_2, "Aymeric");

            Player playerProxy1 = new SmartBot(1, ChMap.routes(), PlayerId.PLAYER_1, "Thösam");

            try (ServerSocket serverSocket = new ServerSocket(5108);
                 Socket socket = serverSocket.accept()) {
                 playerProxy1 = new RemotePlayerProxy(socket);
            }

            Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1, playerProxy1, PlayerId.PLAYER_2, new GameTest.TestPlayer(1, ChMap.routes(), PlayerId.PLAYER_2, "Aymeric"));
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
            Random rng = new Random(1);

            playerProxy1.initPlayers(PlayerId.PLAYER_1, playerNames);

            System.out.println("Server done!");

        }
    }

    public static final class TestClient {
        public static void main(String[] args) {
            System.out.println("Starting client!");
            RemotePlayerClient playerClient =
                    new RemotePlayerClient(new SmartBot(1, ChMap.routes(), PlayerId.PLAYER_1, "Thösam"),
                            "localhost",
                            5108);
            playerClient.run();
            System.out.println("Client done!");
        }
    }
    public static final class TestClient2 {
        public static void main(String[] args) {
            System.out.println("Starting client!");
            RemotePlayerClient playerClient =
                    new RemotePlayerClient(new SmartBot(1, ChMap.routes(), PlayerId.PLAYER_2, "Aymeric"),
                            "localhost",
                            5110);
            playerClient.run();
            System.out.println("Client done!");
        }
    }


}
