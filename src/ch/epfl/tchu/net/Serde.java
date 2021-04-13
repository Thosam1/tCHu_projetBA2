package ch.epfl.tchu.net;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;

/**
 * Interface générique qui représente le serializer-deserializer
    */
public interface Serde<T> {
    //Une interface ne peut pas avoir des attributs à moins qu ils soient static
    //Néanmoins, ici nous n en avons pas besoin
  //  static Function<T, String> fnSerialisation;
 //   static Function<T, String> fnDeserialisation;

  
    /**
     * méthode abstraite qui prend en argument l'objet à sérialiser et qui
     * retourne la chaine correspondante*/
    public abstract String serialize(T objet);
    
    /**
     * méthode abstraite qui prend en argument une chaine et 
     * retourne l'objet correspondant*/
    public abstract T deserialize(String string);
    
    
    /**
     * méthode générique qui permet de créer un Serde
     * @param fnSerialisation
     * @param fnDeserialisation
     * @return nouveau Serde avec une redéfinition des méthodes serialize et deserialize
     * */
    public static <T> Serde<T> of(Function<T,String> fnSerialisation, Function<String, T> fnDeserialisation){
        //retourne un nouveau Serde
        return new Serde<T>() {
            //Les deux fonctions passées en paramètre sont utilisées pour définir serialize et deserialize
            public String serialize(T objet) {
                return fnSerialisation.apply(objet);
            }
            public T deserialize(String string) {
                return fnDeserialisation.apply(string);
            }
        };
    }
        

    /**
     * @param liste de toutes les valeurs d'un ensemble de valeurs énuméré
     *  (énumérations et types dont il existe un nombre restraint(ex: gares, routes etc.))
     *  @return le Serde correspondant
     * */    
    public static <T> Serde<T> oneOf(List<T> liste){
        return new Serde<T>() {
            public String serialize(T objet) {
                return String.valueOf(liste.indexOf(objet));
                //trouve l index de l'objet dans la liste et le retourne en String
            }
            public T deserialize(String string) {
                return liste.get(Integer.parseInt(string));
                //retourne la valeur dans liste qui correspond à l index stocké dans string sous la forme de String
            }
        };
    }
    
    /**
     * @param serde
     * @param separateur caractère de séparation
     * @return un serde capable de (dé)sérialiser
     * des listes de valeurs (dé)sérialisées) par le serde donné
     * */
    public static <T> Serde<List<T>> listOf(Serde<T> serde, Character separateur){
        //Le fait d avoir separateur en Character et pas char permet d appeler la méthode toString
        //comment faire la différence entre les T et les List<T>
        return new Serde<List<T>>() {
            public String serialize(List<T> objet) {
                if(objet.isEmpty()) {
                    return "";
                }
                else {
                    List<String> liste = new ArrayList<String>();
                    objet.forEach(element -> liste.add(serde.serialize(element)));
                    return String.join(separateur.toString(), liste);
                }
        }
            public List<T> deserialize(String string) {
                if(string.isEmpty()) {
                    return List.of();
                }
                else {
                    List<T> output = new ArrayList<T>();
                    
                    String[] stringListe = string.split(Pattern.quote(separateur.toString()), -1);
                    
                    for(String element : stringListe) {
                        output.add(serde.deserialize(element));
                    }
                    return output;
                }
        }
    };
    }
    
    /**
     * @param serde
     * @param separateur caractère de séparation
     * @return un serde capable de (dé)sérialiser 
     * des multiensembles listes de valeurs (dé)sérialisées) par le serde donné
     * */
    public static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, Character separateur){
        return new Serde<SortedBag<T>>() {
            public String serialize(SortedBag<T> objetSortedBag) {
                List<T> objet = objetSortedBag.toList();
                if(objet.isEmpty()) {
                    return "";
                }
                else {
                    List<String> liste = new ArrayList<String>();
                    objet.forEach(element -> liste.add(serde.serialize(element)) );
                    
                    return String.join(separateur.toString(), liste);
                }
        }
            //Est ce que pour la désérialisation il faut retourner un SortedBag?
            public SortedBag<T> deserialize(String string) {
                if(string.isEmpty()) {
                    return SortedBag.of();
                }
                else {
                    List<T> output = new ArrayList<T>();
                    
                    String[] stringListe = string.split(Pattern.quote(separateur.toString()), -1);
                    
                    for(String element : stringListe) {
                        output.add(serde.deserialize(element));
                    }
                    return SortedBag.of(output);
                }
        }
    };
    }
}
