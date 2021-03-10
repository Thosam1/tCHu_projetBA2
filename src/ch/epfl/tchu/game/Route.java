package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * @return liste décrite au-dessus
     */
    public List<SortedBag<Card>> possibleClaimCards(){
        List<SortedBag<Card>> output = new ArrayList<SortedBag<Card>>();
        if(level==Level.UNDERGROUND) {
            for (int i = 0; i<length; ++i) {
                if (color() == null) {//c'est une route neutre
                    //if (Card.of(color))    
                    for(Card card : Card.ALL) {
                        if (!(card.equals(Card.LOCOMOTIVE))) {
                            output.add(SortedBag.of(length-i, card, i, Card.LOCOMOTIVE));}
                    }
                }
                //route de couleur
                else {
                    output.add(SortedBag.of(length-i, Card.of(color), i, Card.LOCOMOTIVE));
                }
                }
            //SortedBag contenant un nombre de locomotives égale à length
            output.add(SortedBag.of(length, Card.LOCOMOTIVE));
            }
        
        //Pour les routes OVERGROUND nous ne voulons pas de LOCOMOTIVES dans l'output
        else {
            if(color()==null) {
                for(Card card : Card.ALL) {
                    if (card!=Card.LOCOMOTIVE) {
                        output.add(SortedBag.of(length,card));}
                }
            }
            else {
                output.add(SortedBag.of(length, Card.of(color)));
            }
        }
        return output;
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
        /*si les cartes de drawnCards sont des Locomotive alors on ajoute une carte
        à additionalCards, sinon il faut que ces cartes soient égales à claimCardsType*/
        for (Card card : drawnCards) {
            if (card.equals(Card.LOCOMOTIVE)) {
                ++additionalCards;
            }
            else {
                if(card.equals(claimCardsType)) {
                    ++additionalCards;
                }
            }
        }
        return additionalCards;
    }
    
    /**
     * @return nombre de points de construction qu'un joueur obtient lorsqu'il s'empare de la route
     */
    public int claimPoints() {return Constants.ROUTE_CLAIM_POINTS.get(length);}
}
