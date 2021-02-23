package ch.epfl.tchu;

public final class Preconditions {
    private Preconditions() {}  // non-instantiable

    public static void checkArgument(boolean shouldBeTrue) {
        if(!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
