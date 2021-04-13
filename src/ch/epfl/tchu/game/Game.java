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
        int numberOfLastTurns = 0; // Égal à 1 quand l'avant dernier joueur joue et 2 quand le dernier joue

        Info player1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        Info player2 = new Info(playerNames.get(PlayerId.PLAYER_2));

        Map<PlayerId, Info> infoMap = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);

        // Avant le début de la partie  -   -   -   -   -


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
        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            c.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));    // les deux joueurs pourront donc consulter leur choix en parallèle, et même utiliser l'interface graphique
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }


        /**
         *  Pour chaque joueur doit être appelée pour savoir quels billets chaque joueur a décidé de garder
         */
        Game.updateStateForAll(players, gameState);
        
        List<Integer> nbOfInitialTickets = new ArrayList<>();

        for(Map.Entry<PlayerId, Player> c : players.entrySet()){
            SortedBag<Ticket> chosenTickets = c.getValue().chooseInitialTickets();      
            nbOfInitialTickets.add(chosenTickets.size());
            gameState = gameState.withInitiallyChosenTickets(c.getKey(), chosenTickets);  
            Game.updateStateForAll(players, gameState); // facultatif
        }


        /**
         *  Communiquer à chacun le nombre de billets gardés par chaque joueur
         */
        Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1).keptTickets(nbOfInitialTickets.get(0)));
        Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2).keptTickets(nbOfInitialTickets.get(1)));



        /**La partie commence*/
        while(numberOfLastTurns<=2){//le loop s'arrete quand les deux joueurs ont joué une dernière fois
            // lisibilité
            Player currPlayerInterf = players.get(gameState.currentPlayerId());  //currentPlayerInterface
            Info currInf = infoMap.get(gameState.currentPlayerId());

            Game.infoToAll(players, currInf.canPlay());
            Game.updateStateForAll(players, gameState);

            /**
             *  Savoir quelle action le joueur courant désire effectuer parmi les trois possibles, on estime que le joueur ne fait pas des choses qui lancent des exceptions
             */

            Player.TurnKind turnKind = currPlayerInterf.nextTurn();

            if(turnKind == Player.TurnKind.DRAW_TICKETS) {
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

                    Game.infoToAll(players, (slot == Constants.DECK_SLOT) ? currInf.drewBlindCard() : currInf.drewVisibleCard(pickedVisibleCard));

                    if(i==0){//nous voulons une update après que la première carte aie été tirée pour que le joueur est accès au gameState courant
                        Game.updateStateForAll(players, gameState);
                    }
                }

            } else {

                /**Si ce n est pas un tunnel ou que c'est un tunnel et qu on ne lui impose pas de cartes en plus, il prend la route et ne peux pas changer d'avis,
                 * parcontre si c'est un tunnel et qu on lui impose des cartes en plus alors il peut changer d avis mais alors ça saute son tour ce qui est géré par gameState.nextTurn() a la fin du while loop*/


                /**la route que le player veut*/
                Route routeDésirée = currPlayerInterf.claimedRoute();
                SortedBag<Card> initialCards = currPlayerInterf.initialClaimCards();

                if (routeDésirée.level() == Route.Level.UNDERGROUND) {
                    Game.infoToAll(players, currInf.attemptsTunnelClaim(routeDésirée, initialCards));

                    SortedBag.Builder<Card> builder = new SortedBag.Builder<>();

                    for(int i = 0; i<Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        builder.add(gameState.topCard());
                        gameState = gameState.withoutTopCard(); //retourne un nouveau gameState sans la carte du haut
                    }
                    SortedBag<Card> drawnCards = builder.build();

                    int additionalCardsCount = routeDésirée.additionalClaimCardsCount(initialCards, drawnCards);

                    Game.infoToAll(players, currInf.drewAdditionalCards(drawnCards, additionalCardsCount));

                    //Les trois cartes piochés sont rajoutés à la défausse
                    gameState = gameState.withMoreDiscardedCards(drawnCards);


                    List<SortedBag<Card>> possibleAdditionalCards = List.of(SortedBag.of());

                    if(additionalCardsCount!=0) { //possibleAdditionalCards ne doit pas prendre de additionalCardsCount égal à 0
                        possibleAdditionalCards = gameState.currentPlayerState()
                                .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);
                    }

                    //si le joueur a des cartes additionnelles à poser
                    if((additionalCardsCount>=1) && (additionalCardsCount<=3) && (possibleAdditionalCards.size()!=0)) {
                        //les cartes additionnelles que le joueur décide de poser
                        SortedBag<Card> additionalCards = currPlayerInterf.chooseAdditionalCards(possibleAdditionalCards);


                        //si il décide de ne pas pas poser plus de cartes ou ne peut pas plus en poser additionalCards est vide

                        //je ne sait pas si chooseAdditionalCards peut retourner une valeur null ou vide donc je teste les deux
                        //en faisant attention de vérifier dabord que ce n'est pas null pour ne pas lancer d exception
                        if(additionalCards == null || additionalCards.size() == 0){
                            Game.infoToAll(players, currInf.didNotClaimRoute(routeDésirée));
                        }
                        else Game.infoToAll(players, currInf.claimedRoute(routeDésirée,initialCards.union(additionalCards))); //info la route a été prise par le joueur

                        gameState = (additionalCards == null || additionalCards.size() == 0)? gameState :  gameState.withClaimedRoute(routeDésirée, initialCards.union(additionalCards)) ;
                    }
                    //si le joueur n a pas de cartes additionnelles à poser alors il s'empare de la route
                    else if(additionalCardsCount==0) {
                        Game.infoToAll(players, currInf.claimedRoute(routeDésirée,initialCards)); //info la route a été prise par le joueur
                        gameState = gameState.withClaimedRoute(routeDésirée, initialCards);

                    }

                    //ce else n'est pas utile si on ne met pas (possibleAdditionalCards.size()!=0) comme condition dans le if
                    else {
                        //la route n est pas rajouté car le joueur n a pas les cartes additionelles
                        Game.infoToAll(players, currInf.didNotClaimRoute(routeDésirée));

                    }
                }
                else {  // pas un tunnel donc il faut seulement prendre le controle de la route
                    Game.infoToAll(players, currInf.claimedRoute(routeDésirée, initialCards)); //info la route a été prise par le joueur
                    gameState = gameState.withClaimedRoute(routeDésirée, initialCards);
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

        //on informe les joueurs du résultat final de la partie
        //afin qu ils connaissent l'état dans lequel la partie s'est terminé
        Game.updateStateForAll(players, gameState);

        switch(Integer.compare(player1Score, player2Score)) {
            case 1:
                Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1).won(player1Score, player2Score));
                break;
            case 0:
                Game.infoToAll(players, Info.draw(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2)), player1Score));
                break;
            case -1:
                Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2).won(player2Score, player1Score));
                break;
            default:
                break;
        }
    }


    /**
     * permet d'envoyer une information À tous les joueurs, en appelant la méthode receiveInfo de chacun d'eux
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
