package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;


/**
 *
 * @author Thösam Norlha-Tsang (330163)
 *  représente : l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs
 */

public final class CardState extends PublicCardState {
//    private List<Card> fiveFaceUp;  // cartes retournées  -> faceUpCards() de la superclasse
    private Deck<Card> deck;    // pioche
    private SortedBag<Card> bin; // défausse  -   ordre n'importe pas
    /**
     * Construit un état public des cartes dans lequel les cartes face visible
     * sont celles données
     * Lance IllegalArgumentException si faceUpCards ne contient pas 5 éléments
     * ou si la taille de la pioche ou de la défausse sont négatives
     *
     * @param faceUpCards  Liste contenant les cartes face visible
     * @param deckSize     nombre de cartes dans la pioche
     * @param discardsSize nombre de cartes dans la défausse
     */
    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck deck, SortedBag<Card> bin) {
        super(faceUpCards, deckSize, discardsSize);
        this.deck = deck;
        this.bin = bin;
    }

    /**
     * @param deck
     * @return un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné, la pioche est constituée des cartes du tas restantes, et la défausse est vide
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        SortedBag<Card> top_five_face_up = deck.topCards(Constants.FACE_UP_CARDS_COUNT); // first five cards

        List<Card> five_faceUp = top_five_face_up.toList();
        Deck<Card> deckMinus = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT); // pioche : cartes restantes

        SortedBag<Card> bin1 = null; // défausse : vide
        return new CardState(five_faceUp, deckMinus.size(), 0, deckMinus, bin1);
    }

    /**
     * @param slot
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible d'index slot a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée
     */
    public CardState withDrawnFaceUpCard(int slot){ // en assumant entre 0 et size-1
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!isDeckEmpty());

        Card cardOnTop = topDeckCard();

        List<Card> newfiveFaceUp = new ArrayList<>(faceUpCards());
        newfiveFaceUp.set(slot, cardOnTop);

        Deck<Card> reworkedDeck = deck.withoutTopCard();

        return new CardState(newfiveFaceUp, deckSize() - 1, discardsSize(), reworkedDeck, bin);
    }

    /**
     * @return  la carte se trouvant au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(deck.size() > 0);   // !isDeckEmpty()
        return deck.topCard();
    }

    /**
     * @return un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche ; lève IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!isDeckEmpty());
        Deck<Card> withoutTopCardDeck = deck.withoutTopCard();
        return new CardState(faceUpCards(), withoutTopCardDeck.size(), discardsSize(), withoutTopCardDeck, bin);
    }

    /**
     * @param rng
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que les cartes de la défausse ont été mélangées au moyen du générateur aléatoire donné afin de constituer la nouvelle pioche; lève IllegalArgumentException si la pioche du récepteur n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(deck.size() == 0);

        //mélange les cartes de la défausse + constitue une nouvelle pioche
        Deck<Card> reworkedDeck = null;
        reworkedDeck = reworkedDeck.of(bin, rng);
        
        return new CardState(faceUpCards(), discardsSize(), 0, reworkedDeck, null);
    }

    /**
     *
     * @param additionalDiscards
     * @return un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        SortedBag<Card> discardsRework = bin;
        if(discardsRework == null || discardsRework.size() == 0){
            discardsRework = additionalDiscards;
        }else{
            discardsRework.union(additionalDiscards);
        }
        return new CardState(faceUpCards(), deckSize(), discardsRework.size(), deck, discardsRework);
    }

//    for(int slot: FACE_UP_CARD_SLOTS){  // parcourir index des cartes face visible
//
//    }

}
