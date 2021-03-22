package ch.epfl.tchu.game;

import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class Game {

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, 
            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument((players.size()==2)&&(playerNames.size()==2));
        
        
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
    }
}
