package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class Station { //immuable ?

    private int id; // between 0-50 - maybe use a map ? a name and a tag for countries ?
    private String name;

    public Station(int id, String name){
        Preconditions.checkArgument(id > 0);  //peut lever une IllegalArgumentException
        this.id = id;
        this.name = name; 
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
