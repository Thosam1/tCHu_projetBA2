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

    //liste immuable
    public static final List<Color> ALL = List.of(Color.values());  // contient toutes les valeurs du type enum, dans leur ordre de def
    public static final int COUNT = Color.ALL.size();   //contient le nombre de valeurs du type enum

}
