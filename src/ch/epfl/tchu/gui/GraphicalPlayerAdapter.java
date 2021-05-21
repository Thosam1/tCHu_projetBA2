package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import javafx.application.Platform;

/**
 * @author Aymeric de chillaz (326617)
 * 
 *         Cette classe a pour but d'adapter (au sens du patron Adapter) une
 *         instance de graphicalPlayer en une valeur de type Player Néanmoins,
 *         contrairement au patrong Adapter, cette classe construit elle meme
 *         l'instance de la classe qu'elle adapte Toutes ces méthodes sont
 *         destinées à etre exécutée par un fil d'exécution différent du fil
 *         JavaFX
 */
public final class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer;    //todo

    /**
     * Les méthodes de GraphicalplayerAdapter et les ActionHandlers en attribut
     * manipulent les Blocking Queue Ceci permet d'avoir une interaction entre
     * le fil JavaFX et un autre fil
     */
    private final BlockingQueue<SortedBag<Ticket>> qTickets = new ArrayBlockingQueue<>(
            Constants.ARRAY_BLOCKING_QUEUE_SIZE);
    private final BlockingQueue<Integer> qCardIndex = new ArrayBlockingQueue<>(
            Constants.ARRAY_BLOCKING_QUEUE_SIZE);
    private final BlockingQueue<SortedBag<Card>> qCards = new ArrayBlockingQueue<>(
            Constants.ARRAY_BLOCKING_QUEUE_SIZE);
    private final BlockingQueue<Route> qRoute = new ArrayBlockingQueue<>(
            Constants.ARRAY_BLOCKING_QUEUE_SIZE);
    private final BlockingQueue<TurnKind> qTurnKind = new ArrayBlockingQueue<>(
            Constants.ARRAY_BLOCKING_QUEUE_SIZE);

    /**
     * les 5 handlers qui sont instanciés dans le constructeur
     */
    private final ActionHandlers.ChooseTicketsHandler chooseTicketHandler;
    private final ActionHandlers.DrawTicketsHandler drawTicketsHandler;
    private final ActionHandlers.DrawCardHandler drawCardsHandler;
    private final ActionHandlers.ClaimRouteHandler claimRouteHandler;
    private final ActionHandlers.ChooseCardsHandler chooseCardsHandler;

    /**
     * construit les instances de ActionHandlers et les stocke en attribut. Leurs
     * unique méthode manipule les BlockingQueue
     */
    public GraphicalPlayerAdapter() {
        // créé les handlers
        chooseTicketHandler = ticketsHandler -> {
            try {
                qTickets.put(ticketsHandler);
            } catch (InterruptedException e) {
                throw new Error();
            }
        };

        drawTicketsHandler = () -> {
            try {
                qTurnKind.put(TurnKind.DRAW_TICKETS);
                // pas d'argument à ajouter à une BlockingQueue
            } catch (InterruptedException e) {
                throw new Error();
            }
        };

        drawCardsHandler = a -> {
            try {
                qCardIndex.put(a);
                qTurnKind.put(TurnKind.DRAW_CARDS);
            } catch (InterruptedException e) {
                throw new Error();
            }
        };

        claimRouteHandler = (route, cards) -> {
            try {
                qRoute.put(route);
                qCards.put(cards);
                qTurnKind.put(TurnKind.CLAIM_ROUTE);
            } catch (InterruptedException e) {
                throw new Error();
            }
        };

        chooseCardsHandler = cards -> {
            try {
                qCards.put(cards);
            } catch (InterruptedException e) {
                throw new Error();
            }
        };
    }

    /**
     * Les seules méthodes public sont celles offertes par Player
     */

    /**
     * construit sur le fil JavaFX, l'instance du joueur graphique
     * (GraphicalPlayer) que cette classe adapte
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        Platform.runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId,
                playerNames));

    }

    /**
     * appelle sur le fil JavaFX la méthode du meme nom du joueur graphique
     */
    @Override
    public void receiveInfo(String info) {
        Platform.runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * appelle sur le fil JavaFX la méthode setState du joueur graphique
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        Platform.runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * appele sur le fil JavaFX la méthode chooseTickets du joueur graphique
     * pour demander de choisir les billets initiaux en lui passant un
     * gestionnaire de choix le choix du joueur est stocké dans une file
     * bloquante
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        Platform.runLater(() -> graphicalPlayer.chooseTickets(tickets,
                chooseTicketHandler));
    }

    /**
     * bloque en attendant que la file utilisée également par
     * setInitialTicketChoice contienne une valeur, puis la retourne
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return take(qTickets);
    }

    /**
     * appelle, sur le fil JavaFX, la méthode startTurn du joueur graphique, en
     * lui passant des gestionnaires d'action qui placent le type de tour
     * choisi, de même que les éventuels «arguments» de l'action puis bloque en
     * attendant qu'une valeur soit placée dans la file contenant le type de
     * tour, qu'elle retire et retourne,
     */
    @Override
    public TurnKind nextTurn() {
        Platform.runLater(() -> graphicalPlayer.startTurn(drawTicketsHandler,
                drawCardsHandler, claimRouteHandler));
        return take(qTurnKind);
    }

    /**
     * enchaîne les actions effectuées par setInitialTicketChoice et
     * chooseInitialTickets
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        Platform.runLater(() -> graphicalPlayer.chooseTickets(options,
                chooseTicketHandler));
        return take(qTickets);
    }

    /**
     * teste (sans bloquer!) si la file contenant les emplacements des cartes
     * contient une valeur; si c'est le cas, cela signifie que drawSlot est
     * appelée pour la première fois du tour, et que le gestionnaire installé
     * par nextTurn a placé l'emplacement de la première carte tirée dans cette
     * file, qu'il suffit donc de retourner;
     * 
     * sinon, cela signifie que drawSlot est appelée pour la seconde fois du
     * tour, afin que le joueur tire sa seconde carte, et il faut donc appeler,
     * sur le fil JavaFX, la méthode drawCard du joueur graphique, avant de
     * bloquer en attendant que le gestionnaire qu'on lui passe place
     * l'emplacement de la carte tirée dans la file, qui est alors extrait et
     * retourné,
     */
    @Override
    public int drawSlot() {
        if (qCardIndex.isEmpty()) {
            Platform.runLater(() -> graphicalPlayer.drawCard(e -> {
                try {
                    qCardIndex.put(e);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }));
            return take(qCardIndex);
            
        } else {// drawSlot est appelée pour la première fois du tour
            return qCardIndex.remove();
        }
    }

    /**
     * extrait et retourne le premier élément de la file contenant les routes,
     * qui y aura été placé par le gestionnaire passé à startTurn par nextTurn,
     */
    @Override
    public Route claimedRoute() {
        return take(qRoute);
    }

    /**
     * extrait et retourne l'élément de la file contenant le multi ensemble de
     * cartes, qui y aura été placé par le gestionnaire passé à startTurn par
     * nextTurn,
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return take(qCards);
    }

    /**
     * appelle, sur le fil JavaFX, la méthode du même nom du joueur graphique
     * puis bloque en attendant qu'un élément soit placé dans la file contenant
     * les multiensembles de cartes, qu'elle retourne.
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(
            List<SortedBag<Card>> options) {
        Platform.runLater(() -> graphicalPlayer.choosedAdditionalCards(options,
                chooseCardsHandler));
        return take(qCards);
    }

    /**
     * méthode privé qui permet de ne pas répéter la meme syntaxe retourne la
     * valeur stocké dans le blockingQueue passé en argument
     */
    private <T> T take(BlockingQueue<T> q) {
        try {
            return q.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
