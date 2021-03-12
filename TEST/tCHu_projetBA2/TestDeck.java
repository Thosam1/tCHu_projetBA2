package tCHu_projetBA2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Deck;

class TestDeck {
    
    @Test
    public void sizeWorksWhenNotEmpty(){
        SortedBag<Card> cards = SortedBag.of(2, Card.BLUE, 1, Card.GREEN);
        assertEquals(3, Deck.of(cards, new Random()).size());
    }
    
    @Test
    public void sizeWorksWhenEmpty(){
        SortedBag<Card> cards = SortedBag.of();
        assertEquals(0, Deck.of(cards, new Random()).size());
    }
    
    @Test
    public void isEmptyWorksWhenNotEmpty(){
        SortedBag<Card> cards = SortedBag.of(2, Card.BLUE, 1, Card.GREEN);
        assertEquals(false, Deck.of(cards, new Random()).isEmpty());
    }
    
    @Test
    public void isEmptyWorksWhenEmpty(){
        SortedBag<Card> cards = SortedBag.of();
        assertEquals(true, Deck.of(cards, new Random()).isEmpty());
    }
    
    @Test
    public void topCardThrowsExceptionWhenNull(){
        SortedBag<Card> cards = SortedBag.of();
        Deck deck = Deck.of(cards, new Random());
        assertThrows(IllegalArgumentException.class, () -> {
           deck.topCard();
          });}
    
    @Test
    public void withoutTopCardThrowsExceptionWhenNull(){
        SortedBag<Card> cards = SortedBag.of();
        Deck deck = Deck.of(cards, new Random());
        assertThrows(IllegalArgumentException.class, () -> {
           deck.withoutTopCard();
          });}

    @Test
    public void topCardsThrowsExceptionWhenNegative(){
        SortedBag<Card> cards = SortedBag.of(2,Card.LOCOMOTIVE);
        Deck deck = Deck.of(cards, new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCards(-1);
        });}
    
    @Test
    public void topCardsThrowsExceptionWhenOverDeckSize(){
        SortedBag<Card> cards = SortedBag.of(2,Card.LOCOMOTIVE);
        Deck deck = Deck.of(cards, new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCards(3);
        });}
    @Test
    public void topCards() {
        SortedBag<Card> cards = SortedBag.of(2, Card.LOCOMOTIVE);
        Deck deck = Deck.of(cards, new Random());
        int number = deck.topCards(2).size();
        assertEquals(number, 2);
    }
    
    @Test
    public void withoutTopCardsThrowsExceptionWhenNegative(){
        SortedBag<Card> cards = SortedBag.of(2,Card.LOCOMOTIVE);
        Deck deck = Deck.of(cards, new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCards(-1);
        });}
    
    @Test
    public void withoutTopCardsThrowsExceptionWhenOverSize(){
        SortedBag<Card> cards = SortedBag.of(2,Card.LOCOMOTIVE);
        Deck deck = Deck.of(cards, new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCards(3);
        });}

    @Test
    public void withoutTopCards() {
        SortedBag<Card> cards = SortedBag.of(2, Card.LOCOMOTIVE);
        Deck deck = Deck.of(cards, new Random());
        int number = deck.withoutTopCards(2).size();
        assertEquals(number, 0);
    }
}
    
    
    
    
    
    
    
       
