package ch.epfl.tchu.gui;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe contenant le programme principal du client tCHu
 * 
 * @author Thösam Norlha-Tsang (330163)
 */

public class ServerMain extends Application {
    /**
     * Contient 2 arguments optionnels : 1) le nom du premier joueur
     *                                   2) le nom du second joueur
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Méthode qui se charge de démarrer le serveur : 1) analysant les arguments
     * passés au programme afin de déterminer les noms des deux joueurs, 2)
     * attendant une connexion de la part du client sur le port 5108, 3) créant
     * les deux joueurs, le premier étant un joueur graphique, le second un
     * mandataire du joueur distant qui se trouve sur le client, 4) démarrant le
     * fil d'exécution gérant la partie, qui ne fait rien d'autre qu'exécuter la
     * méthode play de Game.
     *
     * @param stage this is not used by the method
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        // 1)
        List<String> argList = getParameters().getRaw();
        String firstName = (argList.size() == 2) ? argList.get(0) : "Ada";
        String secondName = (argList.size() == 2) ? argList.get(1) : "Charles";

        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, firstName,
                PlayerId.PLAYER_2, secondName);

        // 2)
        Player secondPlayer;
        Socket socket;
        try (ServerSocket serverSocket = new ServerSocket(5108)) {
            socket = serverSocket.accept();
        }
        secondPlayer = new RemotePlayerProxy(socket);

        // 3)
        GraphicalPlayerAdapter firstGraphicalPlayerAdapter = new GraphicalPlayerAdapter();

        // 4)
        Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1,
                firstGraphicalPlayerAdapter, PlayerId.PLAYER_2, secondPlayer);
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        new Thread(() -> Game.play(players, playerNames, tickets, new Random()))
                .start();
    }
}
