package ch.epfl.tchu.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;

class DeckTest {

    @Test
    void test() {
                            //Put in main method to see if it works
        
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
            SortedBag<Card> cards = builder.build();
          
            /*SortedBag<Card> cards = SortedBag.of(2,Card.BLUE, 1, Card.LOCOMOTIVE);
            SortedBag<Card> cards = SortedBag.of(); */
            Deck<Card> deck = Deck.of(cards, new Random());
            System.out.println(deck.size()); //3
            System.out.println(deck.isEmpty()); //false
            System.out.println(deck.topCard());
            
            
            Deck<Card> newDeck =  deck.withoutTopCard();
            
            System.out.println("end of test withoutTopCard");
            System.out.println();
            
            Deck<Card> newDeck2 = deck.withoutTopCards(2);
            
            System.out.println("end of test withoutTopCards");
            System.out.println();
            
            System.out.println(deck.topCards(4).toString());
            
            
        }
    }
