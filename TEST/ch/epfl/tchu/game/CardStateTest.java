package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class CardStateTest {
    @Test
    public void ofTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);
        cards.add(Card.LOCOMOTIVE);
        cards.add(Card.LOCOMOTIVE);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        assertTrue((test.deckSize() == 3) && (test.discardsSize() == 0) && (test.faceUpCards().size() == 5));
    }
    @Test
    public void ofExceptionTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());

        assertThrows(IllegalArgumentException.class, () -> {
            CardState test = CardState.of(deck);
        });
    }
    @Test
    public void ofException2Test(){
        List<Card> cards = new ArrayList<>();
        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());

        assertThrows(IllegalArgumentException.class, () -> {
            CardState test = CardState.of(deck);
        });
    }

    @Test
    public void withDrawnFaceUpCardTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);
        cards.add(Card.LOCOMOTIVE);
        cards.add(Card.LOCOMOTIVE);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        test = test.withDrawnFaceUpCard(0);

//        System.out.println(test.deckSize());
//        System.out.println(test.discardsSize());
//        System.out.println(test.faceUpCards().size());

        assertTrue((test.deckSize() == 2) && (test.discardsSize() == 0) && (test.faceUpCards().size() == 5));

    }
    @Test
    public void withDrawnFaceUpCardBoundExceptionTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);
        cards.add(Card.LOCOMOTIVE);
        cards.add(Card.LOCOMOTIVE);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            CardState rework = test.withDrawnFaceUpCard(5);
        });

    }
    @Test
    public void withDrawnFaceUpCardBoundException2Test(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);
        cards.add(Card.LOCOMOTIVE);
        cards.add(Card.LOCOMOTIVE);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            CardState rework = test.withDrawnFaceUpCard(6);
        });

    }
    @Test
    public void withDrawnFaceUpCardArgumentExceptionTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        assertThrows(IllegalArgumentException.class, () -> {
            CardState rework = test.withDrawnFaceUpCard(2);
        });
    }

    @Test
    public void topDeckCardTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);
        cards.add(Card.LOCOMOTIVE);
        cards.add(Card.LOCOMOTIVE);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        assertNotEquals(test.topDeckCard(), null);

    }
    @Test
    public void topDeckCardArgumentExceptionTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        assertThrows(IllegalArgumentException.class, () -> {
            Card rework = test.topDeckCard();
        });
    }

    @Test
    public void withoutTopDeckCardTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);
        cards.add(Card.LOCOMOTIVE);
        cards.add(Card.LOCOMOTIVE);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        test = test.withoutTopDeckCard();

        assertTrue((test.deckSize() == 2) && (test.discardsSize() == 0) && (test.faceUpCards().size() == 5));

    }
    @Test
    public void withoutTopDeckCardArgumentExceptionTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        assertThrows(IllegalArgumentException.class, () -> {
            CardState rework = test.withoutTopDeckCard();
        });

    }

    @Test
    public void withDeckRecreatedFromDiscardsTest(){    // se base sur "withMoreDiscardedCards(SortedBag<Card>)
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);


        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        List<Card> discardList = new ArrayList<>();
        discardList.add(Card.WHITE);
        discardList.add(Card.ORANGE);
        discardList.add(Card.VIOLET);

        SortedBag<Card> discard = SortedBag.of(discardList);
        test = test.withMoreDiscardedCards(discard);
        test = test.withDeckRecreatedFromDiscards(new Random());
        assertTrue((test.deckSize() == 3) && (test.discardsSize() == 0) && (test.faceUpCards().size() == 5));



    }
    @Test
    public void withDeckRecreatedFromDiscardsArgumentExceptionTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        assertThrows(IllegalArgumentException.class, () -> {
            CardState rework = test.withDeckRecreatedFromDiscards(new Random());
        });
    }

    @Test
    public void withMoreDiscardedCardsTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);
        cards.add(Card.LOCOMOTIVE);
        cards.add(Card.LOCOMOTIVE);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        List<Card> discardList = new ArrayList<>();
        discardList.add(Card.WHITE);
        discardList.add(Card.ORANGE);
        discardList.add(Card.VIOLET);

        SortedBag<Card> discard = SortedBag.of(discardList);
        test = test.withMoreDiscardedCards(discard);
        assertTrue((test.deckSize() == 3) && (test.faceUpCards().size() == 5) && (test.discardsSize() == 3));

    }
    @Test
    public void withMoreDiscardedCardsZeroTest(){
        List<Card> cards = new ArrayList<>();
        cards.add(Card.BLUE);
        cards.add(Card.BLUE);
        cards.add(Card.RED);
        cards.add(Card.RED);
        cards.add(Card.GREEN);
        cards.add(Card.GREEN);
        cards.add(Card.LOCOMOTIVE);
        cards.add(Card.LOCOMOTIVE);

        SortedBag<Card> bag = SortedBag.of(cards);
        Deck<Card> deck = Deck.of(bag , new Random());
        CardState test = CardState.of(deck);

        List<Card> discardList = new ArrayList<>();

        SortedBag<Card> discard = SortedBag.of(discardList);
        test = test.withMoreDiscardedCards(null);
        assertTrue((test.deckSize() == 3) && (test.faceUpCards().size() == 5) && (test.discardsSize() == 0));

    }


}
