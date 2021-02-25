package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Thösam Norlha-Tsang (330163)
 * class Station : représente une gare  -   immuable
 */
public final class Station {    // immuable veut dire "final"   -> tous les attributs sont finaux

    private final int id; // between 0-50 - maybe use a map ? a name and a tag for countries ?
    private final String name;

    /**
     * Constructor
     * @param id
     * @param name
     * @throws IllegalArgumentException si le numéro d'identification est strictement négatif (<0)
     */
    public Station(int id, String name){    // const
        Preconditions.checkArgument(id >= 0);  //peut lever une IllegalArgumentException
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

    @Override
    public String toString() {
        return name;
    }
}
