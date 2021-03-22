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
    
    private final Deck<Card> deck;    // pioche
    private final SortedBag<Card> bin; // défausse  -   ordre n'importe pas
    /**
     * Construit un état public des cartes dans lequel les cartes face visible
     * sont celles données
     * Lance IllegalArgumentException si faceUpCards ne contient pas 5 éléments
     * ou si la taille de la pioche ou de la défausse sont négatives
     *
     * @param faceUpCards  Liste contenant les cartes face visible
     * @param deck         cartes dans la pioche
     * @param bin          cartes dans la défausse
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

        SortedBag<Card> top_five_face_up = deck.topCards(Constants.FACE_UP_CARDS_COUNT); // first five cards

        List<Card> five_faceUp = top_five_face_up.toList();
        Deck<Card> deckMinus = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT); // pioche : cartes restantes

        SortedBag<Card> bin1 = SortedBag.of(); // défausse : vide
        return new CardState(five_faceUp, deckMinus, bin1);
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

        Card cardOnTop = topDeckCard();

        List<Card> newfiveFaceUp = new ArrayList<>(faceUpCards());
        newfiveFaceUp.set(slot, cardOnTop);

        Deck<Card> reworkedDeck = deck.withoutTopCard();

        return new CardState(newfiveFaceUp, reworkedDeck, bin);
    }

    /**
     * @return  la carte se trouvant au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(deck.size() > 0);   // !isDeckEmpty()
        return deck.topCard();
    }

    /**
     * @return un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche ; lève IllegalArgumentException si la pioche est vide
     * @throws IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!isDeckEmpty());
        Deck<Card> withoutTopCardDeck = deck.withoutTopCard();
        return new CardState(faceUpCards(), withoutTopCardDeck, bin);
    }

    /**
     * @param rng
     * @return un ensemble de cartes identique au récepteur (this), si ce n'est que les cartes de la défausse ont été mélangées au moyen du générateur aléatoire donné afin de constituer la nouvelle pioche; lève IllegalArgumentException si la pioche du récepteur n'est pas vide
     * @throws IllegalArgumentException si la pioche du récepteur n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(deck.size() == 0);

        //mélange les cartes de la défausse + constitue une nouvelle pioche
        Deck<Card> reworkedDeck = null;
        reworkedDeck = Deck.of(bin, rng);
        
        return new CardState(faceUpCards(), reworkedDeck, SortedBag.of());
    }

    /**
     *
     * @param additionalDiscards
     * @return un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
//        if(additionalDiscards == null){additionalDiscards = SortedBag.of(new ArrayList<Card>());}   // just in case for the weekly test !! it should fail if it is null !!!

        if(discardsSize() == 0){
            return new CardState(faceUpCards(), deck, additionalDiscards);
        }else{
            SortedBag<Card> discardsRework = bin;
            discardsRework = discardsRework.union(additionalDiscards);
            return new CardState(faceUpCards(), deck, discardsRework);
        }

    }


}
