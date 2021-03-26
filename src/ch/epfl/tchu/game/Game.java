package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
        int numberOfLastTurns = 0; //égal à 1 quand l'avant dernier joueur joue et 2 qand le dernier joue

        Info player1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        Info player2 = new Info(playerNames.get(PlayerId.PLAYER_2));

        Map<PlayerId, Info> infoMap = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);
        // Avant le début de la partie

        //comment avoir accès au joueur courant? PublicPlayerState

        /**
         * communiquer sa propre identité, et le nom de chaque joueur - le sien inclus
         */
        players.forEach((c,v) -> v.initPlayers(c, playerNames));   // à vérifier

        /**
         * permet de choisir un joueur au hasard, communiquer l'info et d initialiser le GameState
         */
        GameState gameState = GameState.initial(tickets, rng);

        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            c.getValue().receiveInfo(infoMap.get(gameState.currentPlayerId()).willPlayFirst());
        }

        /**
         *  Pour chaque joueur, communiquer les billets qu'il reçoit initialement
         */
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            c.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));    // les deux joueurs pourront donc consulter leur choix en parallèle, et même utiliser l'interface graphique
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }
            /*infoMap.forEach((playerId, playerInfo) ->
            v.receiveInfo(playerInfo.drewTickets(Constants.INITIAL_TICKETS_COUNT)));        // on est pas censé appeler ceci
            Il faut attendre que tous les joueurs aient fait leur choix comme dit
            à la deuxieme ligne sur quand receiveInfo doit etre appelé*/    // -> c'est fait plus bas

        /**
         *  Pour chaque joueur doit être appelée pour savoir quels billets chaque joueur a décidé de garder
         */
        /*players.forEach((c, v) -> {
            SortedBag<Tickets> chosenTickets = v.chooseInitialTickets();
            gameState.withInitiallyChosenTickets(c, chosenTickets);
        });*/
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            c.getValue().updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));  // faut-il aussi informer de l'état du joueur adverse ???
            c.getValue().updateState(gameState, gameState.playerState(PlayerId.PLAYER_2));
        }
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            SortedBag<Ticket> chosenTickets = c.getValue().chooseInitialTickets();  // pop up
            gameState.withInitiallyChosenTickets(c.getKey(), chosenTickets);  // faut-il mettre ceci ici ? OUI
            c.getValue().updateState(gameState, gameState.playerState(c.getKey())); // gameState ? faut-il le transformer en publicGameState ? NON, ça joue comme ça
        }


        /**
         *  Communiquer à chacun le nombre de billets gardés par chaque joueur
         */
        /*players.forEach((c, v) -> { // après leur choix, on les informe
        // joueur 1 reçoit l'info de l'adversaire seulement ou de lui-même aussi ? bonne question
        // c'est bien chooseInitialTickets() de la classe Player qu'il faut appeler ?
        // je dirai que oui
            v.receiveInfo(player1.keptTickets(players.get(PlayerId.PLAYER_1).chooseInitialTickets().size()));
            v.receiveInfo(player2.keptTickets(players.get(PlayerId.PLAYER_2).chooseInitialTickets().size()));
        });*/
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){ // après leur choix, on les informe -> équitable
            c.getValue().receiveInfo(player1.keptTickets(gameState.playerState(PlayerId.PLAYER_1).ticketCount())); // chaque joueur reçoit les infos des deux joueurs ?
            c.getValue().receiveInfo(player2.keptTickets(gameState.playerState(PlayerId.PLAYER_2).ticketCount())); // c'est bien chooseInitialTickets() de la classe Player qu'il faut appeler ? Non !

        }



        /**La partie commence*/
        while(numberOfLastTurns<=2){//le loop s'arrete quand les deux joueurs ont joué une dernière fois
            //currentPlayerInterface
            Player currPlayerInterf = players.get(gameState.currentPlayerId());  // lisibilité
            Info currInf = infoMap.get(gameState.currentPlayerId());

            for(Map.Entry<PlayerId, Player> c : players.entrySet()){
                c.getValue().receiveInfo(currInf.canPlay());

                c.getValue().updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
                c.getValue().updateState(gameState, gameState.playerState(PlayerId.PLAYER_2)); // Aussi celui du deuxième joueur ??? ou chacun reçoit les infos de sa propre main et pas de son adversaire ?
//                v.updateState(gameState, gameState.playerState(c));
            }

            /**
             *  Savoir quelle action le joueur courant désire effectuer parmi les trois possibles
             */

            Player.TurnKind turnKind = currPlayerInterf.nextTurn();


            if(turnKind == Player.TurnKind.DRAW_TICKETS) {
                //faut il verifier qu il reste des tickets
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
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int slot = currPlayerInterf.drawSlot();
                    //Constans.DECK_SLOT est égal à -1 et signigie que le joueur veut la carte du haut du deck
                    gameState = (slot == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(slot);

                    for(Map.Entry<PlayerId, Player> c : players.entrySet()){ // if a visible card, what about discards ??? Pouvons-nous piocher de la défausse ?
                        if ((slot == Constants.DECK_SLOT)) {
                            c.getValue().receiveInfo(currInf.drewBlindCard());
                        } else {
                            c.getValue().receiveInfo(currInf.drewVisibleCard(gameState.cardState().faceUpCard(slot)));
                        }
                    }
                    // faut il communiquer ceci à l'autre joueur ?
                    if(i == 0){ // entre le premier et le deuxième tirage
                        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
                            c.getValue().updateState(gameState, gameState.currentPlayerState());    // aussi celui de l'adversaire ? de quelle façon ?
//                            v.updateState(gameState, gameState.playerState(PlayerId.PLAYER_1));
//                            v.updateState(gameState, gameState.playerState(PlayerId.PLAYER_2));
                        }
                    }
                }
            }

            // gameState
            // receive info
            // update state

            else {  //comment vérifier que le joueur a assez de wagons
                /**la route que le player veut*/
                Route routeDésiré = currPlayerInterf.claimedRoute();
                SortedBag<Card> initialCards = currPlayerInterf.initialClaimCards();

                if (routeDésiré.level() == Route.Level.UNDERGROUND) {
                    SortedBag.Builder builder = new SortedBag.Builder<>();

                    for(int i = 0; i<Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        builder.add(gameState.topCard());
                        gameState = gameState.withoutTopCard(); //retourne un nouveau gameState sans la carte du haut
                        }
                    SortedBag<Card> drawnCards = builder.build();

                    //Les trois cartes piochés sont rajoutés à la défausse
                    gameState = gameState.withMoreDiscardedCards(drawnCards);

                    int additionalCardsCount = routeDésiré.additionalClaimCardsCount(initialCards, drawnCards);

                    List<SortedBag<Card>> possibleAdditionalCards = gameState.currentPlayerState()
                            .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);


                    if((additionalCardsCount>=1) && (additionalCardsCount<=3) && (possibleAdditionalCards.size()!=0)) {
                        SortedBag<Card> additionalCards = currPlayerInterf.chooseAdditionalCards(possibleAdditionalCards);
                        //Est ce que chooseAdditionalCards retourne toutes les cartes à utiliser ou seulement les cartes additionelles?
                        gameState = (additionalCards.size()==0)? gameState :  gameState.withClaimedRoute(routeDésiré, initialCards.union(additionalCards)) ;
                    }

                    else if(additionalCardsCount==0) {
                        gameState = gameState.withClaimedRoute(routeDésiré, initialCards);

                    }

                    else {
                        //la route n est pas rajouté car le joueur n a pas les cartes additionelles

                    }
                }
                else {
                    gameState = gameState.withClaimedRoute(routeDésiré, initialCards);
                }
            }

            gameState = gameState.forNextTurn();

            //incrémente numberOfLastTurns pour qu il y ait deux turns une fois que lastTurnBegins rend true
            if((gameState.lastTurnBegins())||(numberOfLastTurns !=0)) {
                ++numberOfLastTurns;
            }
        }

        int player1Score = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
        int player2Score = gameState.playerState(PlayerId.PLAYER_2).finalPoints();

        int player1LongestTrail = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes()).length();
        int player2LongestTrail = Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes()).length();

        PlayerId playerLongestTrail;
        switch(Integer.compare(player1LongestTrail, player2LongestTrail)) {
        case 1:
            playerLongestTrail = PlayerId.PLAYER_1;
            player1Score += 10;
            break;
        case 0:
            playerLongestTrail = null; //bonus donné aux deux joueurs
            player1Score += 10;
            player2Score += 10;
            break;
        case -1:
            playerLongestTrail = PlayerId.PLAYER_2;
            player2Score += 10;
            break;
        default:
            break;
        }

        PlayerId playerWinner;
        switch(Integer.compare(player1Score, player2Score)) {
        case 1:
            playerWinner = PlayerId.PLAYER_1;
            break;
        case 0:
            playerWinner = null;
            break;
        case -1:
            playerWinner = PlayerId.PLAYER_2;
            break;
        default:
            break;
        }

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
