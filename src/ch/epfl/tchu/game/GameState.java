package ch.epfl.tchu.game;

import java.util.*;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Classe GameState représente l'état d'une partie de tCHu. Elle hérite de PublicGameState.
 * @author Thösam Norlha-Tsang (330163)
 * @author Aymeric de chillaz (326617)
 */
public final class GameState extends PublicGameState{
    private Deck<Ticket> tickets;
    private CardState cardState;
    private Map<PlayerId, PlayerState> playerState;
    private PlayerId currentPlayerId;
    private PlayerId lastPlayer;

    /**
     * Constructeur privé de GameState, fait appel au constructeur de PublicGameState, sa super-classe
     * @param ticketsList
     * @param cardState
     * @param currentPlayerId
     * @param playerState
     * @param lastPlayer
     */
    private GameState(Deck<Ticket> ticketsList, CardState cardState, PlayerId currentPlayerId,
            Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsList.size(), cardState, currentPlayerId, makePublic(playerState), lastPlayer);
        this.tickets = ticketsList;
        this.cardState = cardState;
        this.playerState = Map.copyOf(playerState);
        this.currentPlayerId = currentPlayerId; 
        //je pourrait rendre l'attribut de la superclasse protected mais je ne sais 
        // pas si la classe resterait immuable
        this.lastPlayer= lastPlayer;
    }
    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerState) {
        Map<PlayerId, PublicPlayerState> playerStateMap = new EnumMap<>(PlayerId.class); 
        playerStateMap.put(PlayerId.PLAYER_1, playerState.get(PlayerId.PLAYER_1));
        playerStateMap.put(PlayerId.PLAYER_2, playerState.get(PlayerId.PLAYER_2));
        return playerStateMap;
    }

    /**
     * méthode de construction publique et statique
     * @param tickets
     * @param rng
     * @return une instante de GameState
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        List<Card> cards = Constants.ALL_CARDS.toList();
        Collections.shuffle(cards);
        List<Card> initialPlayerCards = new ArrayList<>();
        List<Card> secondPlayerCards = new ArrayList<>();
        List<Card> deck = new ArrayList<>();
        PlayerId premierJoueurId = PlayerId.ALL.get(rng.nextInt(2));    // et le deuxième joueur ? //il  viendra après, on veut que le premier joueur
        
        for(int i = 0; i<Constants.INITIAL_CARDS_COUNT; i++) {
            initialPlayerCards.add(cards.get(i));
        }
        for(int i = Constants.INITIAL_CARDS_COUNT; i< Constants.INITIAL_CARDS_COUNT * 2; i++) {
            secondPlayerCards.add(cards.get(i));
        }
        for(int i = Constants.INITIAL_CARDS_COUNT * 2; i<Constants.ALL_CARDS.size(); ++i) {
            deck.add(cards.get(i));
        }
        CardState cardState = CardState.of(Deck.of(SortedBag.of(deck), rng));
        
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(PlayerId.class);                       
        playerStateMap.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(initialPlayerCards)));
        playerStateMap.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(secondPlayerCards)));
        
        return new GameState(Deck.of(tickets, rng), cardState, premierJoueurId, playerStateMap, null);
    }

    /**
     * redéfinit la méthode de même nom de PublicGameState
     * @param playerId
     * @return l'état complet du joueur d'identité donnée, et pas seulement sa partie publique
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {return playerState.get(playerId);}   // état complet du joueur d'identité donnée, et pas seulement sa partie publique    ??? OK la méthode fonctionne

    /**
     * redéfinit la méthode de même nom de PublicGameState
     * @return l'état complet du joueur courant, et pas seulement sa partie publique
     */
    @Override
    public PlayerState currentPlayerState() {return playerState.get(currentPlayerId);}

    /**
     * @param count
     * @return les count billets du sommet de la pioche
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public SortedBag<Ticket> topTickets(int count){
        return tickets.topCards(count);
    }

    /**
     * @param count
     * @return un état identique au récepteur, mais sans les count billets du sommet de la pioche
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public GameState withoutTopTickets(int count) {
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId, playerState, lastPlayer);
    }

    /**
     * @return la carte au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topCard() {
        return cardState.topDeckCard();
    }

    /**
     * @return un état identique au récepteur mais sans la carte au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public GameState withoutTopCard() {
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId, playerState, lastPlayer);
    }

    /**
     * @param discardedCards
     * @return un état identique au récepteur mais avec les cartes données ajoutées à la défausse
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId, playerState, lastPlayer);
    }

    /**
     * @param rng
     * @return un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée à partir de la défausse, mélangée au moyen du générateur aléatoire donné
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if(cardState.isDeckEmpty()) {
            return new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId, playerState, lastPlayer);
        } else {
            return this;}
        }


    //  -   -   -   -   - Second groupe de méthodes permettant d'obtenir un état dérivé de l'état courant en réponse à des actions entreprises par un joueur    -   -   -   -   -

    /**
     * utilisée en début de partie pour ajouter les billets choisis par un joueur à sa main
     * @param playerId
     * @param chosenTickets
     * @return retourne un état identique au récepteur mais dans lequel les billets donnés ont été ajoutés à la main du joueur donné
     * @throws IllegalArgumentException si le joueur en question possède déjà au moins un billet
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).ticketCount()<1); // ==0

//        Map<PlayerId, PlayerState> playerState2 = Map.copyOf(playerState);                                                        // correct ???    Way ?   OK fonctionne, la raison est lparce qu'il faut respecter l'immuabilié, en utilisant directement .put sur une map immuable java accepte mais ça casse le principe d'immuabilité
//        Map<PlayerId, PlayerState> playerState2 = new HashMap<>(playerState); // here we're copying the references (changes in one will affect both map)
        Map<PlayerId, PlayerState> playerState2 = new HashMap<PlayerId, PlayerState>();
        playerState2.putAll(playerState);
        playerState2.put(playerId, playerState.get(playerId).withAddedTickets(chosenTickets));

        // j'espère que la modification est effectué
        return new GameState(tickets, cardState, currentPlayerId, playerState2, lastPlayer);    // remove tickets ? NO !
    }

    /**
     *  ne modifie pas la pioche de billets
     * @param drawnTickets  billets tirés
     * @param chosenTickets billets gardés
     * @return un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets drawnTickets du sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket
     * @throws IllegalArgumentException si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) { // faudrait-il utiliser currentPlayerId() au lieu de currentPlayerId ? oui en général avec les opérations
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> playerState2 = new HashMap<>();
        playerState2.putAll(playerState);
        playerState2.put(currentPlayerId, playerState.get(currentPlayerId).withAddedTickets(chosenTickets));
        return new GameState(tickets.withoutTopCards(drawnTickets.size()), cardState, currentPlayerId, playerState, lastPlayer);  // remove tickets from tickets !!!
    }


    /**
     * modifie aussi les cartes
     * @param slot
     * @return un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> playerState2 = new HashMap<>();
        playerState2.putAll(playerState);
        playerState2.put(currentPlayerId, playerState.get(currentPlayerId).withAddedCard(cardState.faceUpCard(slot)));

        return new GameState(tickets, cardState.withDrawnFaceUpCard(slot), currentPlayerId, playerState2, lastPlayer);
    }


    /**
     * modifie aussi les cartes
     * @return un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée dans la main du joueur courant
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> playerState2 = new HashMap<>();
        playerState2.putAll(playerState);
        playerState2.put(currentPlayerId, playerState.get(currentPlayerId).withAddedCard(cardState.topDeckCard()));

        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId, playerState2, lastPlayer);
    }

    /**
     * @param route
     * @param cards
     * @return un état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route donnée au moyen des cartes données
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> playerState2 = new HashMap<>();
        playerState2.putAll(playerState);
        playerState2.put(currentPlayerId, playerState.get(currentPlayerId).withClaimedRoute(route, cards));
        return new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId, playerState2, lastPlayer);                            // Les cartes sont ajoutées à la défausse
    }


    /**
     * cette méthode doit être appelée uniquement à la fin du tour d'un joueur
     * @return vrai ssi le dernier tour commence, c-à-d si l'identité du dernier joueur est actuellement inconnue mais que le joueur courant n'a plus que deux wagons ou moins
     */
    public boolean lastTurnBegins() {
      return ((lastPlayer == null)&&(playerState.get(currentPlayerId).carCount()<=2));
    }

    /**
     * termine le tour du joueur courant
     * @return un état identique au récepteur si ce n'est que le joueur courant est celui qui suit le joueur courant actuel; de plus, si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur
     */
    public GameState forNextTurn() {

        if(lastTurnBegins()){   //le joueur courant actuel devient le dernier joueur
            return new GameState(tickets, cardState, currentPlayerId.next(), playerState, currentPlayerId);
        }else{
            return new GameState(tickets, cardState, currentPlayerId.next(), playerState, lastPlayer);
        }

    }
}
