package ch.epfl.tchu.game;

import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

/**
 * Classe Game représente une partie de tCHu. Elle n'offre qu'une seule méthode publique et statique.
 * @author Thösam Norlha-Tsang (330163)
 * @author Aymeric de chillaz (326617)
 */
public final class Game {

//    private final Map<PlayerId, Player> players;
//    private final Map<PlayerId, String> playerNames;
//    private final SortedBag<Ticket> tickets;
//    private final Random rng;
//
//    private Game(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
//        this.players = players;
//        this.playerNames = playerNames;
//        this.tickets = tickets;
//        this.rng = rng;
//    }


    /**
     * fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table playerNames ;
     * les billets disponibles pour cette partie sont ceux de tickets, et le générateur aléatoire rng est utilisé
     * pour créer l'état initial du jeu et pour mélanger les cartes de la défausse pour en faire une nouvelle pioche quand cela est nécessaire
     * @param players
     * @param playerNames
     * @param tickets
     * @param rng
     * @throws IllegalArgumentException si l'une des deux tables associatives a une taille différente de 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, 
            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument((players.size()==2)&&(playerNames.size()==2));

        Info player1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        Info player2 = new Info(playerNames.get(PlayerId.PLAYER_2));

        Map<PlayerId, Info> infoMap = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);
        // Avant le début de la partie

        //comment avoir accès au joueur courant? PublicPlayerState
        //Est ce qu il faut définir les méthodes de Player?

        /**
         * communiquer sa propre identité, et le nom de chaque joueur - le sien inclus
         */
        players.forEach((c,v) -> v.initPlayers(c, playerNames));    // à vérifier

        /**
         * permet de choisir un joueur au hasard, communiquer l'info et d initialiser le GameState
         */
        GameState gameState = GameState.initial(tickets, rng);
        
        players.forEach((c,v) -> {
            v.receiveInfo(infoMap.get(gameState.currentPlayerId()).willPlayFirst());

        });

