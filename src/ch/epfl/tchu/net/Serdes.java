package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Classe contenant des attributs static public qui permettent de serialiser ou
 * deserialiser des messages (grace aux appels des méthodes serialize et
 * deserialize qui sont spécifiques à chaque Serde)
 * 
 * @author Aymeric de chillaz (326617)
 */
public final class Serdes {

    /**
     * Serde pour les Integer et les String sont créés en passant deux lambdas à
     * la méthode of de Serde
     */
    public static final Serde<Integer> SERDE_INTEGER = Serde
            .of(i -> Integer.toString(i), Integer::parseInt);

    public static final Serde<String> SERDE_STRING = Serde.of(
            i -> Base64.getEncoder()
                    .encodeToString(i.getBytes(StandardCharsets.UTF_8)),
            j -> new String(Base64.getDecoder().decode(j),
                    StandardCharsets.UTF_8));

    /**
     * Serde pour les valeurs d'un ensemble énuméré Serde retournés par la
     * méthode oneOf de Serde qui prend en argument la liste de toutes les
     * valeurs de l'ensemble
     * 
     */
    public static final Serde<PlayerId> SERDE_PLAYER_ID = Serde
            .oneOf(PlayerId.ALL);

    public static final Serde<TurnKind> SERDE_TURN_KIND = Serde
            .oneOf(TurnKind.ALL);

    public static final Serde<Card> SERDE_CARD = Serde.oneOf(Card.ALL);

    public static final Serde<Route> SERDE_ROUTE = Serde.oneOf(ChMap.routes());

    public static final Serde<Ticket> SERDE_TICKET = Serde
            .oneOf(ChMap.tickets());

    /**
     * Serde de listes et/ou de SortedBag, retournés par les méthodes listOf et
     * bagOf de Serde
     */
    public static final Serde<List<String>> SERDE_LIST_OF_STRING = Serde
            .listOf(SERDE_STRING, Constants.SEPARATOR_LIST);

    public static final Serde<List<Card>> SERDE_LIST_OF_CARD = Serde
            .listOf(SERDE_CARD, Constants.SEPARATOR_LIST);

    public static final Serde<List<Route>> SERDE_LIST_OF_ROUTE = Serde
            .listOf(SERDE_ROUTE, Constants.SEPARATOR_LIST);

    public static final Serde<SortedBag<Card>> SERDE_SORTED_BAG_OF_CARD = Serde
            .bagOf(SERDE_CARD, Constants.SEPARATOR_LIST);

    public static final Serde<SortedBag<Ticket>> SERDE_SORTED_BAG_OF_TICKET = Serde
            .bagOf(SERDE_TICKET, Constants.SEPARATOR_LIST);

    public static final Serde<List<SortedBag<Card>>> SERDE_LIST_OF_SORTED_BAG_OF_CARD = Serde
            .listOf(SERDE_SORTED_BAG_OF_CARD,
                    Constants.SEPARATOR_LIST_SORTED_BAG);

    /**
     * Serde pour les valeurs de types composites
     */
    public static final Serde<PublicCardState> SERDE_PUBLIC_CARD_STATE = new Serde<PublicCardState>() {
        @Override
        public String serialize(PublicCardState objet) {
            return String.join(Constants.SEPARATOR_LIST_COMPOSITE,
                    SERDE_LIST_OF_CARD.serialize(objet.faceUpCards()),
                    SERDE_INTEGER.serialize(objet.deckSize()),
                    SERDE_INTEGER.serialize(objet.discardsSize()));
        }

        @Override
        public PublicCardState deserialize(String string) {
            String[] stringListe = string.split(
                    Pattern.quote(Constants.SEPARATOR_LIST_COMPOSITE), -1);
            List<Card> faceUpCards = SERDE_LIST_OF_CARD
                    .deserialize(stringListe[0]);
            int deckSize = SERDE_INTEGER.deserialize(stringListe[1]);
            int discardsSize = SERDE_INTEGER.deserialize(stringListe[2]);
            return new PublicCardState(faceUpCards, deckSize, discardsSize);
        }

    };

