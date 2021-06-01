package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Interface Player représente un joueur de tCHu
 * @author Thösam Norlha-Tsang (330163)
 */
public interface Player {

    public enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         *  Liste immuable contenant toutes les valeurs de la classe enum TurnKind, dans leur ordre de définition
         */
        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * appelée au début de la partie pour communiquer au joueur sa propre identité ownId, ainsi que les noms des différents joueurs, le sien inclus, qui se trouvent dans playerNames
     * @param ownId
     * @param playerNames
     */
    public abstract void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie; cette information est donnée sous la forme d'une chaîne de caractères, généralement produite par la classe Info
     * @param info
     */
    public abstract void receiveInfo(String info);

    /**
     * appelée pour updater le chat d'un joueur
     * Ajoute chatToAdd au chat du joueur courant et retourne la valeur que le joueur désir passer à l'autre joueur
     * */
    public abstract String updateChat(String chatToAdd, boolean returnAvailable);
    
    /**
     * appelée chaque fois que l'état du jeu a changé, pour informer le joueur de la composante publique de ce nouvel état, newState, ainsi que de son propre état, ownState
     * @param newState
     * @param ownState
     */
    public abstract void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * appelée au début de la partie pour communiquer au joueur les cinq billets qui lui ont été distribués
     * @param tickets
     */
    public abstract void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * appelée au début de la partie pour demander au joueur lesquels des billets qu'on lui a distribué initialement (via la méthode précédente) il garde
     * @return  billets choisi
     */
    public abstract SortedBag<Ticket> chooseInitialTickets();

    /**
     * appelée au début du tour d'un joueur, pour savoir quel type d'action il désire effectuer durant ce tour
     * @return
     */
    public abstract TurnKind nextTurn();

    /**
     * appelé à la fin du jeu, lorsque le/les gagnants sont annoncés
     */
    public abstract void gameHasEnded(String message);    // --- Extension

    /**
     * appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie, afin de lui communiquer les billets tirés et de savoir lesquels il garde
     * @param options
     * @return
     */
    public abstract SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive, afin de savoir d'où il désire les tirer: d'un des emplacements contenant une carte face visible ou de la pioche
     * @return entre 0 et 4 inclus — ou de la pioche auquel cas la valeur retournée vaut Constants.DECK_SLOT (c-à-d -1)
     */
    public abstract int drawSlot();

    /**
     * appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route il s'agit
     * @return
     */
    public abstract Route claimedRoute();

    /**
     * appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela
     * @return
     */
    public abstract SortedBag<Card> initialClaimCards();

    /**
     * appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes additionnelles sont nécessaires, afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui étant passées en argument ; si le multiensemble retourné est vide, cela signifie que le joueur ne désire pas (ou ne peut pas) choisir l'une de ces possibilités
     * @param options
     * @return
     */
    public abstract SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     *  À SUPPRIMER APRÈS IMPLEMENTATION DES METHODES :
     *  Les joueurs choisissent en MÊME temps les billets qu'ils désirent garder
     *
     *  il faut avoir à disposition les deux méthodes - setInitialTicketChoice et chooseInitialTickets — afin de pouvoir:
     *
     * communiquer à chaque joueur les billets qui lui ont été distribués initialement, au moyen de la méthode setInitialTicketChoice,
     * demander à chaque joueur les billets qu'il désire garder, au moyen de la méthode chooseInitialTickets.
     * Si une seule méthode était disponible, alors le premier joueur devrait choisir ses billets avant que le second joueur ne puisse voir les siens, ce qui ne serait pas idéal.
     */
}
