package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

/**
 * Classe Game représente une partie de tCHu. Elle n'offre qu'une seule méthode
 * publique et statique.
 * 
 * @author Thösam Norlha-Tsang (330163)
 * @author Aymeric de chillaz (326617)
 */
public abstract class Game {
    //constante désignant le nombre maximum de messages du chat qui peuvent être
    //updatés par un appel à updateChat
    private static final int MAX_NUMBER_OF_CHAT_UPDATES = 5;
    private Game() {
    }

    /**
     * fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent
     * dans la table playerNames ; les billets disponibles pour cette partie
     * sont ceux de tickets, et le générateur aléatoire rng est utilisé pour
     * créer l'état initial du jeu et pour mélanger les cartes de la défausse
     * pour en faire une nouvelle pioche quand cela est nécessaire
     * 
     * @param players
     * @param playerNames
     * @param tickets
     * @param rng
     * @throws IllegalArgumentException
     *             si l'une des deux tables associatives a une taille différente
     *             de 2
     */
    public static void play(Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets,
            Random rng) {
        Preconditions.checkArgument((players.size() == PlayerId.COUNT)
                && (playerNames.size() == PlayerId.COUNT));
        int numberOfLastTurns = 0; // Égal à 1 quand l'avant dernier joueur joue
                                   // et 2 quand le dernier joue

        Info player1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        Info player2 = new Info(playerNames.get(PlayerId.PLAYER_2));

        Map<PlayerId, Info> infoMap = Map.of(PlayerId.PLAYER_1, player1,
                PlayerId.PLAYER_2, player2);

        // Avant le début de la partie - - - - -

        /**
         * communiquer sa propre identité, et le nom de chaque joueur - le sien
         * inclus
         */
        players.forEach((c, v) -> v.initPlayers(c, playerNames));

        /**
         * permet de choisir un joueur au hasard, communiquer l'info et d
         * initialiser le GameState
         */
        GameState gameState = GameState.initial(tickets, rng);
        Game.infoToAll(players,
                infoMap.get(gameState.currentPlayerId()).willPlayFirst(), playerNames);

        /**
         * Pour chaque joueur, communiquer les billets qu'il reçoit initialement
         */
        for (Map.Entry<PlayerId, Player> c : players.entrySet()) {
            c.getValue().setInitialTicketChoice(
                    gameState.topTickets(Constants.INITIAL_TICKETS_COUNT)); // les
                                                                            // deux
                                                                            // joueurs
                                                                            // pourront
                                                                            // donc
                                                                            // consulter
                                                                            // leur
                                                                            // choix
                                                                            // en
                                                                            // parallèle,
                                                                            // et
                                                                            // même
                                                                            // utiliser
                                                                            // l'interface
                                                                            // graphique
            gameState = gameState
                    .withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        /**
         * Pour chaque joueur doit être appelée pour savoir quels billets chaque
         * joueur a décidé de garder
         */
        Game.updateStateForAll(players, gameState);

        List<Integer> nbOfInitialTickets = new ArrayList<>();

        for (Map.Entry<PlayerId, Player> c : players.entrySet()) {
            SortedBag<Ticket> chosenTickets = c.getValue()
                    .chooseInitialTickets();
            nbOfInitialTickets.add(chosenTickets.size());
            gameState = gameState.withInitiallyChosenTickets(c.getKey(),
                    chosenTickets);
        }

        /**
         * Communiquer à chacun le nombre de billets gardés par chaque joueur
         */
        Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1)
                .keptTickets(nbOfInitialTickets.get(0)), playerNames);
        Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2)
                .keptTickets(nbOfInitialTickets.get(1)), playerNames);

        /** La partie commence */
        while (numberOfLastTurns <= 2) {// le loop s'arrete quand les deux
                                        // joueurs ont joué une dernière fois

            Player currPlayerInterf = players.get(gameState.currentPlayerId()); // currentPlayerInterface
            Info currInf = infoMap.get(gameState.currentPlayerId());

            Game.infoToAll(players, currInf.canPlay(), playerNames);
            Game.updateStateForAll(players, gameState);

            /**
             * L'appel à nextTurn permet de savoir quelle action le joueur
             * courant désire effectuer parmi les trois possibles, on estime que
             * le joueur ne fait pas des choses qui lancent des exceptions
             */

            Player.TurnKind turnKind = currPlayerInterf.nextTurn();

            switch (turnKind) {
            case DRAW_TICKETS:
                /**
                 * Le joueur a décidé de piocher 3 tickets (c'est un nombre
                 * constant)
                 */
                SortedBag<Ticket> drawnTickets = gameState
                        .topTickets(Constants.IN_GAME_TICKETS_COUNT);

                // les deux joueurs sont informés du fait que trois tickets ont
                // été pioché (donc retiré de la pioche)
                Game.infoToAll(players,
                        currInf.drewTickets(Constants.IN_GAME_TICKETS_COUNT), playerNames);

                // le joueur choisi au moins un tickets parmis les trois tickets
                // qui ont été retiré de la pioche de Tickets
                SortedBag<Ticket> chosenTickets = currPlayerInterf
                        .chooseTickets(drawnTickets);

                gameState = gameState.withChosenAdditionalTickets(drawnTickets,
                        chosenTickets);

                // les deux joueurs sont informés du nombre de tickets qui ont
                // été gardé par le joueur courant
                Game.infoToAll(players,
                        currInf.keptTickets(chosenTickets.size()), playerNames);

                break;

            case DRAW_CARDS:
                /**
                 * Le joueur a décidé de piocher 2 cartes (il a le choix entre
                 * les cartes de la pioche ou entre les 5 cartes qui sont face
                 * visible)
                 */
                for (int i = 0; i < 2; i++) { // tire deux fois
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int slot = currPlayerInterf.drawSlot();

                    // Constans.DECK_SLOT est égal à -1 et signifie que le
                    // joueur veut la carte du haut du deck
                    Card pickedVisibleCard = (slot != Constants.DECK_SLOT)
                            ? gameState.cardState().faceUpCard(slot)
                            : null;

                    // gameState doit etre updaté soit en retirant la carte du
                    // haut de la pioche ou en échangeant la carte face visible
                    // sélectionné par le joueur
                    gameState = (slot == Constants.DECK_SLOT)
                            ? gameState.withBlindlyDrawnCard()
                            : gameState.withDrawnFaceUpCard(slot);

                    // Les deux joueurs sont informés du type de carte qui a été
                    // sélectionné (visible ou pioche)
                    Game.infoToAll(players, (slot == Constants.DECK_SLOT)
                            ? currInf.drewBlindCard()
                            : currInf.drewVisibleCard(pickedVisibleCard), playerNames);

                    if (i == 0) {// nous voulons une update après que la
                                 // première carte aie été tirée pour que le
                                 // joueur est accès au gameState courant avant
                                 // de faire sa deuxieme séléction
                        Game.updateStateForAll(players, gameState);
                    }
                }
                break;

            case CLAIM_ROUTE:
                /***
                 * Le joueur décide d'essayer de prendre possession d'une route
                 * Celle ci peut etre un tunnel(UNDERGROUND) ou pas (OVERGROUND)
                 * Dans le premier cas le joueur doit potentiellement poser des
                 * cartes additionnelles dans le second il prend possession de
                 * la route
                 */

                /**
                 * Si ce n est pas un tunnel ou que c'est un tunnel et qu on ne
                 * lui impose pas de cartes en plus, il prend la route et ne
                 * peux pas changer d'avis, parcontre si c'est un tunnel et qu
                 * on lui impose des cartes en plus alors il peut changer d avis
                 * mais alors ça saute son tour ce qui est géré par
                 * gameState.nextTurn() a la fin du while loop
                 */

                /**
                 * la route que le player veut est communiqué par l appel à
                 * claimedRoute et les cartes qu il veut utiliser pour s en
                 * emparer sont communiquées à travers l appel à
                 * initialClaimCards
                 */
                Route desiredRoute = currPlayerInterf.claimedRoute();
                SortedBag<Card> initialCards = currPlayerInterf
                        .initialClaimCards();

                // Si la route est un tunnel
                if (desiredRoute.level() == Route.Level.UNDERGROUND) {
                    // Les joueurs reçoivent l information que le joueur courant
                    // cherche à s'emparer d'un tunnel
                    Game.infoToAll(players, currInf
                            .attemptsTunnelClaim(desiredRoute, initialCards), playerNames);

                    SortedBag.Builder<Card> builder = new SortedBag.Builder<>();

                    // les trois cartes du haut de la pioche sont retirées et
                    // mises dans le builder pour obtenir un SortedBag avec les
                    // trois cartes du haut de la pioche
                    for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                        gameState = gameState
                                .withCardsDeckRecreatedIfNeeded(rng); // vérifie
                                                                      // que
                                                                      // l'on ne
                                                                      // retire
                                                                      // pas de
                                                                      // carte à
                                                                      // un deck
                                                                      // vide
                                                                      // (le
                                                                      // recréé
                                                                      // si il
                                                                      // est
                                                                      // vide)
                        builder.add(gameState.topCard());
                        gameState = gameState.withoutTopCard(); // retourne un
                                                                // nouveau
                                                                // gameState
                                                                // sans la carte
                                                                // du haut
                    }
                    SortedBag<Card> drawnCards = builder.build();

                    // cet appel permet de savoir combien de cartes
                    // additionnelles le joueur va devoir poser pour s'emparer
                    // de la route
                    int additionalCardsCount = desiredRoute
                            .additionalClaimCardsCount(initialCards,
                                    drawnCards);

                    Game.infoToAll(players, currInf.drewAdditionalCards(
                            drawnCards, additionalCardsCount), playerNames);

                    // Les trois cartes piochés sont rajoutés à la défausse
                    gameState = gameState.withMoreDiscardedCards(drawnCards);

                    List<SortedBag<Card>> possibleAdditionalCards = List
                            .of(SortedBag.of());

                    // si le joueur doit poser plus de cartes,
                    // possibleAdditionalCards, calcule toutes les combinaisons
                    // possible de cartes que le joueur peut poser
                    if (additionalCardsCount != 0) { // possibleAdditionalCards
                                                     // ne doit pas prendre de
                                                     // additionalCardsCount
                                                     // égal à 0
                        possibleAdditionalCards = gameState.currentPlayerState()
                                .possibleAdditionalCards(additionalCardsCount,
                                        initialCards);
                    }

                    // si le joueur a des cartes additionnelles à poser (entre 1
                    // et 3, autrement dit, différent que 0)
                    // et qu il est capable d'en poser
                    // plus(possibleAdditionalCards n'est pas vide)
                    if ((additionalCardsCount != 0)
                            && (!possibleAdditionalCards.isEmpty())) {
                        // les cartes additionnelles que le joueur décide de
                        // poser
                        SortedBag<Card> additionalCards = currPlayerInterf
                                .chooseAdditionalCards(possibleAdditionalCards);
                        SortedBag<Card> cardsUsedWithAddition = initialCards
                                .union(additionalCards);

                        // si il décide de ne pas pas poser plus de cartes ou ne
                        // peut pas plus en poser, additionalCards est vide

                        // je ne sait pas si chooseAdditionalCards peut
                        // retourner une valeur null ou vide donc je teste les
                        // deux
                        // en faisant attention de vérifier dabord que ce n'est
                        // pas null pour ne pas lancer d exception
                        if (additionalCards == null
                                || additionalCards.size() == 0) {
                            Game.infoToAll(players,
                                    currInf.didNotClaimRoute(desiredRoute), playerNames);
                        }

                        // info la route a été prise par le joueur

                        else
                            Game.infoToAll(players, currInf.claimedRoute(
                                    desiredRoute, cardsUsedWithAddition), playerNames);

                        /**
                         * si le joueur ne rajoute pas de cartes alors il garde
                         * toutes ses cartes et saute son tour (gameState ne
                         * change pas) sinon on lui retir ses cartes et il prend
                         * possession de la route
                         */
                        gameState = (additionalCards == null
                                || additionalCards.size() == 0)
                                        ? gameState
                                        : gameState.withClaimedRoute(
                                                desiredRoute,
                                                cardsUsedWithAddition);
                        Game.updateStateForAll(players, gameState);                 // Extension, mis à jour de la listview des billets indirectement
                    }

                    // si le joueur n a pas de cartes additionnelles à poser
                    // alors il s'empare de la route
                    else if (additionalCardsCount == 0) {
                        Game.infoToAll(players, currInf
                                .claimedRoute(desiredRoute, initialCards), playerNames); 
                        
                        gameState = gameState.withClaimedRoute(desiredRoute,
                                initialCards);
                        Game.updateStateForAll(players, gameState);                 // Extension, mis à jour de la listview des billets indirectement

                    } else {
                        // la route n est pas rajouté car le joueur n a pas les
                        // cartes additionelles
                        Game.infoToAll(players,
                                currInf.didNotClaimRoute(desiredRoute), playerNames);
                    }
                }

                /**
                 * La route n'est pas un tunnel donc il faut seulement prendre
                 * le controle de la route
                 */
                else {
                    Game.infoToAll(players,
                            currInf.claimedRoute(desiredRoute, initialCards), playerNames); 
                    
                    gameState = gameState.withClaimedRoute(desiredRoute,
                            initialCards);
                    Game.updateStateForAll(players, gameState);                 // Extension, mis à jour de la listview des billets indirectement
                }
                break;
            default:
                throw new Error("turnkind not valid");
            }
            // Lorsque le dernier tour commence, l'info est passé aux deux
            // joueurs
            if (gameState.lastTurnBegins()) {
                Game.infoToAll(players, currInf.lastTurnBegins(
                        gameState.currentPlayerState().carCount()), playerNames);
            }

            // incrémente numberOfLastTurns pour qu il y ait deux turns une fois
            // que lastTurnBegins rend true
            if ((gameState.lastTurnBegins()) || (numberOfLastTurns != 0)) {
                ++numberOfLastTurns;
            }
            gameState = gameState.forNextTurn();// doit etre appelé en dernier
        }

        int player1Score = gameState.playerState(PlayerId.PLAYER_1)
                .finalPoints();
        int player2Score = gameState.playerState(PlayerId.PLAYER_2)
                .finalPoints();

        Trail player1LongestTrail = Trail
                .longest(gameState.playerState(PlayerId.PLAYER_1).routes());
        Trail player2LongestTrail = Trail
                .longest(gameState.playerState(PlayerId.PLAYER_2).routes());

        switch (Integer.compare(player1LongestTrail.length(),
                player2LongestTrail.length())) {
        case 1:
            player1Score += Constants.LONGEST_TRAIL_BONUS_POINTS;
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1)
                    .getsLongestTrailBonus(player1LongestTrail), playerNames);
            break;

        case 0:
            // bonus donné aux deux joueurs
            player1Score += Constants.LONGEST_TRAIL_BONUS_POINTS;
            player2Score += Constants.LONGEST_TRAIL_BONUS_POINTS;
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_1)
                    .getsLongestTrailBonus(player1LongestTrail), playerNames);
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2)
                    .getsLongestTrailBonus(player2LongestTrail), playerNames);
            break;

        case -1:
            player2Score += Constants.LONGEST_TRAIL_BONUS_POINTS;
            Game.infoToAll(players, infoMap.get(PlayerId.PLAYER_2)
                    .getsLongestTrailBonus(player2LongestTrail), playerNames);
            break;
        default:
            break;
        }

        // on informe les joueurs du résultat final de la partie
        // afin qu ils connaissent l'état dans lequel la partie s'est terminé
        Game.updateStateForAll(players, gameState);

        switch (Integer.compare(player1Score, player2Score)) {
        case 1:
            Game.gameHasEndedToAll(players, infoMap.get(PlayerId.PLAYER_1)
                    .won(player1Score, player2Score), playerNames);
            break;
        case 0:
            Game.gameHasEndedToAll(players,
                    Info.draw(
                            List.of(playerNames.get(PlayerId.PLAYER_1),
                                    playerNames.get(PlayerId.PLAYER_2)),
                            player1Score), playerNames);
            break;
        case -1:
            Game.gameHasEndedToAll(players, infoMap.get(PlayerId.PLAYER_2)
                    .won(player2Score, player1Score), playerNames);
            break;
        default:
            break;
        }
    }

    /**
     * permet d'envoyer une information À tous les joueurs, en appelant la
     * méthode receiveInfo de chacun d'eux
     */
    private static void infoToAll(Map<PlayerId, Player> players, String info,
            Map<PlayerId, String> playerNames) {
        players.forEach((c, v) -> v.receiveInfo(info));

        /**ETAPE LIBRE*/
        updateChat(players, playerNames);
    }

    /**
     * permet d'informer tous les joueurs d'un changement d'état, en appelant la
     * méthode updateState de chacun d'eux
     */
    private static void updateStateForAll(Map<PlayerId, Player> players,
            GameState gameState) {
        players.forEach(
                (c, v) -> v.updateState(gameState, gameState.playerState(c)));
    }

    /**
     * Pour réduire la longueur de code, on a fusionné infoToAll() et l'envoi du message de fin de jeu
     * @param players
     * @param info
     * @param playerNames
     */
    private static void gameHasEndedToAll(Map<PlayerId, Player> players, String info, Map<PlayerId, String> playerNames){  // --- Extension
        players.forEach((c, v) -> v.receiveInfo(info));
        updateChat(players, playerNames);

        players.forEach((c, v) -> v.gameHasEnded(info));
    }

    /**
     * updatChat est appelé pour updater le chat des deux joueurs cette méthode
     * consiste en trois appels à updateChat
     * 
     * 
     * récupère le chat à rajouter pour le second joueur, on passe "" car nous
     * ne savons pas ce que le deuxieme joueur veut passer comme chat au premier
     * Le second permet de passer le nouveau chat au Player 2 et de récupérer
     * sont nouveau message pour le Player1 Le troisieme ne retourne pas de
     * message, il se charge de passer chatFor1 au premier joueur
     * 
     * Cette technique permet de créer un dialogue entre les joueurs. Si nous
     * voulons un dialogue quasiment instantané, il faudrait faire des appels à
     * cette méthode un peu partout dans la méthode play de Game
     * 
     * Une deuxieme possibilité est de faire un appel à updateChat à chaque fois
     * que infoToAll est appelé. (les messages ne sont pas instantanés mais le
     * dialogue est possible et la méthode play ne doit pas etre criblée
     * d'appels à cette méthode)
     * 
     * Il est important de noter que le jeu n'a pas de notion du temps et que
     * chaque appel à cette méthode permet d'updater un nombre restreint de messages
     * 
     * @param players
     *            permet d'avoir accès aux Player
     * @param playerNames
     *            permet de rajouter l'émétteur du message au message Le premier
     */
    private static void updateChat(Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {
        for (int i = 0; i < MAX_NUMBER_OF_CHAT_UPDATES; i++) {
            
            String chatFor2 = players.get(PlayerId.PLAYER_1).updateChat("",
                    true);

            String chatFor1 = players.get(PlayerId.PLAYER_2).updateChat(
                    writeMessage(playerNames.get(PlayerId.PLAYER_1), chatFor2),
                    true);

            players.get(PlayerId.PLAYER_1).updateChat(
                    writeMessage(playerNames.get(PlayerId.PLAYER_2), chatFor1),
                    false);
        }
    }

    private static String writeMessage(String playerName, String chat) {
        return (chat.isBlank()) ? ""
                : String.join(": ", List.of(playerName, chat));
    }
}
