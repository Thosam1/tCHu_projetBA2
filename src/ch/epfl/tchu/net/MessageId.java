package ch.epfl.tchu.net;

import java.util.List;

/**
 * Enumeration désignant les messages que le serveur peut envoyer aux clients
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
    CHOOSE_ADDITIONAL_CARDS;
    
    public static final List<MessageId> ALL = List.of(MessageId.values());    
    //toutes les valeurs du type enum dans l'ordre
    
    public static final int COUNT = MessageId.ALL.size();    
    //le nombre total de valeurs du type enum

}
