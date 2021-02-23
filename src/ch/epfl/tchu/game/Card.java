package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;

public enum Card {

    BLACK(Color.BLACK),      // wagons
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null); // has to be at the end - null

    public static final List<Card> ALL = List.of(Card.values());
    public static final int COUNT = Card.ALL.size();

    final private Color color;

    private Card(Color color) { this.color = color;}  // if null -> locomotive

    public static final List<Card> CARS = List.of(Card.values()).subList(0, COUNT-1);   //A REVOIR

    /**
     * @param color
     * @return the type of wagon card corresponding to a color
     */
    public static Card of(Color color) {
        if(color == null) {
            return Card.valueOf(LOCOMOTIVE.toString());
        }else{
            return Card.valueOf(color.toString());
        }
    }

    /**
     * @return
     */
    public Color color() {
        return color;
    }   // null if locomotive

    //test is this working ?
}
