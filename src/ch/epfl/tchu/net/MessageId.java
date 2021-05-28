package ch.epfl.tchu.net;

import java.util.List;

/**
 * Enumeration désignant les types de messages que le serveur peut envoyer aux clients
 * @author Aymeric de chillaz (326617)
 * */
public enum MessageId {
    //ces messages correspondent directement aux méthodes de l'interface Player
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS,
    UPDATE_CHAT;//rajouté pour les appels à updateChat

    /**
     *  Liste immuable contenant toutes les valeurs du type enum MessageId, dans l'ordre
     */
    public static final List<MessageId> ALL = List.of(MessageId.values());

    /**
     *  Nombre total de valeurs dans la classe enum MessageId
     */
    public static final int COUNT = MessageId.ALL.size();

}
