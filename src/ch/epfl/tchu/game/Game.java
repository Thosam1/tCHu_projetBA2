package ch.epfl.tchu.game;

import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

public final class Game {

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, 
            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument((players.size()==2)&&(playerNames.size()==2));
        Info player1 = new Info(PlayerId.PLAYER_1.toString());
        Info player2 = new Info(PlayerId.PLAYER_2.toString());
        //comment avoir accès au joueur courant? PublicPlayerState
        //Est ce qu il faut définir les méthodes de Player?
        
        players.forEach((c,v) -> v.initPlayers(c, playerNames));
        
        //permet de choisir un joueur au hasard et d initialiser le GameState
        GameState gameState = GameState.initial(tickets, rng);
        
        
        players.forEach((c,v) -> v.receiveInfo(.willPlayFirst()));
        
        //est ce qu il faut donner les 5 billets du haut de tickets et updater gameState
       ERROR players.forEach((c,v) ->v.setInitialTicketChoice(tickets));
        
       //il faudrait savoir sur quoi appeler keptTickets
        players.forEach((c,v) ->{v.receiveInfo(keptTickets(chooseInitialTickets()));});
        
        while(!gameState.lastTurnBegins()) {
            TurnKind turnKind = currentPlayer.nextTurn();
            if(turnKind == Player.DRAW_TICKETS) {
                //currentPlayer.chooseTickets()
            }
            else if(turnKind == Player.DRAW_CARDS) {}
            else {//j émet l'hypothèse que le joueur ne peux pas séléctionner une autre valeur
                
            }
        }
        //dont forget the two last turns
    }
    
}
