package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Thösam Norlha-Tsang (330163)
 * class Station : représente une gare  -   immuable
 */
public final class Station {

    private final int id;
    private final String name;

    /**
     * Constructeur
     * @param id
     * @param name
     * @throws IllegalArgumentException si le numéro d'identification est strictement négatif (<0)
     */
    public Station(int id, String name){
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name; 
    }

    /**
     * @return  the id of the station
     */
    public int id() {
        return id;
    }

    /**
     * @return  the name of the station
     */
    public String name() {
        return name;
    }

    /**
     * @return the name of the Station
     */
    @Override
    public String toString() {
        return name;
    }
}
