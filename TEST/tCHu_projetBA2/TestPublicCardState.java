package tCHu_projetBA2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PublicCardState;

class TestPublicCardState {

    @Test
    public void constructeurThrowsExceptionWhenMoreThan5(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE, Card.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
           new PublicCardState(list, 13, 15);
          });}

    @Test
    public void constructeurThrowsExceptionWhenLessThan5(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.VIOLET, Card.LOCOMOTIVE);
        assertThrows(IllegalArgumentException.class, () -> {
           new PublicCardState(list, 13, 15);
          });}
    
    @Test
    public void constructeurThrowsExceptionWhenDeckSizeIsNegative(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        assertThrows(IllegalArgumentException.class, () -> {
           new PublicCardState(list, -5, 15);
          });}
    
    @Test
    public void constructeurThrowsExceptionWhenGarbageSizeIsNegative(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        assertThrows(IllegalArgumentException.class, () -> {
           new PublicCardState(list, 13, -15);
          });}
    
    @Test
    public void faceUpCardsWorks(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        PublicCardState cardState = new PublicCardState(list, 4, 5);
        assertEquals(List.copyOf(list), cardState.faceUpCards());
    }
    
    @Test
    public void faceUpCardWorks(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        PublicCardState cardState = new PublicCardState(list, 4, 5);
        assertEquals(Card.BLACK, cardState.faceUpCard(2));
    }
    
    @Test
    public void faceUpCardThrowsExceptionWhenSlotIsOutOfBounds1(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        PublicCardState cardState = new PublicCardState(list, 4, 5);
        assertThrows(IndexOutOfBoundsException.class, () -> {
           cardState.faceUpCard(-1);
          });}
    
    @Test
    public void faceUpCardThrowsExceptionWhenSlotIsOutOfBounds2(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        PublicCardState cardState = new PublicCardState(list, 4, 5);
        assertThrows(IndexOutOfBoundsException.class, () -> {
           cardState.faceUpCard(5);
          });}
    
    @Test
    public void deckSizeWorks(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        PublicCardState cardState = new PublicCardState(list, 4, 5);
        assertEquals(4, cardState.deckSize());
    }
    
    @Test
    public void isDeckEmptyWorksWhenFull(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        PublicCardState cardState = new PublicCardState(list, 4, 5);
        assertEquals(false, cardState.isDeckEmpty());
    }
    
    @Test
    public void isDeckEmptyWorksWhenEmpty(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        PublicCardState cardState = new PublicCardState(list, 0, 5);
        assertEquals(true, cardState.isDeckEmpty());
    }
    
    @Test
    public void discardsSizeWorks(){
        List<Card> list = List.of(Card.BLUE, Card.GREEN, Card.BLACK, Card.VIOLET, Card.LOCOMOTIVE);
        PublicCardState cardState = new PublicCardState(list, 4, 5);
        assertEquals(5, cardState.discardsSize());
    }
}
