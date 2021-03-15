package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class GameState extends PublicGameState{
    private Deck<Ticket> tickets;
    private CardState cardState;
    private Map<PlayerId, PlayerState> playerState;
    private PlayerId currentPlayerId;
    private PlayerId lastPlayer;
    
    private GameState(Deck<Ticket> ticketsList, CardState cardState, PlayerId currentPlayerId,
            Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsList.size(), cardState, currentPlayerId, playerState, lastPlayer);
        this.tickets = ticketsList;
        this.cardState = cardState;
        this.playerState = playerState;
        this.currentPlayerId = currentPlayerId; 
        //je pourrait rendre l'attribut de la superclasse protected mais je ne sais 
        // pas si la classe resterait immuable
        this.lastPlayer= lastPlayer;
    }
    private static makePublic() {
        
    }
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        List<Card> cards = Constants.ALL_CARDS.toList();
        Collections.shuffle(cards);
        List<Card> initialPlayerCards = new ArrayList<>();
        List<Card> secondPlayerCards = new ArrayList<>();
        List<Card> deck = new ArrayList<>();
        PlayerId premierJoueurId = PlayerId.ALL.get(rng.nextInt(2));
        
        for(int i = 0; i<4; i++) {
            initialPlayerCards.add(cards.get(i));
        }
        for(int i = 4; i<8; i++) {
            secondPlayerCards.add(cards.get(i));
        }
        for(int i = 8; i<Constants.ALL_CARDS.size(); ++i) {
            deck.add(cards.get(i));
        }
        CardState cardState = CardState.of(Deck.of(SortedBag.of(deck), rng));
        
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(PlayerId.class);
        playerStateMap.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(initialPlayerCards)));
        playerStateMap.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(secondPlayerCards)));
        
        return new GameState(Deck.of(tickets, rng), cardState, premierJoueurId, playerStateMap, null);
    }
    
    public PlayerState playerState(PlayerId playerId) {return playerState.get(playerId);}
    public PlayerState currentPlayerState() {return playerState.get(currentPlayerId);}


    public SortedBag<Ticket> topTickets(int count){
        return tickets.topCards(count);
    }
    
    public GameState withoutTopTickets(int count) {
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId, playerState, lastPlayer);
    }
    
    public Card topCard() {
        return cardState.deck.topCard();
    }
    public GameState withoutTopCard() {
        //est ce que la carte du sommet de la pioche doit etre ajouté à bin?
        return cardState.deck.withoutTopCard;
    }
    
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId, playerState, lastPlayer);
    }
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if(cardState.deck.size() == 0) {
            return new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId, playerState, lastPlayer);
        }
        else {
            return this;}
        }
    
    
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).ticketCount()==0);
        playerState.put(playerId, playerState.get(playerId).withAddedTickets(chosenTickets));
    }
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {}
    public GameState withDrawnFaceUpCard(int slot) {}
    public GameState withBlindlyDrawnCard() {}
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {}
    
    
    public boolean lastTurnBegins() {
      Je met une erreur ici parcequ il faut que je vois si je ne doit pas avoir un attribut boolean qui chqnge de valeur ici
      //TODO  
      return ((lastPlayer == null)&&(playerState.get(currentPlayerId).carCount<=2)) {
    }
        
    public GameState forNextTurn() {
        PlayerId newCurrentPlayer = currentPlayerId.next();}
}
