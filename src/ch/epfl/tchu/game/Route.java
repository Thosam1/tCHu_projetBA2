package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

public final class Route {
    public final String id;
    public final Station station1;
    public final Station station2;
    public final int length;
    public final Level level;
    public final Color color;
    
    
    public enum Level{
        OVERGROUND,
        UNDERGROUND;
    }
    
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
    public String id() {return id;}
    public Station station1() {return station1;}
    public Station station2() {return station2;}
    public int length() {return length;}
    public Level level() {return level;}
    public Color color() {return color;}
    
    public List<Station> stations(){
        return List.of(station1, station2);
    }
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument((station.equals(station1))||(station.equals(station2)));
        return (station.equals(station1)) ? station2 : station1;
    }
    
    public List<SortedBag<Card>> possibleClaimCards(){
        
    }
    
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
    
    public int claimPoints() {return Constants.ROUTE_CLAIM_POINTS.get(length);}
}
