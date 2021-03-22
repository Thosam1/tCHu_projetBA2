package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

public final class Game {

    private final Map<PlayerId, Player> players;
    private final Map<PlayerId, String> playerNames;
    private final SortedBag<Ticket> tickets;
    private final Random rng;

    private Game(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        this.players = players;
        this.playerNames = playerNames;
        this.tickets = tickets;
        this.rng = rng;
    }

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, 
            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument((players.size()==2)&&(playerNames.size()==2));


        // Avant le début de la partie  -   -   -

        players.forEach((c,v) -> v.initPlayers(c, playerNames));

        //permet de choisir un joueur au hasard et d initialiser le GameState
        GameState gameState = GameState.initial(tickets, rng);

        //Je ne sais pas quelle instance de info il faut passer puisqu on a pas accès au playerId
        Info player = new Info()
        players.forEach((c,v) -> v.receiveInfo(player.willPlayFirst()));

        //est ce qu il faut donner les 5 billets du haut de tickets
        ERROR players.forEach((c,v) ->v.setInitialTicketChoice(tickets));

        //il faudrait savoir sur quoi appeler keptTickets
        players.forEach((c,v) ->{v.receiveInfo(keptTickets(chooseInitialTickets()));});

        while(!gameState.lastTurnBegins()) {}
        //dont forget the two last turns



        // La partie commence - jusqu'à la fin  -   -   -











    }

    /**
     * permettan d'envoyer une information à tous les joueurs, en appelant la méthode receiveInfo de chacun d'eux
     */
    private void infoToAll(String info) {
        players.forEach((c,v) -> v.receiveInfo(info));
    }

    /**
     * permettant d'informer tous les joueurs d'un changement d'état, en appelant la méthode updateState de chacun d'eux
     */
    private void stateChangeToAll(String stateChange) {
        players.forEach((c,v) -> v.receiveInfo(stateChange));
    }


}
