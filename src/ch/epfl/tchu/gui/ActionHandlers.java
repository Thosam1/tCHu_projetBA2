package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 *  @author Thösam Norlha-Tsang (330163)
 *  Contient cinq interfaces fonctionnelles imbriquées représentant différents «gestionnaires d'actions».
 */
public interface ActionHandlers {

    interface DrawTicketsHandler{
        /**
         * appelée lorsque le joueur désire tirer des billets
         */
        void onDrawTickets();
    }
    interface DrawCardHandler{
        /**
         *  prenant un numéro d'emplacement (0 à 4, ou -1 pour la pioche)
         *  appelée lorsque le joueur désire tirer une carte de l'emplacement donné
         */
         void onDrawCard(int a);
    }
    interface ClaimRouteHandler{
        /**
         *  appelée lorsque le joueur désire s'emparer de la route donnée au moyen
         *  des cartes (initiales) données
         * @param route route ciblée
         * @param cards cartes (initiales) que le joueur veut utiliser pour s'emparer de la route
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }
    interface ChooseTicketsHandler{
        /**
         * appelée lorsque le joueur a choisi de garder les billets donnés suite à un tirage de billets
         * @param tickets tickets gardés
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }
    interface ChooseCardsHandler{
        /**
         * appelée lorsque le joueur a choisi d'utiliser les cartes données comme cartes initiales
         * ou additionnelles lors de la prise de possession d'une route; s'il s'agit de cartes additionnelles,
         * alors le multiensemble peut être vide, ce qui signifie que le joueur renonce à s'emparer du tunnel
         * @param cards
         */
        void onChooseCards(SortedBag<Card> cards);
    }

}
