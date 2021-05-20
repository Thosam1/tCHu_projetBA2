package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Classe contenant des attributs static public qui permettent de serialiser ou deserialiser
 * des messages (grace aux appels des méthodes serialize et deserialize qui sont spécifiques à chaque Serde)
 * @author Aymeric de chillaz (326617)
 * */
public final class Serdes {

    
    public static final Serde<Integer> serdeInteger = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);
    
    public static final Serde<String> serdeString = Serde.of(
            i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)),
            j -> new String(Base64.getDecoder().decode(j), StandardCharsets.UTF_8));

    public static final Serde<PlayerId> serdePlayerId = Serde.oneOf(PlayerId.ALL);
    public static final Serde<TurnKind> serdeTurnKind = Serde.oneOf(TurnKind.ALL);
    public static final Serde<Card> serdeCard = Serde.oneOf(Card.ALL);
    public static final Serde<Route> serdeRoute = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> serdeTicket = Serde.oneOf(ChMap.tickets());
    
    public static final Serde<List<String>> serdeListeOfString = Serde.listOf(serdeString, ',');
    public static final Serde<List<Card>> serdeListeOfCard = Serde.listOf(serdeCard, ',');
    public static final Serde<List<Route>> serdeListeOfRoute = Serde.listOf(serdeRoute, ',');
    public static final Serde<SortedBag<Card>> serdeSortedBagOfCard = Serde.bagOf(serdeCard, ',');
    public static final Serde<SortedBag<Ticket>> serdeSortedBagOfTicket = Serde.bagOf(serdeTicket, ',');
    public static final Serde<List<SortedBag<Card>>> serdeListeOfSortedBagOfCard = Serde.listOf(serdeSortedBagOfCard, ';');
    
    
    public static final Serde<PublicCardState> serdePublicCardState = new Serde<PublicCardState>() {
        @Override
        public String serialize(PublicCardState objet) {
            Objects.requireNonNull(objet);  //ToDo ça vaut la peine de mettre ceci ici ?
            List<String> liste = new ArrayList<String>();
            liste.add(serdeListeOfCard.serialize(objet.faceUpCards()));
            liste.add(serdeInteger.serialize(objet.deckSize()));
            liste.add(serdeInteger.serialize(objet.discardsSize()));
            return String.join(";", liste);
        }

        @Override
        public PublicCardState deserialize(String string) {
            String[] stringListe = string.split(Pattern.quote(";"), -1);
            List<Card> faceUpCards = serdeListeOfCard.deserialize(stringListe[0]);
            int deckSize = serdeInteger.deserialize(stringListe[1]);
            int discardsSize = serdeInteger.deserialize(stringListe[2]);
            return new PublicCardState(faceUpCards, deckSize, discardsSize);
        }
        
    };
            
            
            
    
    public static final Serde<PublicPlayerState> serdePublicPlayerState = new Serde<PublicPlayerState>() {
        @Override
        public String serialize(PublicPlayerState objet) {
            Objects.requireNonNull(objet);  //ToDo ça vaut la peine de mettre ceci ici ?
            List<String> liste = new ArrayList<String>();
            liste.add(serdeInteger.serialize(objet.ticketCount()));
            liste.add(serdeInteger.serialize(objet.cardCount()));
            liste.add(serdeListeOfRoute.serialize(objet.routes()));
            return String.join(";", liste);
        }

        @Override
        public PublicPlayerState deserialize(String string) {
            String[] stringListe = string.split(Pattern.quote(";"), -1);
            
            int ticketCount = serdeInteger.deserialize(stringListe[0]);
            int cardCount = serdeInteger.deserialize(stringListe[1]);
            List<Route> routes = serdeListeOfRoute.deserialize(stringListe[2]);
            return new PublicPlayerState(ticketCount, cardCount, routes);
        }
    
    };


    public static final Serde<PlayerState> serdePlayerState = new Serde<PlayerState>() {
        @Override
        public String serialize(PlayerState objet) {
            Objects.requireNonNull(objet);  //ToDo ça vaut la peine de mettre ceci ici ?
            List<String> liste = new ArrayList<String>();
            liste.add(serdeSortedBagOfTicket.serialize(objet.tickets()));
            liste.add(serdeSortedBagOfCard.serialize(objet.cards()));
            liste.add(serdeListeOfRoute.serialize(objet.routes()));
            return String.join(";", liste);
        }

        @Override
        public PlayerState deserialize(String string) {
            String[] stringListe = string.split(Pattern.quote(";"), -1);
            
            SortedBag<Ticket> tickets = serdeSortedBagOfTicket.deserialize(stringListe[0]);
            SortedBag<Card> cards = serdeSortedBagOfCard.deserialize(stringListe[1]);
            List<Route> routes = serdeListeOfRoute.deserialize(stringListe[2]);
            return new PlayerState(tickets, cards, routes);
        }    
    };
    


    public static final Serde<PublicGameState> serdePublicGameState = new Serde<PublicGameState>() {

        @Override
        public String serialize(PublicGameState objet) {
            Objects.requireNonNull(objet);  //ToDo ça vaut la peine de mettre ceci ici ?
            List<String> liste = new ArrayList<String>();
            
            liste.add(serdeInteger.serialize(objet.ticketsCount()));
            liste.add(serdePublicCardState.serialize(objet.cardState()));
            liste.add(serdePlayerId.serialize(objet.currentPlayerId()));
            liste.add(serdePublicPlayerState.serialize(objet.playerState(PlayerId.PLAYER_1)));
            liste.add(serdePublicPlayerState.serialize(objet.playerState(PlayerId.PLAYER_2)));
            liste.add(serdePlayerId.serialize(objet.lastPlayer()));
            return String.join(":", liste);
        }

        @Override
        public PublicGameState deserialize(String string) {
            String[] stringListe = string.split(Pattern.quote(":"), -1);
            
            int ticketsCount = serdeInteger.deserialize(stringListe[0]);
            PublicCardState cardState = serdePublicCardState.deserialize(stringListe[1]);
            PlayerId currentPlayerId = serdePlayerId.deserialize(stringListe[2]);
            PublicPlayerState playerState1 = serdePublicPlayerState.deserialize(stringListe[3]);
            PublicPlayerState playerState2 = serdePublicPlayerState.deserialize(stringListe[4]);
            PlayerId lastPlayer = serdePlayerId.deserialize(stringListe[5]);
            
            Map<PlayerId, PublicPlayerState> playerState = Map.of(PlayerId.PLAYER_1, playerState1, PlayerId.PLAYER_2, playerState2);
            return new PublicGameState(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
        }
        
    };
    
    
    
}
