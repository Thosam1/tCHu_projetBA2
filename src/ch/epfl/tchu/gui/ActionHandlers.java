package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 *  Contient cinq interfaces fonctionnelles imbriquées représentant différents «gestionnaires d'actions».
 */
public interface ActionHandlers {

    public interface DrawTicketsHandler{
        /**
         * appelée lorsque le joueur désire tirer des billets
         */
        public abstract void onDrawTickets();    //TODO les types de retour ? void ?
    }
    public interface DrawCardHandler{
        /**
         *  prenant un numéro d'emplacement (0 à 4, ou -1 pour la pioche)
         *  appelée lorsque le joueur désire tirer une carte de l'emplacement donné
         */
        public abstract void onDrawCard(int a);
    }
    public interface ClaimRouteHandler{
        /**
         *  appelée lorsque le joueur désire s'emparer de la route donnée au moyen
         *  des cartes (initiales) données
         * @param route
         * @param cards
         */
        public abstract void onClaimRoute(Route route, SortedBag<Card> cards);   //TODO est-ce que les cartes initiales données sont le sortedBag? (multiensenble)
    }
    public interface ChooseTicketsHandler{
        /**
         * appelée lorsque le joueur a choisi de garder les billets donnés suite à un tirage de billets
         * @param tickets
         */
        public abstract void onChooseTickets(SortedBag<Ticket> tickets);
    }
    public interface ChooseCardsHandler{
        /**
         * appelée lorsque le joueur a choisi d'utiliser les cartes données comme cartes initiales
         * ou additionnelles lors de la prise de possession d'une route; s'il s'agit de cartes additionnelles,
         * alors le multiensemble peut être vide, ce qui signifie que le joueur renonce à s'emparer du tunnel
         * @param cards
         */
        public abstract void onChooseCards(SortedBag<Card> cards);
    }

}
