package ch.epfl.tchu.game;

import java.util.List;

/**
 * Représente l'identité d'un joueur
 * L'enumeration contient que deux élément car tCHu se joue à deux joueurs
 * @author Aymeric de chillaz (326617)
 *
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;
    
    /**
     * le premier est une liste contenant les éléments de cette énumération
     * le deuxieme est le nombre d'éléments dans cette énumération
     */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    public static final int COUNT = PlayerId.ALL.size();
    
    /**
     * @return l'identité du joueur qui suit celui auquel on l'applique
     */
    public PlayerId next() {
        return (this.equals(PLAYER_1)) ? PLAYER_2 : PLAYER_1;
    }
}
