package ch.epfl.tchu.game;


import java.util.Collections;
import java.util.List;

/**
 *
 * @author Thösam Norlha-Tsang (330163)
 *  représente : un chemin dans le réseau d'un joueur
 */
public final class Trail {

    private final int length;    // optional
    private final Station station1;   // optional
    private final Station station2;   // optional
    private final List<Route> listOfRoutesInTrail;    //in order

    private Trail (Station station1, Station station2, int length, List<Route> listOfRoutesInTrail) {
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.listOfRoutesInTrail = listOfRoutesInTrail;
    } // contructeur privé

    /**
     * @param routes
     * @return  le plus long chemin du réseau constitué des routes données
     *      s'il y a plusieurs chemins de longueur maximale, celui qui est retourné n'est pas spécifié
     *      si la liste des routes données est vide, retourne un chemin de longueur zéro, dont les gares sont toutes deux égales à null
     */
    public static Trail longest(List<Route> routes){

        List<Trail> listOfTrails = null;    // list of trails containing a single route (the starting route)
        for(Route a : routes){
            listOfTrails.add(new Trail(a.station1(), a.station2(), a.length(), Collections.singletonList(a)));
        }

        List<List<Route>> storagePotentialTrails = null;   // if one trail with length > longestTrail, then remove all and add the latest one, if == just add
        List<String> tempListOfIdInTrail = null;
        int longestTrail = 0;

        int trailLength = 0;

        while(listOfTrails != null) {
            List<Trail> tempListOfTrails = null;
            for(Trail singleRouteTrail : listOfTrails){
                List<Route> otherPossibleRoutes = null;
                for(Route a : routes) {

                }
            }
        }



//        while(listOfTrails != null) {
//
//            List<Route> tempList = null;
//            for(Route c : singleRoutes) {   // itérer avec pour départ une des routes (différents à chaque itération)
//                List<Route> rs = null;  // routes pouvant prolonger c
//                for(Route toR)
//
//                for(Route r : rs) {
//                    tempList.add(r);    // ajouter les prolongation
//                }
//            }
//            singleRoutes = tempList;
//        }


        if(storagePotentialTrails == null) {
            return new Trail(null, null, 0, Collections.singletonList(null));
        }else if(storagePotentialTrails.size() == 1){
            int last = storagePotentialTrails.size() - 1;
            Station firstStat = storagePotentialTrails.get(0).
                    get(0).station1();
            Station secondStat = storagePotentialTrails.get(0).
                    get(last).station2();

            return new Trail(firstStat, secondStat, longestTrail, storagePotentialTrails.get(0));
        }else{
            return new Trail(null, null, longestTrail, Collections.singletonList(null));    //  ????? s'il y a plusieurs chemins de longueur maximale, celui qui est retourné n'est pas spécifié ;
        }

    }

    public int length() { return length; }

    /**
     * @return  la première gare du chemin ou null si la longueur du chemin vaut 0
     */
    public Station station1() {
        return (length == 0) ? null : listOfRoutesInTrail.get(0).station1();
    }

    /**
     * @return  la dernière gare du chemin ou null si la longueur du chemin vaut 0
     */
    public Station station2() {
        return (length == 0) ? null : listOfRoutesInTrail.get(listOfRoutesInTrail.size() - 1).station2();
    }

    /**
     * @return  une représentation textuelle du chemin, qui doit au moins contenir le nom de la première et de la dernière gare (dans cet ordre) ainsi que la longueur du chemin entre parenthèses
     */
    @Override
    public String toString() {

        if(length == 0 || station1 == null || station2 == null){    // to avoid errors
            return "(0)";
        }else{
            String trail = "";
            for(Route a : listOfRoutesInTrail){
                trail.concat(a.station1() + " - "); // all station 1
            }
            trail.concat(listOfRoutesInTrail.get(listOfRoutesInTrail.size()-1).station2() + " (" + length + ") ");  // last station2
            return trail;
        }
    }

}
