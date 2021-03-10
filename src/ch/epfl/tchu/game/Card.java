package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Thösam Norlha-Tsang (330163)
 * class enum Card, représente les différents type de cartes du jeu (8 wagons et locomotive)
 */
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

    public static final List<Card> ALL = List.of(Card.values());    // toutes les valeurs du type enum dans l'ordre
    
    public static final int COUNT = Card.ALL.size();    //  le nombre total de valeurs du type enum

    private Color color;  // enregistrer la couleur à la création

    private Card(Color color) { this.color = color;}  // constructeur privé     // if null -> locomotive

    public static final List<Card> CARS = List.of(Card.values()).subList(0, COUNT-1);   // liste des valeurs (wagons) dans l'ordre sans la locomotive

    /**
     * @param color
     * @return le type de carte wagon correspondant à la couleur donnée
     */
    public static Card of(Color color) {
        if(color == null) {
            Card actual = Card.valueOf(LOCOMOTIVE.toString());
            actual.color = color;
//            System.out.println(actual);
            return actual;
        }else{
            Card actual = Card.valueOf(color.toString());
            actual.color = color;
            return actual;
        }
    }

    /**
     * @return  retourne la couleur du type de carte auquel on l'applique s'il s'agit d'un type wagon, ou null s'il s'agit du type locomotive
     */
    public Color color() {
        return color;
    }   // null if locomotive


}
