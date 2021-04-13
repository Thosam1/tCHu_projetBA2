package ch.epfl.tchu.game;

/**
 * @author Thösam Norlha-Tsang (330163)
 * interface StationConnectivity : représente la "connectivité" du réseau d'un joueur
 */
public interface StationConnectivity {
    /**
     * @param s1    première station
     * @param s2    deuxième station
     * @return     true si les deux sont reliées
     */
    public abstract boolean connected(Station s1, Station s2);

}
