package ch.epfl.tchu;

import ch.epfl.tchu.game.*;
import java.util.List;
import java.util.Random;

/**
 * classe implémentant l’interface Player représentant un joueur jouant de manière aléatoire.
 * En plus de jouer une partie, ce joueur doit également mémoriser autant d’informations que
 * possible quant au déroulement de la partie, informations qui seront testées à la fin de la partie.
 */

/**
 * Pour savoir quelle action effectuer lorsque c’est son tour,
 * ce joueur peut procéder de manière très simple en :
 * 1) calculant l’ensemble de toutes les routes dont il peut s’emparer,
 * 2) si cet ensemble est vide, tirer des cartes,
 * 3) sinon, choisir au hasard une route dont il peut s’emparer, et s’en emparer.
 */
private static final class TestPlayer implements Player {
    private static final int TURN_LIMIT = 1000;

    private final Random rng;
    // Toutes les routes de la carte
    private final List<Route> allRoutes;

    private int turnCounter;
    private PlayerState ownState;
    private GameState gameState;

    // Lorsque nextTurn retourne CLAIM_ROUTE
    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;

    public TestPlayer(long randomSeed, List<Route> allRoutes) {
        this.rng = new Random(randomSeed);
        this.allRoutes = List.copyOf(allRoutes);
        this.turnCounter = 0;
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.gameState = gameState;
        this.ownState = ownState;
    }

    @Override
    public TurnKind nextTurn() {
        turnCounter += 1;
        if (turnCounter > TURN_LIMIT)
            throw new Error("Trop de tours joués !");

        // Détermine les routes dont ce joueur peut s'emparer
        List<Route> claimableRoutes = /* ... */;
        if (claimableRoutes.isEmpty()) {
            return TurnKind.DRAW_CARDS;
        } else {
            int routeIndex = rng.nextInt(claimableRoutes.size());
            Route route = claimableRoutes.get(routeIndex);
            List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

            routeToClaim = route;
            initialClaimCards = cards.get(0);
            return TurnKind.CLAIM_ROUTE;
        }
    }
}
