package ch.epfl.tchu.game;


import java.util.List;

/**
 * @author Thösam Norlha-Tsang (330163)
 * class enum Card, représente les différents type de cartes du jeu (8 wagons de couleurs et locomotive)
 */

public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    /**
     *  Liste immuable contenant toutes les valeurs du type enum Card, dans l'ordre, wagons et locomotive
     */
    public static final List<Card> ALL = List.of(Card.values());

    /**
     *  Nombre total de valeurs dans la classe enum Card
     */
    public static final int COUNT = Card.ALL.size();

    private Color color;  // pour enregistrer la couleur à la création

    private Card(Color color) { this.color = color;}  // constructeur privé, if color == null -> locomotive

    /**
     *  Liste immuable contenant toutes les valeurs (couleur) des wagons, dans l'ordre, sans la locomotive
     */
    public static final List<Card> CARS = List.of(Card.values()).subList(0, COUNT-1);

    /**
     * @param color
     * @return le type de carte wagon correspondant à la couleur donnée
     */
    public static Card of(Color color) {
        Card card = (color == null) ? Card.valueOf(LOCOMOTIVE.toString()) : Card.valueOf(color.toString());
        card.color = color;
        return card;
    }

    /**
     * @return  retourne la couleur du type de carte auquel on l'applique s'il s'agit d'un type wagon, ou null s'il s'agit du type locomotive
     */
    public Color color() {
        return color;
    }   // null if locomotive


}
