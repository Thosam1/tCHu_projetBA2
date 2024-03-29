package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;


/**
 * 
 * @author Aymeric de chillaz (326617)
 *
 */
public final class Route {

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

/**
 * Enumeration qui permet de savoir si une route est un tunnel ou pas
 *
 */
    public enum Level{
        OVERGROUND,
        UNDERGROUND; //tunnel
    }
    /**
     * construit les routes et vérifie que les stations ne sont pas égales
     * vérifie aussi que length est compris entre les constantes MIN_ROUTE_LENGTH et MAX_ROUTE_LENGTH
     * @param id identité de la route
     * @param station1 première gare de la route
     * @param station2 seconde gare de la route
     * @param length longueur de la route
     * @param level niveau auquel se trouve la route (i.e. tunnel ou pas)
     * @param color couleur de la route ou null s'il s'agit d'une route de couleur neutre
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!(station1.equals(station2)));
        Preconditions.checkArgument((length >= Constants.MIN_ROUTE_LENGTH) && (length <= Constants.MAX_ROUTE_LENGTH));
        
        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }
    
    
    /**
     * getter pour l'identité de la premiere route
     * @return id
     */
    public String id() {return id;}
    
    /**
     * getter pour la première gare de la route
     * @return station1
     */
    public Station station1() {return station1;}
    
    /**
     * getter pour la seconde gare de la route
     * @return station2
     */
    public Station station2() {return station2;}
    
    /**
     * getter pour la longueur de la route
     * @return length
     */
    public int length() {return length;}
    
    /**
     * getter pour le Level de la route (OVERGROUND ou UNDERGROUND)
     * @return level
     */
    public Level level() {return level;}
    
    /**
     * getter pour la couleur de la route, peut etre null s'il s'agit d'une route neutre
     * @return color
     */
    public Color color() {return color;}
    
    /**
     * méthode qui return une liste contenant la premiere gare en premiere position et la seconde en seconde position
     */
    public List<Station> stations(){
        return List.of(station1, station2);
        }
    
    /**
     * méthode qui retourne la gare de la route qui n'est pas celle donnée
     * lève une exception si station est différent des deux gares stockées en attribut
     * @param station 
     * @return station1 ou station2 dépendant de la valeur de station
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument((station.equals(station1))||(station.equals(station2)));
        return (station.equals(station1)) ? station2 : station1;
        }
    
    /**
     * retourne la liste de tous les ensembles de cartes qui pourraient etre joués pour s'emparer
     * de la route
     * Trié par ordre croissant de nombre de cartes locomotive, puis par couleur
     * Notez que si la couleur est null cela veut dire que la route est neutre et donc que toutes les couleurs
     * de cartes peuvent etre utiliser (a condition de respecter certaines règles)
     * 1) un seul type de carte wagon peut etre utilisé (meme si la route est null)
     * 2) si la route est un tunnel (UNDERGROUND) alors des locomotives peuvent etre utilisé
     * @return liste décrite au-dessus
     */
    public List<SortedBag<Card>> possibleClaimCards(){

        Set<SortedBag<Card>> output = new LinkedHashSet<>(); 
        //permet de garder l'ordre (donc pas besoin de definir de comparator)
        //et permet de ne pas avoir de doublons (important car pour les routes UNDERGROUND on rajoute listCard.size() fois un sortedbag avec que des Locomotives)
        
        
        List<Card> listCard = (color() == null) ? Card.CARS : List.of(Card.of(color));
        int index = (level == Level.UNDERGROUND) ? length : 0;
        
        for(int i = 0; i <= index; i++) {
            for(Card card : listCard) {
                output.add(SortedBag.of(length-i, card, i, Card.LOCOMOTIVE));
            }
        }
        return new ArrayList<>(output);
        
        }
    
    /**
     * retourne le nombre de cartes additionnelles à jouer pour s'emparer de la route (en tunnel)
     * lève l'exception IllegalArgumentException si la route n'est pas un tunnel ou drawnCards n'est pas égal à 3
     * @param claimCards cartes initialement posés par le joueur
     * @param drawnCards les trois cartes tirées du sommet de la pioche
     * @return une valeur int entre, 0 et 3, compris
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(level.equals(Level.UNDERGROUND));
        Card claimCardsType = Card.LOCOMOTIVE; //Locomotive par défaut
        int additionalCards = 0;
        
        for (Card claimCard : claimCards) {
            //nous voulons que claimCardsType soit égale à toute carte qui n est pas
            //une locomotive (si il y en a)
            if (!(claimCard.equals(Card.LOCOMOTIVE))){
                claimCardsType = claimCard;
            }
        }
        
        //si claimCardsType est une Locomotive, alors il faut que les drawnCards soient des Locomotives pour incrémenter additionalCards
        //si ce n'est pas une locomotive, alors toutes les cartes de drawnCards qui sont pareil que claimCardsType ou sont des locomotives incrément additionalCards
        
        
        /*si les cartes de drawnCards sont des Locomotive alors on ajoute une carte
        à additionalCards, sinon il faut que ces cartes soient égales à claimCardsType*/
        for (Card card : drawnCards) {
            if (card.equals(Card.LOCOMOTIVE) || card.equals(claimCardsType)) {
                ++additionalCards;
            }
        }
        return additionalCards;
    }
    
    /**
     * @return nombre de points de construction qu'un joueur obtient lorsqu'il s'empare de la route
     */
    public int claimPoints() {return Constants.ROUTE_CLAIM_POINTS.get(length);}
}
