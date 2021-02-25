package ch.epfl.tchu;

/**
 * class Preconditions :
 *      pour vérifier les préconditions
 */
public final class Preconditions {
    private Preconditions() {}  // non-instanciable

    /**
     * vérifie si la condition en paramètre est vraie, sinon lance une erreur
     * @param shouldBeTrue
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if(!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
