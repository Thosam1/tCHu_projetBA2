package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;

/**
 * Classe regroupants les principales constantes qui définissent le jeu
 */
public final class Constants {
    private Constants() {
    }

    /**
     * Séparateur passer aux serdes pour la serialisation et deserialisation de
     * List et SortedBag
     */
    public static final Character SEPARATOR_LIST = ',';

    /**
     * Séparateur passer aux serdes pour la serialisation et deserialisation de
     * List de SortedBag
     */
    public static final Character SEPARATOR_LIST_SORTED_BAG = ';';

    /**
     * Séparateur passer aux serdes pour la serialisation et deserialisation des
     * trois composites: PublicCardState, PublicPlayerState et PlayerState C'est
     * la meme valeur que SEPARATOR_LIST_SORTED_BAG mais en string car nous
     * passons directement ce separateur à la méthode join qui prend un String
     */
    public static final String SEPARATOR_LIST_COMPOSITE = ";";

    /**
     * Séparateur passer aux serdes pour la serialisation et deserialisation de
     * composite de composite, autrement dit pour PublicGameState
     */
    public static final String SEPARATOR_PUBLIC_GAME_STATE = ":";

    /**
     * Taille des ArrayBlockingQueue de Graphical
     */
    public static final int ARRAY_BLOCKING_QUEUE_SIZE = 1;

    /**
     * Nombre maximum de messages qui peuvent etre affichés dans le
     * InfoViewCreator
     */
    public static final int MAXIMUM_NUMBER_VISIBLE_INFO = 5;

    /**
     * Valeur àl'initialisation des IntegerProperty dans ObservableGameState
     */
    public static final int INITIAL_VALUE_OF_INTEGER_PROPERTY = 0;

    /**
     * Nombre de wagons à partir duquel la partie entre dans le dernier tour
     */
    public static final int LAST_TURN_BEGINS_CAR_COUNT = 2;

    /**
     * Nombre de cartes wagon de chaque couleur.
     */
    public static final int CAR_CARDS_COUNT = 12;

    /**
     * Nombre de cartes locomotive.
     */
    public static final int LOCOMOTIVE_CARDS_COUNT = 14;

    /**
     * Nombre total de cartes wagon/locomotive.
     */
    public static final int TOTAL_CARDS_COUNT = LOCOMOTIVE_CARDS_COUNT
            + CAR_CARDS_COUNT * Color.COUNT;

    /**
     * Ensemble de toutes les cartes (110 au total).
     */
    public static final SortedBag<Card> ALL_CARDS = computeAllCards();

    private static SortedBag<Card> computeAllCards() {
        var cardsBuilder = new SortedBag.Builder<Card>();
        cardsBuilder.add(LOCOMOTIVE_CARDS_COUNT, Card.LOCOMOTIVE);
        for (Card card : Card.CARS)
            cardsBuilder.add(CAR_CARDS_COUNT, card);
        assert cardsBuilder.size() == TOTAL_CARDS_COUNT;
        return cardsBuilder.build();
    }

    /**
     * Numéro d'emplacement fictif désignant la pioche de cartes.
     */
    public static final int DECK_SLOT = -1;

    /**
     * Liste de tous les numéros d'emplacements de cartes face visible.
     */
    public static final List<Integer> FACE_UP_CARD_SLOTS = List.of(0, 1, 2, 3,
            4);

    /**
     * Nombre d'emplacements pour les cartes face visible.
     */
    public static final int FACE_UP_CARDS_COUNT = FACE_UP_CARD_SLOTS.size();

    /**
     * Nombre de billets distribués à chaque joueur en début de partie.
     */
    public static final int INITIAL_TICKETS_COUNT = 5;

    /**
     * Nombre de cartes distribuées à chaque joueur en début de partie.
     */
    public static final int INITIAL_CARDS_COUNT = 4;

    /**
     * Nombre de wagons dont dispose chaque joueur en début de partie.
     */
    public static final int INITIAL_CAR_COUNT = 40;

    /**
     * Nombre de billets tirés à la fois en cours de partie.
     */
    public static final int IN_GAME_TICKETS_COUNT = 3;

    /**
     * Nombre maximum de billets qu'un joueur peut défausser lors d'un tirage.
     */
    public static final int DISCARDABLE_TICKETS_COUNT = 2;

    /**
     * Nombre de cartes à tirer lors de la construction d'un tunnel.
     */
    public static final int ADDITIONAL_TUNNEL_CARDS = 3;

    /**
     * Nombre de points obtenus pour la construction de routes de longueur 1 à
     * 6. (L'élément à l'index i correspond à une longueur de route i. Une
     * valeur invalide est placée à l'index 0, car les routes de longueur 0
     * n'existent pas).
     */
    public static final List<Integer> ROUTE_CLAIM_POINTS = List
            .of(Integer.MIN_VALUE, 1, 2, 4, 7, 10, 15);

    /**
     * Longueur minimum d'une route.
     */
    public static final int MIN_ROUTE_LENGTH = 1;

    /**
     * Longueur maximum d'une route.
     */
    public static final int MAX_ROUTE_LENGTH = ROUTE_CLAIM_POINTS.size() - 1;

    /**
     * Nombre de points bonus obtenus par le(s) joueur(s) disposant du plus long
     * chemin.
     */
    public static final int LONGEST_TRAIL_BONUS_POINTS = 10;
}
