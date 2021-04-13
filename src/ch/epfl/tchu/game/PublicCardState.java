package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * Classe qui représente l'état des cartes wagon/locomotive qui ne sont pas en main
 * des joueurs
 *      les 5 cartes face visible à coté du plateau
 *      la pioche
 *      la défausse
 * @author Aymeric de chillaz (326617)
 *
 */
public class PublicCardState {
    private final int deckSize;
    private final int discardsSize;
    private final List<Card> faceUpCards;
    
    /**
     * Construit un état public des cartes dans lequel les cartes face visible
     * sont celles données
     * @param faceUpCards Liste contenant les cartes face visible
     * @param deckSize nombre de cartes dans la pioche
     * @param discardsSize nombre de cartes dans la défausse
     * @throws IllegalArgumentException si faceUpCards ne contient pas 5 éléments ou si la taille de la pioche ou de la défausse sont négatives
     *
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument((faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT)&&(deckSize>=0)&&(discardsSize>=0));
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
        this.faceUpCards = List.copyOf(faceUpCards);
    }
    
    /**
     * @return le nombre total de cartes qui sont pas en main des joueurs
     * (5 correspond au nombre de carte visible)
     */
    public int totalSize() {return Constants.FACE_UP_CARDS_COUNT + deckSize + discardsSize;}
    
    /**
     * @return les 5 cartes face visible sous la forme d'une liste
     */
    public List<Card> faceUpCards() {return faceUpCards;}
    
    /**
     * @param slot index de la carte visible que nous voulons
     * @return la carte face visible à l'index donné
     * @throws IndexOutOfBoundsException si slot n'est pas compris entre 0(inclus) et 5(exclus)
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, 5);
        return faceUpCards.get(slot);
    }
    
    /**
     * @return la taille de la pioche
     */
    public int deckSize() {return deckSize;}
    
    /**
     * @return true si la pioche est vide et false sinon
     */
    public boolean isDeckEmpty() {return (deckSize == 0);}
    
    /**
     * @return la taille de la défausse
     */
    public int discardsSize() {return discardsSize;}

    
}
