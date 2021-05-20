package ch.epfl.tchu.net;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;

/**
 * Interface générique qui représente le serializer-deserializer
 * 
 * @author Aymeric de chillaz (326617)
 */
public interface Serde<T> {
    /**
     * méthode abstraite qui prend en argument l'objet à sérialiser et qui
     * retourne la chaine correspondante
     */
    public abstract String serialize(T objet);

    /**
     * méthode abstraite qui prend en argument une chaine et retourne l'objet
     * correspondant
     */
    public abstract T deserialize(String string);

    // les deux méthodes ci-dessus sont redéfinies dans les méthodes ci-dessous

    /**
     * En plus des méthodes abstraites de (dé)sérialisation, l'interface Serde
     * définit quatre méthodes statiques permettant de créer différents types de
     * serdes
     */

    /**
     * méthode générique qui permet de créer un Serde
     * 
     * @param fnSerialisation
     * @param fnDeserialisation
     * @return nouveau Serde avec une redéfinition des méthodes serialize et
     *         deserialize
     */
    public static <T> Serde<T> of(Function<T, String> fnSerialisation,
            Function<String, T> fnDeserialisation) {
        // retourne un nouveau Serde en créant une classe anonyme qui redéfinit
        // serialize et deserialize
        return new Serde<T>() {
            // Les deux fonctions passées en paramètre sont utilisées pour
            // définir serialize et deserialize (les méthodes font appel à la
            // méthode apply des Function)
            public String serialize(T object) {
                return fnSerialisation.apply(object);
            }

            public T deserialize(String string) {
                return fnDeserialisation.apply(string);
            }
        };
    }

    /**
     * @param list
     *            de toutes les valeurs d'un ensemble de valeurs énuméré
     *            (énumérations et types dont il existe un nombre restraint(ex:
     *            gares, routes etc.))
     * @return le Serde correspondant
     */
    public static <T> Serde<T> oneOf(List<T> list) {
        // retourne un nouveau Serde en créant une classe anonyme qui redéfinit
        // serialize et deserialize
        return new Serde<T>() {
            public String serialize(T object) {
                // retourne "" si objet est null
                return object == null ? ""
                        : String.valueOf(list.indexOf(object));
                // trouve l index de l'objet dans la liste et le retourne en
                // String
            }

            public T deserialize(String string) {
                // retourne null si string est vide
                return string.equals("") ? null
                        : list.get(Integer.parseInt(string));
                // sinon, retourne l'élément stocké dans liste à l'index string
            }
        };
    }

    /**
     * @param serde
     * @param separator
     *            caractère de séparation
     * @return un serde capable de (dé)sérialiser des listes de valeurs
     *         (dé)sérialisées) par le serde donné (celui est défini par une
     *         classe anonyme) La serialisation d'une liste vide retourne ""
     */
    public static <T> Serde<List<T>> listOf(Serde<T> serde,
            Character separator) {
        // Le fait d avoir separateur en Character et pas char permet d appeler
        // la méthode toString

        return new Serde<List<T>>() {
            public String serialize(List<T> objet) {
                List<String> list = new ArrayList<String>();
                objet.forEach(x -> list.add(serde.serialize(x)));
                return String.join(separator.toString(), list);
            }

            public List<T> deserialize(String string) {
                List<T> output = new ArrayList<T>();
                String[] stringList = string
                        .split(Pattern.quote(separator.toString()), -1);

                for (String element : stringList) {
                    if (!string.isEmpty()) {// si string est vide output doit
                                            // etre vide
                        output.add(serde.deserialize(element));
                    }
                }
                return output;
            }
        };
    }

    /**
     * Serialisation et deserialisation des SortedBag
     * 
     * @param serde
     * @param separator
     *            caractère de séparation
     * @return un serde capable de (dé)sérialiser des multiensembles listes de
     *         valeurs (dé)sérialisées) par le serde donné
     */
    public static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(
            Serde<T> serde, Character separator) {
        // retourne un nouveau Serde en créant une classe anonyme qui redéfinit
        // serialize et deserialize
        return new Serde<SortedBag<T>>() {
            public String serialize(SortedBag<T> sortedBag) {
                List<String> list = new ArrayList<String>();
                sortedBag.forEach(x -> list.add(serde.serialize(x)));

                return String.join(separator.toString(), list);
            }

            public SortedBag<T> deserialize(String string) {
                List<T> output = new ArrayList<T>();
                String[] stringList = string
                        .split(Pattern.quote(separator.toString()), -1);

                for (String element : stringList) {
                    if (!string.isEmpty()) {// si string est vide output doit
                                            // etre vide
                        output.add(serde.deserialize(element));
                    }
                }

                return SortedBag.of(output);
            }
        };
    }

}
