package ch.epfl.tchu.game;


import java.util.ArrayList;
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

    public Trail (Station station1, Station station2, int length, List<Route> listOfRoutesInTrail) {
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

        if(routes == null){
            return new Trail(null, null, 0, null);
        }

        // first double the size of the list, for 'backward' routes (to interchange the stations position)
        List<Route> allRoutesPossible = new ArrayList<>();
        for(Route a : routes) {
            allRoutesPossible.add(a);
            Route opposite = new Route(a.id(), a.station2(), a.station1(), a.length(), a.level(), a.color());
            allRoutesPossible.add(opposite);
        }

        List<Trail> listOfTrails = new ArrayList<>();    // list of trails containing a single route (the starting route)
        for(Route a : allRoutesPossible){   // so we get double size
            listOfTrails.add(new Trail(a.station1(), a.station2(), a.length(), Collections.singletonList(a)));
        }

        //  --- --- --- Storage --- --- ---

        List<Trail> storagePotentialTrails = new ArrayList<>();   // if one trail with length > longestTrail, then remove all and add the latest one, if == just add
        int longestTrail = 0;

        //  --- --- ---

        while(listOfTrails != null) {

            List<Trail> tempListOfTrails = null;    // list with trails containing added routes (if there are)
//            System.out.println("list of trails : " + listOfTrails);
            for(Trail singleRouteTrail : listOfTrails){ // goal here is just to find we can 'prolonger' for each starting route and if it is larger than the saved list of trails, erase everything and add what we found to the memory

                List<Route> otherPossibleRoutes = new ArrayList<>(); // trouver les routes qui peuvent prolonger :

                for(Route test: allRoutesPossible) {   // iterate over all possible routes

                    List<Route> all = singleRouteTrail.listOfRoutesInTrail; // list of routes in actual trail


                    if(test.station1() == all.get(all.size()-1).station2()) {   // peut prolonger ?

                        // look if the route is not already there, in the list of route in "singleRouteTrail"
                        boolean here = false;
                        for(int i = 0; i < singleRouteTrail.listOfRoutesInTrail.size(); i++){
                            if(test.id() == singleRouteTrail.listOfRoutesInTrail.get(i).id()){
                                here = true;
                            }
                        }
                        if(!here){ // if not already there
                            otherPossibleRoutes.add(test);  // déjà dans la liste ?
                        }
                    }

                }

                if(otherPossibleRoutes.size() > 0){
                    if(tempListOfTrails == null){
                        tempListOfTrails = new ArrayList<>();   // this was hard to fix
                    }
                    for(Route r: otherPossibleRoutes) {

                        List<Route> previousList = new ArrayList<>(singleRouteTrail.listOfRoutesInTrail);
                        previousList.add(r);
                        int newLength = singleRouteTrail.length() + r.length();

                        Trail newTrail = new Trail(previousList.get(0).station1(), previousList.get(previousList.size()-1).station2(), newLength, previousList);
                        tempListOfTrails.add(newTrail);
//                        System.out.println("new trail : " + newTrail.toString());
                        int actualLength = newTrail.length();
                        if(actualLength == longestTrail){
                            storagePotentialTrails.add(newTrail);
                        }else if(actualLength > longestTrail){
                            longestTrail = actualLength;
                            storagePotentialTrails.clear();
                            storagePotentialTrails.add(newTrail);
                        }
                    }

//                    int count = 0;
//                    System.out.println("tempListOfTrails : " + count + " - " + tempListOfTrails);
                }


                // if we have already attained the longest, tempListOfTrails will be null

            }

            listOfTrails = tempListOfTrails;    // (if the we can't find more routes to add, the initialised list of trail will stay at "null", and we break out of the while loop)

//            System.out.println(listOfTrails);

        }

        return storagePotentialTrails.get(0);

    }

    public int length() {   // assuming a mistake in the constructor
        int length = 0;
        if(listOfRoutesInTrail != null){
            for(Route route : listOfRoutesInTrail) {
                length += route.length();
            }
        }
        return length;
    }

    /**
     * @return  la première gare du chemin ou null si la longueur du chemin vaut 0
     */
    public Station station1() {
        return (length == 0) ? null : listOfRoutesInTrail.get(0).station1();
    }// assuming a mistake in the constructor

    /**
     * @return  la dernière gare du chemin ou null si la longueur du chemin vaut 0
     */
    public Station station2() {
        return (length == 0) ? null : listOfRoutesInTrail.get(listOfRoutesInTrail.size() - 1).station2();// assuming a mistake in the constructor
    }


    @Override
    public String toString() {
        if(length() == 0 || station1() == null || station2() == null || listOfRoutesInTrail == null){    // to avoid errors
            return "(0)";
        }else{
            String trail = "";
            for(Route a : listOfRoutesInTrail){
                trail = trail + a.station1().name() + " - "; // all station 1
            }
            trail = trail + listOfRoutesInTrail.get(listOfRoutesInTrail.size()-1).station2().name() + " (" + length() + ")";  // last station2
//            System.out.println(trail);
            return trail;
        }   // is .name() relevant here ?   OR DID I MAKE A MISTAKE IN STATION CLASS
    }

}
