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
    private final Deck<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;

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
        super(ticketsList.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.tickets = ticketsList;
        this.cardState = cardState;
        this.playerState = Map.copyOf(playerState);
        Objects.requireNonNull(ticketsList);
        Objects.requireNonNull(cardState);
    }

    /**
     * méthode de construction publique et statique
     * Créé une GameState de début de jeu
     * 4 cartes sont données aux joueurs et l'identité du premier joueur est déterminé
     * @param tickets
     * @param rng
     * @return une instante de GameState
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        /**
         *  Création de la map, des playerstates, choix aléatoire des premiers joueurs
         */
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(PlayerId.class);
        List<PlayerId> shuffledList = new ArrayList<>(PlayerId.ALL);
        Collections.shuffle(shuffledList, rng);
        
        /**
         *  Création du deck - mélanger - distribuer
         */
        Deck<Card> deck = Deck.of(SortedBag.of(Constants.ALL_CARDS), rng); //mélange les cartes

        for(PlayerId player : shuffledList) {
            SortedBag<Card> playerCards = deck.topCards(Constants.INITIAL_CARDS_COUNT);
            deck = deck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
            
            playerStateMap.put(player, PlayerState.initial(playerCards));
            
        }
        return new GameState(Deck.of(tickets, rng), CardState.of(deck), shuffledList.get(0), playerStateMap, null);
    }

    /**
     * redéfinit la méthode de même nom de PublicGameState
     * @param playerId
     * @return l'état complet du joueur d'identité donnée, et pas seulement sa partie publique
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {return playerState.get(playerId);}

    /**
     * redéfinit la méthode de même nom de PublicGameState
     * @return l'état complet du joueur courant, et pas seulement sa partie publique
     */
    @Override
    public PlayerState currentPlayerState() {return playerState.get(currentPlayerId());}

    /**
     * @param count
     * @return les count billets du sommet de la pioche
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument((0<=count)&&(count<=tickets.size()));
        return tickets.topCards(count);
    }

    /**
     * @param count
     * @return un état identique au récepteur, mais sans les count billets du sommet de la pioche
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument((0<=count)&&(count<=tickets.size()));
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @return la carte au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState().isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * @return un état identique au récepteur mais sans la carte au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState().isDeckEmpty());
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @param discardedCards
     * @return un état identique au récepteur mais avec les cartes données ajoutées à la défausse
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * @param rng
     * @return un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée à partir de la défausse, mélangée au moyen du générateur aléatoire donné
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return (cardState.isDeckEmpty()) ? new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer()) : this;
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
        Preconditions.checkArgument(playerState.get(playerId).ticketCount()==0);
        Map<PlayerId, PlayerState> playerState2 = new EnumMap<>(playerState);
        playerState2.put(playerId, playerState.get(playerId).withAddedTickets(chosenTickets));
        return new GameState(tickets, cardState, currentPlayerId(), playerState2, lastPlayer());
    }

    /**
     * modifie la pioche de billets
     * @param drawnTickets  billets tirés
     * @param chosenTickets billets gardés
     * @return un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets drawnTickets du sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket
     * @throws IllegalArgumentException si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> playerState2 = new EnumMap<>(playerState);
        playerState2.put(currentPlayerId(), playerState.get(currentPlayerId()).withAddedTickets(chosenTickets));
        return new GameState(tickets.withoutTopCards(drawnTickets.size()), cardState, currentPlayerId(), playerState2, lastPlayer());
    }


    /**
     * modifie aussi la pioche de carte
     * @param slot
     * @return un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> playerState2 = new EnumMap<>(playerState);
        playerState2.put(currentPlayerId(), playerState.get(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot)));
        return new GameState(tickets, cardState.withDrawnFaceUpCard(slot), currentPlayerId(), playerState2, lastPlayer());
    }


    /**
     * modifie aussi la pioche de carte
     * @return un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée dans la main du joueur courant
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> playerState2 = new EnumMap<>(playerState);
        playerState2.put(currentPlayerId(), playerState.get(currentPlayerId()).withAddedCard(cardState.topDeckCard()));
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState2, lastPlayer());
    }

    /** modifie la défausse
     * @param route
     * @param cards
     * @return un état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route donnée au moyen des cartes données
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> playerState2 = new EnumMap<>(playerState);
        playerState2.put(currentPlayerId(), playerState.get(currentPlayerId()).withClaimedRoute(route, cards));     // on enlève les cartes utilisées de la main du joueur
        return new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId(), playerState2, lastPlayer());      // puis ces cartes sont ajoutées à la défausse
    }


    /**
     * cette méthode doit être appelée uniquement à la fin du tour d'un joueur
     * @return vrai ssi le dernier tour commence, c-à-d si l'identité du dernier joueur est actuellement inconnue mais que le joueur courant n'a plus que deux wagons ou moins
     */
    public boolean lastTurnBegins() {
      return ((lastPlayer() == null)&&(playerState.get(currentPlayerId()).carCount()<=Constants.LAST_TURN_BEGINS_CAR_COUNT));
    }

    /**
     * termine le tour du joueur courant
     * @return un état identique au récepteur si ce n'est que le joueur courant est celui qui suit le joueur courant actuel; de plus, si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur
     */
    public GameState forNextTurn() {
        return (lastTurnBegins()) ? new GameState(tickets, cardState, currentPlayerId().next(), playerState, currentPlayerId()) : new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastPlayer());
    }

}