        /**
         *  Pour chaque joueur, communiquer les billets qu'il reçoit initialement
         */
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            c.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));    // les deux joueurs pourront donc consulter leur choix en parallèle, et même utiliser l'interface graphique
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        // ou faut-il juste mettre "tickets"
        //est ce qu il faut donner les 5 billets du haut de tickets et updater gameState -> OUI

        /**
         *  Pour chaque joueur doit être appelée pour savoir quels billets chaque joueur a décidé de garder
         */

        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            c.getValue().updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));  // faut-il aussi informer de l'état du joueur adverse ???
            c.getValue().updateState(gameState, gameState.playerState(PlayerId.PLAYER_2));
        }
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            SortedBag<Ticket> chosenTickets = c.getValue().chooseInitialTickets();  // ?? ligne à supprimer ?? à voir
            gameState.withInitiallyChosenTickets(c.getKey(), chosenTickets);  // faut-il mettre ceci ici ? OUI
            c.getValue().updateState(gameState, gameState.playerState(c.getKey())); // gameState ? faut-il le transformer en publicGameState ? NON, ça joue comme ça
        }

        /**
         *  Communiquer à chacun le nombre de billets gardés par chaque joueur
         */
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){ // après leur choix, on les informe -> équitable
            c.getValue().receiveInfo(player1.keptTickets(gameState.playerState(PlayerId.PLAYER_1).ticketCount())); // joueur 1 reçoit l'info de l'adversaire seulement ou de lui-même aussi ? Oui !
            c.getValue().receiveInfo(player2.keptTickets(gameState.playerState(PlayerId.PLAYER_2).ticketCount())); // c'est bien chooseInitialTickets() de la classe Player qu'il faut appeler ? Non !

        }


        // La partie commence, à tour de rôle

        while(!gameState.lastTurnBegins()){ // -> break, à la fin d'un tour + si le joueur actuel = lastPlayer

            Player currPlayerInterf = players.get(gameState.currentPlayerId());  // lisibilité
//            PlayerState currPlayerStat = gameState.currentPlayerState();  // va être modifié au cours ...
            Info currInf = infoMap.get(gameState.currentPlayerId());

            players.forEach((c, v) -> {
                v.receiveInfo(currInf.canPlay());

                v.updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
                v.updateState(gameState, gameState.playerState(PlayerId.PLAYER_2)); // Aussi celui du deuxième joueur ??? ou chacun reçoit les infos de sa propre main et pas de son adversaire ?
//                v.updateState(gameState, gameState.playerState(c));
            });

            /**
             *  Savoir quelle action le joueur courant désire effectuer parmi les trois possibles
             */

            Player.TurnKind turnKind = currPlayerInterf.nextTurn();

            if(turnKind == Player.TurnKind.DRAW_TICKETS) {

                SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);

                players.forEach((c, v) -> {
                    v.receiveInfo(currInf.drewTickets(Constants.IN_GAME_TICKETS_COUNT));   // Avant qu'il choisisse
                });

                SortedBag<Ticket> chosenTickets = currPlayerInterf.chooseTickets(drawnTickets);    // vérifier si c'est bien cette constante-là : OUI

                gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets); // playerState et tickets changent

                players.forEach((c, v) -> {
                    v.receiveInfo(currInf.keptTickets(chosenTickets.size()));
                });

            }else if(turnKind == Player.TurnKind.DRAW_CARDS){

                for(int i = 0; i < 2; i++){ // tire deux fois
                    int slot = currPlayerInterf.drawSlot();
                    if(slot == -1){    // deck slot
                        players.forEach((c, v) -> {
                            v.receiveInfo(currInf.drewBlindCard());
                        });
                        gameState = gameState.withBlindlyDrawnCard();
                    }else{  // face up cards -> 0-4
                        players.forEach((c, v) -> {
                            v.receiveInfo(currInf.drewVisibleCard(gameState.cardState().faceUpCard(slot)));    // what about discards ???
                        });
                        gameState = gameState.withDrawnFaceUpCard(slot);
                    }

                    if(i == 0){ // entre le premier et le deuxième tirage
                        players.forEach((c, v) -> {
                            v.updateState(gameState, gameState.currentPlayerState());    // aussi celui de l'adversaire ?
//                            v.updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
//                            v.updateState(gameState, gameState.playerState(PlayerId.PLAYER_2));
                        });
                    }
                }

                // gameState
                // receive info
                // update state
            }else{
                Route route = currPlayerInterf.claimedRoute();
                SortedBag<Card> cards = currPlayerInterf.initialClaimCards();
                if(route.level() == Route.Level.UNDERGROUND && ){ // que les trois cartes du sommet de la pioche impliquent l'utilisation d'au moins une carte additionnelle, et que le joueur courant a dans sa main les cartes additionnelles nécessaires
                    currPlayerInterf.chooseAdditionalCards(gameState.currentPlayerState().possibleAdditionalCards());   // je te laisse voir ça
                   players.forEach((c, v) -> { // if a visible card
                        v.receiveInfo(currInf.attemptsTunnelClaim(route, ));
                    });
                }else{
                    players.forEach((c, v) -> { // if a visible card
                        v.receiveInfo(currInf.claimedRoute(route, ));
                    });
                }
//                gameState. ???? devons-nous changer ceci ?
            }
        }

//       //il faudrait savoir sur quoi appeler keptTickets
//        players.forEach((c,v) ->{v.receiveInfo(keptTickets(chooseInitialTickets()));});
//
//        while(!gameState.lastTurnBegins()) {
//            //comment faire pour switcher entre les deux joueurs
//            TurnKind turnKind = gameState.currentPlayerId().nextTurn();
//            if(turnKind == Player.DRAW_TICKETS) {
//                //currentPlayer.chooseTickets()
//            }
//            else if(turnKind == Player.DRAW_CARDS) {}
//            else {//j émet l'hypothèse que le joueur ne peux pas séléctionner une autre valeur
//
//            }
//        }
//        //dont forget the two last turns
    }


    /**
     * permettan d'envoyer une information à tous les joueurs, en appelant la méthode receiveInfo de chacun d'eux
     */
//    private void infoToAll(String info) {
//        players.forEach((c,v) -> v.receiveInfo(info));
//    }

    /**
     * permettant d'informer tous les joueurs d'un changement d'état, en appelant la méthode updateState de chacun d'eux
     */
//    private void stateChangeToAll(String stateChange) {
//        players.forEach((c,v) -> v.receiveInfo(stateChange));
//    }
}

// GameState ??? Comment on fait pour changer ?
// UpdateState ??? Comment on fait pour changer