    public static final Serde<PublicPlayerState> SERDE_PUBLIC_PLAYER_STATE = new Serde<PublicPlayerState>() {
        @Override
        public String serialize(PublicPlayerState objet) {
            return String.join(Constants.SEPARATOR_LIST_COMPOSITE,
                    SERDE_INTEGER.serialize(objet.ticketCount()),
                    SERDE_INTEGER.serialize(objet.cardCount()),
                    SERDE_LIST_OF_ROUTE.serialize(objet.routes()));
        }

        @Override
        public PublicPlayerState deserialize(String string) {
            String[] stringListe = string.split(
                    Pattern.quote(Constants.SEPARATOR_LIST_COMPOSITE), -1);
            int ticketCount = SERDE_INTEGER.deserialize(stringListe[0]);
            int cardCount = SERDE_INTEGER.deserialize(stringListe[1]);
            List<Route> routes = SERDE_LIST_OF_ROUTE
                    .deserialize(stringListe[2]);
            return new PublicPlayerState(ticketCount, cardCount, routes);
        }

    };

    public static final Serde<PlayerState> SERDE_PLAYER_STATE = new Serde<PlayerState>() {
        @Override
        public String serialize(PlayerState objet) {
            return String.join(Constants.SEPARATOR_LIST_COMPOSITE,
                    SERDE_SORTED_BAG_OF_TICKET.serialize(objet.tickets()),
                    SERDE_SORTED_BAG_OF_CARD.serialize(objet.cards()),
                    SERDE_LIST_OF_ROUTE.serialize(objet.routes()));
        }

        @Override
        public PlayerState deserialize(String string) {
            String[] stringListe = string.split(
                    Pattern.quote(Constants.SEPARATOR_LIST_COMPOSITE), -1);

            SortedBag<Ticket> tickets = SERDE_SORTED_BAG_OF_TICKET
                    .deserialize(stringListe[0]);
            SortedBag<Card> cards = SERDE_SORTED_BAG_OF_CARD
                    .deserialize(stringListe[1]);
            List<Route> routes = SERDE_LIST_OF_ROUTE
                    .deserialize(stringListe[2]);
            return new PlayerState(tickets, cards, routes);
        }
    };

    public static final Serde<PublicGameState> SERDE_PUBLIC_GAME_STATE = new Serde<PublicGameState>() {

        @Override
        public String serialize(PublicGameState objet) {
            return String.join(Constants.SEPARATOR_PUBLIC_GAME_STATE,
                    SERDE_INTEGER.serialize(objet.ticketsCount()),
                    SERDE_PUBLIC_CARD_STATE.serialize(objet.cardState()),
                    SERDE_PLAYER_ID.serialize(objet.currentPlayerId()),
                    SERDE_PUBLIC_PLAYER_STATE
                            .serialize(objet.playerState(PlayerId.PLAYER_1)),
                    SERDE_PUBLIC_PLAYER_STATE
                            .serialize(objet.playerState(PlayerId.PLAYER_2)),
                    SERDE_PLAYER_ID.serialize(objet.lastPlayer()));
        }

        @Override
        public PublicGameState deserialize(String string) {
            String[] stringListe = string.split(
                    Pattern.quote(Constants.SEPARATOR_PUBLIC_GAME_STATE), -1);

            int ticketsCount = SERDE_INTEGER.deserialize(stringListe[0]);
            PublicCardState cardState = SERDE_PUBLIC_CARD_STATE
                    .deserialize(stringListe[1]);
            PlayerId currentPlayerId = SERDE_PLAYER_ID
                    .deserialize(stringListe[2]);
            PublicPlayerState playerState1 = SERDE_PUBLIC_PLAYER_STATE
                    .deserialize(stringListe[3]);
            PublicPlayerState playerState2 = SERDE_PUBLIC_PLAYER_STATE
                    .deserialize(stringListe[4]);
            PlayerId lastPlayer = SERDE_PLAYER_ID.deserialize(stringListe[5]);

            Map<PlayerId, PublicPlayerState> playerState = Map.of(
                    PlayerId.PLAYER_1, playerState1, PlayerId.PLAYER_2,
                    playerState2);
            return new PublicGameState(ticketsCount, cardState, currentPlayerId,
                    playerState, lastPlayer);
        }

    };

}
