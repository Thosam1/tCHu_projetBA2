package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 *  Classe contenant le programme principal du client tCHu
 *  @author Thösam Norlha-Tsang (330163)
 */
public class ClientMain extends Application {

    /**
     *      * Contient 2 arguments optionnels :
     *      *  1) le nom de l'hôte sur lequel le serveur s'exécute—par défaut localhost    todo ou ces arguments sont passés à main au lieu de start ?
     *      *  2) le numéro de port sur lequel le serveur écoute—par défaut 5108
     * @param args
     */
    public static void main(String[] args) { launch(args); }

    /**
     * Méthode qui se charge de démarrer le client :
     * 1) analysant les arguments passés au programme afin de déterminer le nom de l'hôte et le numéro de port du serveur,
     * 2) créant un client distant—une instance de RemotePlayerClient — associé à un joueur graphique—une instance de GraphicalPlayerAdapter,
     * 3) démarrant le fil gérant l'accès au réseau, qui ne fait rien d'autre qu'exécuter la méthode run du client créé précédemment.
     *
     * Contient 2 arguments optionnels :
     *  1) le nom de l'hôte sur lequel le serveur s'exécute—par défaut localhost    todo avec start ou avec main
     *  2) le numéro de port sur lequel le serveur écoute—par défaut 5108
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            // 1)
            List<String> argList = getParameters().getRaw();    // ici index 0 et index 1 ?? avec le run edit config ?
            String hostName = (argList.size() == 2) ? argList.get(0) : "localhost";
            String portNumber = (argList.size() == 2) ? argList.get(1) : "5108";
            System.out.println(portNumber); // TODO EFFACER
            // 2)
            GraphicalPlayerAdapter graphicalPlayerAdapter = new GraphicalPlayerAdapter();//GraphicalPlayerAdapter.initPlayers();
            System.out.println("creating the client server");
            RemotePlayerClient client = new RemotePlayerClient(graphicalPlayerAdapter, hostName, Integer.parseInt(portNumber));
            System.out.println("client créé");
            // 3)
            new Thread(() -> client.run()).start();    // TODO EFFACER
            System.out.println("client running");    // TODO EFFACER

        } catch(Exception e){
            System.out.println("ERROR OCCURRED");
        }
    }
}
