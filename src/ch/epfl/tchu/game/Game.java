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
            if ((gameState.currentPlayerId() == PlayerId.PLAYER_1)) {
                v.receiveInfo(player1.willPlayFirst());
            } else {
                v.receiveInfo(player2.willPlayFirst());
            }
        });

        /**
         *  Pour chaque joueur, communiquer les billets qu'il reçoit initialement
         */
        players.forEach((c, v) -> {
            if(c == PlayerId.PLAYER_1)
                v.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            else
                v.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT * 2).difference(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT)));


//            gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

            v.receiveInfo(player1.drewTickets(Constants.INITIAL_TICKETS_COUNT));
            v.receiveInfo(player2.drewTickets(Constants.INITIAL_TICKETS_COUNT));

        });   // ou faut-il juste mettre "tickets"
        //est ce qu il faut donner les 5 billets du haut de tickets et updater gameState

        /**
         *  Pour chaque joueur doit être appelée pour savoir quels billets chaque joueur a décidé de garder
         */
        players.forEach((c, v) -> {
            v.chooseInitialTickets();
            gameState.withInitiallyChosenTickets(c, v.chooseInitialTickets());  // faut-il mettre ceci ici ?
        });


        /**
         *  Communiquer à chacun le nombre de billets gardés par chaque joueur
         */
        players.forEach((c, v) -> { // après leur choix, on les informe
                                                                                        // joueur 1 reçoit l'info de l'adversaire seulement ou de lui-même aussi ?
                                                                                        // c'est bien chooseInitialTickets() de la classe Player qu'il faut appeler ?
            v.receiveInfo(player1.keptTickets(players.get(PlayerId.PLAYER_1).chooseInitialTickets().size()));
            v.receiveInfo(player2.keptTickets(players.get(PlayerId.PLAYER_2).chooseInitialTickets().size()));
        });

        // La partie commence, à tour de rôle

        while(!gameState.lastTurnBegins()){

            Player currPlayerInterf = players.get(gameState.currentPlayerId());  // lisibilité
            PlayerState currPlayerStat = gameState.currentPlayerState();
            Info currInf;
            if(gameState.currentPlayerId() == PlayerId.PLAYER_1){
                currInf = player1;
            }else{ currInf = player2;}

            /**
             *  Savoir quelle action le joueur courant désire effectuer parmi les trois possibles
             */
            players.forEach((c, v) -> {
                v.receiveInfo(currInf.canPlay());
            });

            Player.TurnKind turnKind = currPlayerInterf.nextTurn();
            if(turnKind == Player.TurnKind.DRAW_TICKETS) {
                currPlayerInterf.chooseTickets(tickets);
                players.forEach((c, v) -> {
                    v.receiveInfo(currInf.drewTickets(Constants.IN_GAME_TICKETS_COUNT));   // comment sait-on le nbre tiré
                });

                players.forEach((c, v) -> {
                    v.receiveInfo(currInf.keptTickets(currPlayerInterf.chooseTickets(tickets).size()));
                });
                gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), currPlayerInterf.chooseTickets(tickets));

            }else if(turnKind == Player.TurnKind.DRAW_CARDS){

                int slot1 = currPlayerInterf.drawSlot();
                players.forEach((c, v) -> { // if a visible card, what about discards ???
                    v.receiveInfo(currInf.drewVisibleCard());
                });
                players.forEach((c, v) -> { // if a blind card
                    v.receiveInfo(currInf.drewBlindCard());
                });
                int slot2 = currPlayerInterf.drawSlot();     // faut-il faire un update dans gameState ?? ou communiquer ceci à l'autre joueur ?

                // qu'en est-il de gameState.WithDrawnFaceUpCard() ???
            }else{
                Route route = currPlayerInterf.claimedRoute();
                SortedBag<Card> cards = currPlayerInterf.initialClaimCards();
                if(route.level() == Route.Level.UNDERGROUND && ){ // que les trois cartes du sommet de la pioche impliquent l'utilisation d'au moins une carte additionnelle, et que le joueur courant a dans sa main les cartes additionnelles nécessaires
                    currPlayerInterf.chooseAdditionalCards(currPlayerStat.possibleAdditionalCards());   // je te laisse voir ça
0                   players.forEach((c, v) -> { // if a visible card
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
