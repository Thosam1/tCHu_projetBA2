package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
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


        /**
         * communiquer sa propre identité, et le nom de chaque joueur - le sien inclus
         */
        players.forEach((c,v) -> v.initPlayers(c, playerNames));

        /**
         * permet de choisir un joueur au hasard, communiquer l'info et d initialiser le GameState
         */
        GameState gameState = GameState.initial(tickets, rng);
        Game.infoToAll(players, infoMap.get(gameState.currentPlayerId()).willPlayFirst());

        /**
         *  Pour chaque joueur, communiquer les billets qu'il reçoit initialement
         */
//        List<SortedBag<Ticket>> firstTickets = new ArrayList<>();
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            c.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));    // les deux joueurs pourront donc consulter leur choix en parallèle, et même utiliser l'interface graphique
//            firstTickets.add(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);       // est-ce une bonne idée d'enlever les tickets à ce moment-là ?
        }


        /**
         *  Pour chaque joueur doit être appelée pour savoir quels billets chaque joueur a décidé de garder
         */

        Game.updateStateForAll(players, gameState);     //vérifier qu il ne faut pas l appeler dans le loop avant d appeler chooseInitialTickets
        
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            SortedBag<Ticket> chosenTickets = c.getValue().chooseInitialTickets();      // Comment Sait-on quelle carte il garde si on enlève les tickets du haut de la pile dans la loop avant ??? Ou faut-il les sauvegarder ?
            gameState = gameState.withInitiallyChosenTickets(c.getKey(), chosenTickets);  //withInitiallyChosenTickets ne modife pas la pioche de billets...
            c.getValue().updateState(gameState, gameState.playerState(c.getKey()));
            // faudrait-il laisser l'update directement après le choix ou à la fin quand les deux joueurs ont finit leur choix ? c'est pas précisé donc facultatif ?
        }


        /**
         *  Communiquer à chacun le nombre de billets gardés par chaque joueur
         */
        // après leur choix, on les informe -> équitable
       // joueur 1 reçoit l'info de l'adversaire seulement ou de lui-même aussi ? Oui !
        Game.infoToAll(players, player1.keptTickets(gameState.playerState(PlayerId.PLAYER_1).ticketCount()));   // une bonne façon ou y aurait-il une meilleure approche ? par exemple enregister les chosenTickets
        Game.infoToAll(players, player2.keptTickets(gameState.playerState(PlayerId.PLAYER_2).ticketCount()));   // dans une liste et calculer la taille au lieu de partir du principe que les joueurs commencent avec 0 ticket ?




        /**La partie commence*/
        while(numberOfLastTurns<=2){//le loop s'arrete quand les deux joueurs ont joué une dernière fois
            // lisibilité
            Player currPlayerInterf = players.get(gameState.currentPlayerId());  //currentPlayerInterface
            Info currInf = infoMap.get(gameState.currentPlayerId());

            Game.infoToAll(players, currInf.canPlay());
            Game.updateStateForAll(players, gameState);

            /**
             *  Savoir quelle action le joueur courant désire effectuer parmi les trois possibles
             */

            Player.TurnKind turnKind = currPlayerInterf.nextTurn();
            
            if(turnKind == Player.TurnKind.DRAW_TICKETS) {
                //faut il verifier qu il reste des tickets  ???
                SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);

                Game.infoToAll(players, currInf.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                SortedBag<Ticket> chosenTickets = currPlayerInterf.chooseTickets(drawnTickets); // il choisit

                gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets); // playerState et tickets changent

                Game.infoToAll(players, currInf.keptTickets(chosenTickets.size()));

                
            }else if(turnKind == Player.TurnKind.DRAW_CARDS){

                for(int i = 0; i < 2; i++){ // tire deux fois
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int slot = currPlayerInterf.drawSlot();
  
                    Card pickedVisibleCard = (slot != Constants.DECK_SLOT) ? gameState.cardState().faceUpCard(slot) : null ;    //Constans.DECK_SLOT est égal à -1 et signifie que le joueur veut la carte du haut du deck
                    gameState = (slot == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(slot);

                    //comment faire pour savoir quelle carte a été sélectionné si slot n est pas 1
                    Game.infoToAll(players, (slot == Constants.DECK_SLOT) ? currInf.drewBlindCard() : currInf.drewVisibleCard(pickedVisibleCard));

                    if(i==0){
                        Game.updateStateForAll(players, gameState);
                    }
                }
            }
            
            else {  //comment vérifier que le joueur a assez de wagons ??? -> regarde la ligne en bas, il y a une méthode canClaimRoute() dans la classe PlayerState
//                gameState.currentPlayerState().canClaimRoute(route);
                // Devrait-on faire une do while loop ? en mode while canClaimRoute == false ... ?
                // que ce passe-t-il si le joueur choisit de s'emparer d'une route, puis découvre qu'il ne peut s'emparer d'aucune route ? ou change d'avis ?
                // Doit-il dans ce cas sauter son tour ? y a t-il une méthode pour sauter son tour ?? ou revenir en arrière ?


                /**la route que le player veut*/
                Route routeDésiré = currPlayerInterf.claimedRoute();
                SortedBag<Card> initialCards = currPlayerInterf.initialClaimCards();
                
                if (routeDésiré.level() == Route.Level.UNDERGROUND) {
                    Game.infoToAll(players, currInf.attemptsTunnelClaim(routeDésiré, initialCards));
                    
                    SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
                    
                    for(int i = 0; i<Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        builder.add(gameState.topCard());
                        gameState = gameState.withoutTopCard(); //retourne un nouveau gameState sans la carte du haut
                        }
                    SortedBag<Card> drawnCards = builder.build(); 

                    int additionalCardsCount = routeDésiré.additionalClaimCardsCount(initialCards, drawnCards);
                    
                    Game.infoToAll(players, currInf.drewAdditionalCards(drawnCards, additionalCardsCount));
                    
                    //Les trois cartes piochés sont rajoutés à la défausse
                    gameState = gameState.withMoreDiscardedCards(drawnCards);
                    
                    
                    List<SortedBag<Card>> possibleAdditionalCards = gameState.currentPlayerState()
                            .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);
                    
                    //est ce que c'est bien d'avoir mis dans le if que possibleAdditionalCards doit etre différent que 0
                    if((additionalCardsCount>=1) && (additionalCardsCount<=3)/* && (possibleAdditionalCards.size()!=0)*/) {
                        SortedBag<Card> additionalCards = currPlayerInterf.chooseAdditionalCards(possibleAdditionalCards);  
                        
                        if(additionalCards.size() == 0){
                            Game.infoToAll(players, currInf.didNotClaimRoute(routeDésiré));
                        }
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
                else {  // pas un tunnel
                    gameState = gameState.withClaimedRoute(routeDésiré, initialCards);
                }
            }
            
            if(gameState.lastTurnBegins()) {
                Game.infoToAll(players, currInf.lastTurnBegins(gameState.currentPlayerState().carCount()));
            }
            
            //incrémente numberOfLastTurns pour qu il y ait deux turns une fois que lastTurnBegins rend true
            if((gameState.lastTurnBegins())||(numberOfLastTurns !=0)) {
                ++numberOfLastTurns;
            }
            gameState = gameState.forNextTurn();//doit etre appelé en dernier
        }
        
        int player1Score = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
        int player2Score = gameState.playerState(PlayerId.PLAYER_2).finalPoints();
        
        Trail player1LongestTrail = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes());
        Trail player2LongestTrail = Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes());
        
        
        switch(Integer.compare(player1LongestTrail.length(), player2LongestTrail.length())) {
        case 1:
            player1Score += 10;
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1).getsLongestTrailBonus(player1LongestTrail));
            break;
            
        case 0:
            //bonus donné aux deux joueurs
            player1Score += 10;
            player2Score += 10;
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1).getsLongestTrailBonus(player1LongestTrail));
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2).getsLongestTrailBonus(player2LongestTrail));
            break;
            
        case -1:
            player2Score += 10;
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2).getsLongestTrailBonus(player2LongestTrail));
            break;
        default:
            break;
        }
        
        switch(Integer.compare(player1Score, player2Score)) {
        case 1:
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1).won(player1Score, player2Score));
            break;
        case 0:
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1).won(player1Score, player2Score));
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2).won(player2Score, player1Score));
            break;
        case -1:
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2).won(player2Score, player1Score));
            break;
        default:
            break;
        }
     //on informe les joueurs du résultat final de la partie 
     //afin qu ils connaissent l'état dans lequel la partie s'est terminé   
        Game.updateStateForAll(players, gameState);
       
    }


    /**
     * permet d'envoyer une information à tous les joueurs, en appelant la méthode receiveInfo de chacun d'eux
     */
    private static void infoToAll(Map<PlayerId, Player> players, String info) {
        players.forEach((c,v) -> v.receiveInfo(info));
    }


    /**
     * permet d'informer tous les joueurs d'un changement d'état, en appelant la méthode updateState de chacun d'eux
     */
    private static void updateStateForAll(Map<PlayerId, Player> players, GameState gameState) {
        players.forEach((c,v) -> v.updateState(gameState, gameState.playerState(c)));
    }
}
