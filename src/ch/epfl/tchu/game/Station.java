package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class Station { //immuable ?

    private int id; // between 0-50 - maybe use a map ? a name and a tag for countries ?
    private String name;

    Station(int id, String name){
        this.id = id;
        this.name = name;           //lever une IllegalArgumentException <0
        Preconditions.checkArgument(id > 0);
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
