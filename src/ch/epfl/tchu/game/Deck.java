package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Représente la pioche des wagons/locomotives, mais aussi des billets
 * La carte du haut est à la position 0 et ainsi de suite
 * @author Aymeric de chillaz (326617)
 *
 */
public final class Deck<C extends Comparable<C>> {
    private final int size;
    private final boolean isEmpty;
    private final List<C> deck;
    
    /**
     * construit un Deck à partir d'une liste de cartes de Type C
     * size correspond à la taille de la liste cards
     * isEmpty prend la valeur true si cards est vide et false sinon
     * deck correspond à une copie de cards
     * @param cards la liste qui permet de créer les trois attributs de cette instance
     */
    private Deck(List<C> cards){
        size = cards.size();
        isEmpty = (size==0);
        deck = List.copyOf(cards);
        //TEST
        /*
        System.out.println("construction Deck");
        for (C card : deck) {
            System.out.println(card);
        }*/
    }
    
    public static void main(String[] args) {
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(Card.BLUE);
        builder.add(Card.GREEN);
        builder.add(Card.BLUE);
        builder.add(Card.LOCOMOTIVE);
        builder.add(Card.VIOLET);
        builder.add(Card.BLACK);
        builder.add(Card.BLACK);
        builder.add(Card.RED);
        builder.add(Card.GREEN);
        builder.add(Card.BLACK);
        builder.add(Card.ORANGE);
        builder.add(Card.YELLOW);
        SortedBag cards = builder.build();
      
        /*SortedBag<Card> cards = SortedBag.of(2,Card.BLUE, 1, Card.LOCOMOTIVE);
        SortedBag<Card> cards = SortedBag.of(); */
        Deck<Card> deck = Deck.of(cards, new Random());
        System.out.println(deck.deck.toString());
        System.out.println(deck.size()); //3
        System.out.println(deck.isEmpty()); //false
        System.out.println(deck.topCard());
        
        
        Deck<Card> newDeck =  deck.withoutTopCard();
        System.out.println(newDeck.deck.toString());
        
        System.out.println("end of test withoutTopCard");
        System.out.println();
        
        Deck<Card> newDeck2 = deck.withoutTopCards(2);
        System.out.println(newDeck2.deck.toString());
        
        System.out.println("end of test withoutTopCards");
        System.out.println();
        
        System.out.println(deck.topCards(4).toString());
        
        
    }
    
    /**
     * méthode de construction qui est public et static
     * @param <C> type des éléments stockés dans le SortedBag cards
     * @param cards SortedBag contenant des cartes de type C
     * @param rng générateur de nombres aléatoires
     * @return une instance de Deck<C> à partir de la liste listCards
     *         qui est la liste correspondant au SortedBag cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> listCards = cards.toList();
        Collections.shuffle(listCards, rng);
        return new Deck<C>(listCards);
    }
    
    /**
     * Getter qui rend la taille de deck associé à cette instance
     * @return size
     */
    public int size() {return size;}
    
    /**
     * Getter qui rend l'attribut isEmpty
     * @return isEmpty
     */
    public boolean isEmpty() {return isEmpty;}
    
    /**
     * méthode qui lance une IllegalArgumentException si le tas est vide
     * @return la carte en haut du deck (à la position 0)
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty);
        return deck.get(0);
    }
    
    /**
     * lance une IllegalArgumentException si le tas est vide
     * @return un tas identique mais sans la carte au sommet
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty);
        return this.withoutTopCards(1);
        
    }
    
    /**
     * lance IllegalArgumentException si count n'est pas compris entre 0 (inclus)
     * et la taille du tas (incluse)
     * @param count nombre de cartes du haut du tas que l'on veut
     * @return SortedBag<C> contenant les count cartes du haut de deck
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument((0<=count)&&(count<=size));
        SortedBag.Builder<C> builder = new SortedBag.Builder<>();
        for(int i=0; i<count; ++i) {
            builder.add(deck.get(i));
        }
        return builder.build();
        
    }
    
    /**
     * lance IllegalArgumentException si count n'est pas compris entre 0 (inclus)
     * et la taille du tas (incluse)
     * @param count nombre de cartes du haut du tas que l'on veut retirer
     * @return un Deck identique mais sans les count cartes du sommet
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument((0<=count)&&(count<=size));
        List<C> newDeckList = List.copyOf(deck);

        return new Deck<C>(newDeckList.subList(count, size));
    }
    
}
