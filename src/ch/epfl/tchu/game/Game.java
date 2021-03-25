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
        int numberOfLastTurns = 0; //égal à 1 quand l'avant dernier joueur joue et 2 qand le dernier joue

        Info player1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        Info player2 = new Info(playerNames.get(PlayerId.PLAYER_2));

        Map<PlayerId, Info> infoMap = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);
        // Avant le début de la partie

        //comment avoir accès au joueur courant? PublicPlayerState

        /**
         * communiquer sa propre identité, et le nom de chaque joueur - le sien inclus
         */
        players.forEach((c,v) -> v.initPlayers(c, playerNames));   

        /**
         * permet de choisir un joueur au hasard, communiquer l'info et d initialiser le GameState
         */
        GameState gameState = GameState.initial(tickets, rng);
        
        players.forEach((c,v) -> {
            v.receiveInfo(infoMap.get(gameState.currentPlayerId()).willPlayFirst());
            //v.receiveInfo(infoMap.get(c).willPlayFirst());
        });

        /**
         *  Pour chaque joueur, communiquer les billets qu'il reçoit initialement
         */
        players.forEach((c, v) -> {
            v.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

            /*infoMap.forEach((playerId, playerInfo) -> 
            v.receiveInfo(playerInfo.drewTickets(Constants.INITIAL_TICKETS_COUNT)));
            Il faut attendre que tous les joueurs aient fait leur choix comme dit
            à la deuxieme ligne sur quand receiveInfo doit etre appelé*/
        });
       
        /**
         *  Pour chaque joueur doit être appelée pour savoir quels billets chaque joueur a décidé de garder
         */
        players.forEach((c, v) -> {
            SortedBag<Tickets> chosenTickets = v.chooseInitialTickets();
            gameState.withInitiallyChosenTickets(c, chosenTickets);
        });


        /**
         *  Communiquer à chacun le nombre de billets gardés par chaque joueur
         */
        players.forEach((c, v) -> { // après leur choix, on les informe
        // joueur 1 reçoit l'info de l'adversaire seulement ou de lui-même aussi ? bonne question
        // c'est bien chooseInitialTickets() de la classe Player qu'il faut appeler ?
        // je dirai que oui  
            v.receiveInfo(player1.keptTickets(players.get(PlayerId.PLAYER_1).chooseInitialTickets().size()));
            v.receiveInfo(player2.keptTickets(players.get(PlayerId.PLAYER_2).chooseInitialTickets().size()));
        });
        
        
        
        
        /**La partie commence*/
        while(numberOfLastTurns<=2){//le loop s'arrete quand les deux joueurs ont joué une dernière fois
            //currentPlayerInterface
            Player currPlayerInterf = players.get(gameState.currentPlayerId());  // lisibilité
        //    PlayerState currPlayerStat = gameState.currentPlayerState();
            //je ne pense pas que ce soit une bonne idée de sépparer currentPlayerState et gameState
            Info currInf = infoMap.get(gameState.currentPlayerId());

            /**
             *  Savoir quelle action le joueur courant désire effectuer parmi les trois possibles
             */
            players.forEach((c, v) -> {
                v.receiveInfo(currInf.canPlay());
            });

            Player.TurnKind turnKind = currPlayerInterf.nextTurn();
            
            
            if(turnKind == Player.TurnKind.DRAW_TICKETS) {
                //faut il verifier qu il reste des tickets
                SortedBag<Tickets> chosenTickets = currPlayerInterf.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                
                players.forEach((c, v) -> {
                    v.receiveInfo(currInf.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                });

                players.forEach((c, v) -> {
                    v.receiveInfo(currInf.keptTickets(chosenTickets.size()));
                });
                gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), chosenTickets);

            }else if(turnKind == Player.TurnKind.DRAW_CARDS){

                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                int slot1 = currPlayerInterf.drawSlot();
                //Constans.DECK_SLOT est égal à -1 et signigie que le joueur veut la carte du haut du deck
                gameState = (slot1 == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(slot1);
                
                players.forEach((c, v) -> { // if a visible card, what about discards ???
                    v.receiveInfo(currInf.drewVisibleCard());
                });
                players.forEach((c, v) -> { // if a blind card
                    v.receiveInfo(currInf.drewBlindCard());
                });
                
                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                int slot2 = currPlayerInterf.drawSlot();    // faut il communiquer ceci à l'autre joueur ?
                gameState = (slot2 == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(slot2);
                // qu'en est-il de gameState.WithDrawnFaceUpCard() ???
            
            }
            
            
            else {//comment vérifier que le joueur a assé de wagons
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
            playerWinner = PlayerId.PLAYER_2
            break;
        default:
            break;
        }
       
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
