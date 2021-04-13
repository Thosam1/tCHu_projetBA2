package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Thösam Norlha-Tsang (330163)
 * class enum Color, représente les 8 couleurs du jeu pour colorer les wagons
 */
public enum Color {

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     *  Liste immuable contenant toutes les valeurs/couleurs de la classe enum Color, dans leur ordre de définition
     */
    public static final List<Color> ALL = List.of(Color.values());

    /**
     *  Nombre de valeurs/couleurs dans la classe enum Color
     */
    public static final int COUNT = Color.ALL.size();


}
