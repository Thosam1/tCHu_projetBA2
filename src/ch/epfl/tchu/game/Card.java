package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;

public enum Card {

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE,
    LOCOMOTIVE;

    public static final List<Card> ALL = List.of(Card.values());
    public static final int COUNT = Card.ALL.size();
    public final Color couleur;
    
    /*
    private Card () {}
    
    private Card (Color couleur){
        this.couleur = couleur;
    }
    
//    final Color type;
//    Card(Color type) { this.type = type; }

  //  public static final List<Card> CARS = List.of(Card.values()).remove(List.of(Card.values()).size() - 1);   //A REVOIR
  //  public static final List<Card> CARS = Card.values().remove(Card.LOCOMOTIVE);
    
    */
    /**
     * @param color
     * @return the type of wagon card corresponding to a color
     */
    public static Card of(Color color) {
        return null;
    }

    /**
     * @return
     */
    public static Color color() {
        return null;
    }

    //test is this working ?
}
