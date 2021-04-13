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
    
    private final Deck<Card> deck;    // pioche
    private final SortedBag<Card> bin; // défausse

    /**
     * Construit un état public des cartes dans lequel les cartes face visible
     * sont celles données
     *
     * @param faceUpCards  Liste contenant les cartes face visible
     * @param deck         cartes dans la pioche
     * @param bin          cartes dans la défausse
     * @throws IllegalArgumentException si faceUpCards ne contient pas 5 éléments ou si la taille de la pioche ou de la défausse sont négatives
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> bin) {
        super(faceUpCards, deck.size(), bin.size());
        this.deck = deck;
        this.bin = bin;
    }

    /**
     * @param deck
     * @return un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné, la pioche est constituée des cartes du tas restantes, et la défausse est vide
     * @throws IllegalArgumentException si le tas donné contient moins de 5 cartes
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);
        List<Card> five_faceUp = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(); // first five cards
        Deck<Card> deckMinus = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT); // pioche contenant les cartes restantes
        return new CardState(five_faceUp, deckMinus, SortedBag.of());
    }

    /**
     * @param slot
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible d'index slot a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée
     * @throws IndexOutOfBoundsException si l'index donné n'est pas compris entre 0 (inclus) et 5 (exclus)
     * @throws IllegalArgumentException IllegalArgumentException si la pioche est vide
     */
    public CardState withDrawnFaceUpCard(int slot){ // en assumant entre 0 et size-1
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!isDeckEmpty());

        List<Card> newfiveFaceUp = new ArrayList<>(faceUpCards());
        newfiveFaceUp.set(slot, topDeckCard());

        return new CardState(newfiveFaceUp, deck.withoutTopCard(), bin);
    }

    /**
     * @return  la carte se trouvant au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!isDeckEmpty());   // !isDeckEmpty()
        return deck.topCard();
    }

    /**
     * @return un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!isDeckEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard(), bin);
    }

    /**
     * @param rng
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que les cartes de la défausse ont été mélangées au moyen du générateur aléatoire donné afin de constituer la nouvelle pioche;
     * @throws IllegalArgumentException si la pioche du récepteur n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(deck.size() == 0);
        return new CardState(faceUpCards(), Deck.of(bin, rng), SortedBag.of()); //mélange les cartes de la défausse + constitue une nouvelle pioche
    }

    /**
     *
     * @param additionalDiscards
     * @return un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(faceUpCards(), deck, SortedBag.of(bin).union(additionalDiscards));
    }


}
