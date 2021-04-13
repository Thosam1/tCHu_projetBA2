package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
/**
 * Cette classe représente une partition aplatie des gares,
 * Cette partition est contenue dans le tableau de nombre entiers tableauDesLiens
 * Elle implémente l'interface StationConnectivity car ses instances
 * ont pour but d'etre passées à la méthode points de Ticket
 * @author Aymeric de chillaz (326617)
 *
 */
public final class StationPartition implements StationConnectivity{
    private final int[] tableauDesLiens;
    
    /**
     * Constructeur privé qui prend en argument le tableau d'entiers contenant
     * les liens liant chaque élément au représentant de leur sous ensemble.
     * Il est appelé dans la classe StationPartition.Builder
     * @param tableauDesLiens
     */
    private StationPartition(int[] tableauDesLiens) {
        this.tableauDesLiens = tableauDesLiens;
    }
    
    /**
     * return true si les deux stations passées en paramètre sont connectées
     *      c' est à dire qu'elles ont les même représentant de sous ensemble
     * return false sinon
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if((s1.id()>=tableauDesLiens.length)||(s2.id()>=tableauDesLiens.length)
            ||(s1.id()<0)||(s2.id()<0)) {
            //la méthode connected doit également accepter des gares dont l'identité est hors des bornes du tableau passé à son constructeur.
            //dans ce cas la elle compare directement l identite des deux stations
            return s1.id()==s2.id();
        }
        else {
            return tableauDesLiens[s1.id()]==tableauDesLiens[s2.id()];}//true si les valeurs correspondant aux deux stations sont égales, false sinon
    }

    /**
     * Classe imbriquée dans la classe StationPartition qui représente un batisseur 
     * de partition de gare
     * Construit la version profonde de la partition, qu'il aplatit dans la méthode build
     * La partition consiste en un tableau à une dimension contenant un élément par
     * Station (stationCount éléments)
     * @author Aymeric de chillaz (326617)
     */
    public static final class Builder{
        private int[] partition;
        
        /**
         * Construit un batisseur de partition d'un ensemble de gares dont l'identité
         * est comprise entre 0 (inclus) et stationCount(exclus)
         * Lève IllegalArgumentException si stationCount est strictement négatif
         * Initialement, chaque élément est égale à son index
         * @param stationCount
         */
        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount>=0);
            partition = new int[stationCount];
                
            for(int i = 0; i<stationCount; ++i) {
                partition[i] = i;//chaque élément est égal à son index dans le tableau partition
                }
            }
        
        /**
         * Méthode publique qui joint les sous ensembles contenant les deux gares
         * passées en argument
         * @param s1 la première Station
         * @param s2 la deuxième Station
         * @return le batisseur (this)
         */
        public Builder connect(Station s1, Station s2) {
            int representativeS1 = this.representative(s1.id());//représentant de la premiere station
            int representativeS2 = this.representative(s2.id());//pareil pour la deuxieme
            partition[representativeS1] = representativeS2;//pour joindre les deux sous ensembles, nous décidons que le représentant du représentant de la première station n'est plus soit meme
            return this;
            }
            
        /**
         * @return une instance de StationPartition crée à partir de la partition aplatie
         * Celle-ci est créée en associant à chaque index (d'une liste de même longueur)
         * son représentant (ce qui est retourné par la méthode representative)
         */
        public StationPartition build() {
            int[] output = new int[partition.length];
                
            for(int i = 0; i<partition.length; ++i) {
                output[i] = this.representative(i);
            }
            
            return new StationPartition(output);
        }
        
        /**
         * Cette méthode est plutot simple. Elle cherche la valeur de partition à l index
         * id et fait de meme pour cette valeur jusqu'à ce que l'index soit égal à la valeur
         * stocké dans partition
         * @param id numéro d'identification d'une gare
         * @return l'id de la gare représentant le sous ensemble dans lequel se trouve
         *         la gare correspondant à id
         */
        private int representative(int id) {
            while(partition[id]!=id) {
                id = partition[id];
            }
            return id;
            }
        
        }
}
