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

    private final int length;
    private final Station station1;
    private final Station station2;
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

        if(routes == null || routes.size() == 0){
            return new Trail(null, null, 0, null);
        }

        /**
         *  Créons une liste de trails, contenant chaque route seule, faire de même avec leur inverses
         */
        List<Trail> listOfTrails = new ArrayList<>();
        for(Route a : routes){
            listOfTrails.add(new Trail(a.station1(), a.station2(), a.length(), Collections.singletonList(a)));
            listOfTrails.add(new Trail(a.station2(), a.station1(), a.length(), Collections.singletonList(new Route(a.id(), a.station2(), a.station1(), a.length(), a.level(), a.color()))));
        }

        //  --- --- --- Stockage de données --- --- ---

        Trail storagePotentialTrail = new Trail(null, null, 0, null);   // if one trail with length > longestTrail, then remove all and add the latest one, if == just add
        int longestTrailLength = 0;

        //  --- --- --- --- --- --- --- --- --- --- ---

        /**
         *  Premier cas : toutes les routes sont déconnectées entre elles, dans ce cas, on choisit la plus longue route
         */
        for(Route r: routes){
            if(r.length() > longestTrailLength){
                longestTrailLength = r.length();
                storagePotentialTrail = new Trail(r.station1(), r.station2(), r.length(), Collections.singletonList(r));
            }
        }

        /**
         *  Les autres cas: au moins deux routes sont connectées
         */
        while(listOfTrails != null) {

            /**
             *  Liste des trails contenant les routes ajoutées (s'il y en a)
             */
            List<Trail> tempListOfTrails = new ArrayList<>();

            /**
             *  Le but ici est de savoir si on peut 'prolonger' chaque trail contenant une route de départ.
             */
            for(Trail singleRouteTrail : listOfTrails){ // goal here is just to find we can 'prolonger' for each starting route and if it is larger than the saved list of trails, erase everything and add what we found to the memory

                /**
                 *  Liste où on ajoutera les routes pouvant prolonger la trail actuelle
                 */
                List<Route> otherPossibleRoutes = new ArrayList<>();

                /**
                 *  Itérer sur toutes les routes, pour savoir lesquelles peuvent prolonger la trail actuelle
                 */
                for(Route testedRoute: routes) {
                    if(testedRoute.station1() == singleRouteTrail.station2() && singleRouteTrail.doesNotContainTheRoute(testedRoute)) {
                             otherPossibleRoutes.add(testedRoute);
                    }
                    if(testedRoute.station2() == singleRouteTrail.station2() && singleRouteTrail.doesNotContainTheRoute(testedRoute)){
                        otherPossibleRoutes.add(new Route(testedRoute.id(), testedRoute.station2(), testedRoute.station1(), testedRoute.length(), testedRoute.level(), testedRoute.color()));
                    }
                }

                /**
                 *  Si il y a des routes qui peuvent rallonger le trail
                 */
                if(otherPossibleRoutes.size() > 0){
                    for(Route r: otherPossibleRoutes) {
                        List<Route> previousList = new ArrayList<>();
                        previousList.addAll(singleRouteTrail.listOfRoutesInTrail);
                        previousList.add(r);

                        Trail newTrail = new Trail(previousList.get(0).station1(), previousList.get(previousList.size()-1).station2(), singleRouteTrail.length() + r.length(), previousList);

                        /**
                         *  On ajoute la nouvelle trail à la liste des trails, si la trail est de longueur supérieur à celle(s) stockée(s) en mémoire, on supprime les trails stockées et rajoute la nouvelle plus longue trail
                         *  Si la trail est de même longueur que les plus longues, on peut l'ajouter au stockage
                         */
                        tempListOfTrails.add(newTrail);
                        if(newTrail.length() > longestTrailLength){
                            longestTrailLength = newTrail.length();
                            storagePotentialTrail = newTrail;
                        }
                    }
                }
            }
            /**
             *   On veut itérer sur les nouvelles trails le prochain tour, ou sortir de la boucle s'il n'y a plus de trails prolongé
             */
            listOfTrails = (tempListOfTrails.size() == 0) ? null : tempListOfTrails;    // On veut itérer sur les nouvelles trails le prochain tour
        }

        return storagePotentialTrail;

    }

    public int length() {
        return length;
    }

    /**
     * @return  la première gare du chemin ou null si la longueur du chemin vaut 0
     */
    public Station station1() {
        return (length == 0) ? null : station1;
    }

    /**
     * @return  la dernière gare du chemin ou null si la longueur du chemin vaut 0
     */
    public Station station2() {
        return (length == 0) ? null : station2;
    }


    @Override
    public String toString() {
        if(length() == 0 || station1() == null || station2() == null || listOfRoutesInTrail == null){
            return "(0)";
        }else{
            StringBuilder str = new StringBuilder();
            for(Route a : listOfRoutesInTrail){
                str.append(a.station1().name() + " - ");
            }
            str.append(listOfRoutesInTrail.get(listOfRoutesInTrail.size()-1).station2().name() + " (" + length() + ")");    //TODO quelle est la différence entre ça et le truc du bas ?

//            String trail = "";
//            for(Route a : listOfRoutesInTrail){
//                trail = trail + a.station1().name() + " - "; // toutes les première stations
//            }
//            trail = trail + listOfRoutesInTrail.get(listOfRoutesInTrail.size()-1).station2().name() + " (" + length() + ")";  // le dernier deuxième station
//            return trail;
            return str.toString();
        }
    }

    /**
     * @param route
     * @return true si la route ne fait pas partie du trail
     */
    private boolean doesNotContainTheRoute(Route route){
        boolean notHere = true;
        for(Route routes : listOfRoutesInTrail){
            if(route.id() == routes.id()){
                notHere = false;
                break;
            }
        }
        return notHere;
    }

}
