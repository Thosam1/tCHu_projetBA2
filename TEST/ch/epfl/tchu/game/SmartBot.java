package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

import java.util.*;


/**
 * @author Thösam Norlha-Tsang (330163)
 * class SmartBot, représente un "joueur" joué par l'ordinateur suivant un algorithme
 */

public class SmartBot implements Player {

    private final List<Integer> DIFFICULTY = List.of(0, 1, 2);  // ajouter dans la classe constante
    private int difficulty = 1;

    private PlayerId id;
    private String name;
    private PlayerId otherPlayerId;
    private TurnKind nextTurnKind;
    private int drawSlot;
    private int additionalCardsIndex;
    private static final int TURN_LIMIT = 1000;

    private int drawCards10TimesOutOf20 = 0;

    private int ticketTurn = 0;
    private final int MAX_TICKET_TURN = 5;  // maximum de fois qu'on tire un ticket

    private final Random rng;
    // Toutes les routes de la carte
    private final List<Route> allRoutes;

    private int turnCounter;
    private PlayerState ownState;
    private PublicGameState gameState;

    // Lorsque nextTurn retourne CLAIM_ROUTE
    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;

    // Pour les tickets
    private SortedBag<Ticket> chosenInitialTickets = SortedBag.of();

    public SmartBot(long randomSeed, List<Route> allRoutes, PlayerId id, String name) {
        //precondition bound pour la difficulté
        this.rng = new Random(randomSeed);
        this.allRoutes = List.copyOf(allRoutes);
        this.turnCounter = 0;
        this.id = id;
        otherPlayerId = id.next();
        this.name = name;
        this.nextTurnKind = TurnKind.DRAW_CARDS;
        this.drawSlot = 0;
        this.additionalCardsIndex = 0;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
//        System.out.println(playerNames + "playerId 1 is : " + playerNames.get(PlayerId.PLAYER_1));
        System.out.println(id.name() + " name is " + name + "; playerNames have been communicated to this player, " + "those names are : " + Collections.singletonList(playerNames));
        //TODO parfois c'est inversé mais ça change rien au fonctionnement du programme "{PLAYER_2=Aymeric, PLAYER_1=Thösam}playerId 1 is : Thösam"
        System.out.println(" -   -   -   -   -");
    }

    @Override
    public void receiveInfo(String info) {
        System.out.println(name + " has received the info : | " + info);
        System.out.println(" -   -   -   -   -");
    }

    @Override
    public String updateChat(String chatToAdd, boolean returnAvailable) {
        return null;
    }


    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        this.gameState = newState;
        this.ownState = ownState;
        System.out.println(name + " has received the updateState : | " + ownState);
        System.out.println(" -   -   -   -   -");
    }


    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) { // communiquer les billets qu'il reçoit initialement
        System.out.println("Pour " + name + ", voici les " + tickets.size() + " tickets reçu initialement : " + tickets.toList().toString());               // choix stratégique possible...

        List<Ticket> allTicketList = tickets.toList();
        List<Ticket> ticketList = new ArrayList<>();
        ticketList.add(allTicketList.get(0));
        ticketList.add(allTicketList.get(1));
        ticketList.add(allTicketList.get(2));

        chosenInitialTickets = chosenInitialTickets.union(SortedBag.of(ticketList));
        System.out.println(name + " a choisi " + chosenInitialTickets.size() + " tickets");
        System.out.println(" -   -   -   -   -");
    }


    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        System.out.println("Pour " + name + ", voici les " + chosenInitialTickets.size() + " tickets choisi initialement : " + chosenInitialTickets.toList().toString());
        System.out.println(" -   -   -   -   -");
        return chosenInitialTickets;
    }   // va retourner les 3 premiers tickets à choix


    @Override
    public TurnKind nextTurn() {
        turnCounter += 1;
        /**
         *  décider l'action à effectuer parmi les trois possibles
         */


        /**
         *  Détermine les routes dont le bot peut s'emparer
         */
        List<Route> claimableRoutes = new ArrayList<>();
        for(Route r : allRoutes){
            if(ownState.canClaimRoute(r) && !ownState.routes().contains(r) && !gameState.playerState(otherPlayerId).routes().contains(r)){    // avec le "contient" ???
                claimableRoutes.add(r);
            }
        }

        /**
         *  Le bot prends des cartes 10 tours d'affilées, pour avoir de quoi prendre des routes
         */
        if(drawCards10TimesOutOf20 % 20 <=9) {
            ++drawCards10TimesOutOf20;
            return TurnKind.DRAW_CARDS;
        }
        ++drawCards10TimesOutOf20;

        /**
         *  Ensuite le bot va se décider de quelle action faire entre 1) se saisir d'une route, tirer des cartes ou prendre un ticket
         */
        if(!claimableRoutes.isEmpty()) {    // s'emparer d'une route
            System.out.println("    Le joueur : " + name + " va s'emparer d'une route");

            Route route = getNextRouteToClaim(difficulty, claimableRoutes);
            List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

            routeToClaim = route;
            initialClaimCards = cards.get(0);   // get an error and size = 0    // TODO retourner les initialClaimCards en fonction de la difficulté (le bot décide quelles cartes il veut joueur, ex en regardant les couleurs des routes de ses tickets, ou du joueur adverse par exemple)
            //TODO grosse diff entre (0) et (rng.nextInt(cards.size()))
            return TurnKind.CLAIM_ROUTE;
        } else {
//            return DrawTicketOrCard(difficulty);
//            nextTurnKind = ((turnCounter)%3 == 0) ? TurnKind.DRAW_TICKETS : TurnKind.DRAW_CARDS;  // lol en piochant trop de ticket on perd :(
            //TODO selon la difficulté, différents behavior pour la pioche des tickets (checker si on possède plusieurs routes reliées par exemple et pas de ticket, (piocher seulement vers tour 15))
            if(gameState.canDrawTickets() && nextTurnKind == TurnKind.DRAW_TICKETS) {
                System.out.println("    Le joueur : " + name + " va tirer des tickets");
                nextTurnKind = TurnKind.DRAW_CARDS;
                return TurnKind.DRAW_TICKETS;
            }else{
                System.out.println("    Le joueur : " + name + " va tirer des cartes");
                nextTurnKind = (ticketTurn <= MAX_TICKET_TURN) ? TurnKind.DRAW_TICKETS : TurnKind.DRAW_CARDS;   // une tirer un maximum de 4 fois par partie
                return TurnKind.DRAW_CARDS;
            }
        }


    }


    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) { // préférence en fonction des routes déjà prises par le joueur et le joueur adverse
        int randomInt = rng.nextInt(3) + 1;



        // for each option, iterate over list of trips, compare it to the routes station, if other guy doesnt have any of those routes pick it, else continue, by default pick one random
//        for (Ticket t : options){
//            t.
//        }

        // by default
        SortedBag.Builder<Ticket> chosenTickets = new SortedBag.Builder<>();
//        for(int i = 0; i<randomInt; ++i) {
//            chosenTickets.add(options.get(i));
//        }
        int maxPoints = -3000;
        int secMax = -3000;
        Ticket bestTicket = null;
        Ticket secBest = null;
        for(Ticket t : options){
            int curPoints = chooseTicketPoints(t);
            maxPoints = (curPoints >= maxPoints) ? curPoints : maxPoints;
            bestTicket = (curPoints >= maxPoints) ? t : bestTicket;

            secMax = (curPoints < maxPoints && curPoints >= secMax) ? curPoints : secMax;
            secBest = (curPoints < maxPoints && curPoints >= secMax) ? t : secBest;
        }
        chosenTickets.add(bestTicket);   // only one ticket
//        chosenTickets.add(secBest); // maybe randomize ?
        return chosenTickets.build();
    }

    @Override
    public int drawSlot() {

        List<Route> possibleRoutes = new ArrayList<>();
        for(Route r : allRoutes){
            if(couldClaimRoute(r) && !ownState.routes().contains(r) && !gameState.playerState(otherPlayerId).routes().contains(r)){    // avec le "contient" ???
                possibleRoutes.add(r);
            }
        }

        if(possibleRoutes.size() != 0){
            // Counter for all routes
            List<Card> memory = new ArrayList<Card>();  // sort les dans l'ordre, si aucune n'est dans les face up, draw une carte de la pioche
            List<Card> sorted = new ArrayList<Card>();  // most = index 0, etc...

            for(Route r : possibleRoutes){
                    memory.add(oneMostMissingCard(r));
            }

//            Card toReturn = Card.LOCOMOTIVE;    // à préferer par défault
//            int occurences = 0;
//            for(Card c : Card.ALL){
//                int curOc = Collections.frequency(memory, c);
//                if(occurences < curOc){
//                    occurences = curOc;
//                    toReturn = c;
//                }
//            }
            // maybe make a ranking algorithm / sorting in order for to chose between the faceUpCards

            Set<Card> choices = SortedBag.of(memory).toSet();
            if(choices.size() > 0){
                for(Card c : choices){
                    if(gameState.cardState().faceUpCards().contains(c)){
                        return gameState.cardState().faceUpCards().indexOf(c);
                    }
                }
            }else{
                return Constants.DECK_SLOT;
            }


        }else{
            return Constants.DECK_SLOT;
        }
        return Constants.DECK_SLOT;
    }
    @Override
    public Route claimedRoute() {
        return routeToClaim;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return initialClaimCards;
    }


    /**TODO implémenter le scénario ou il y a des cartes additionnelles à jouer*/
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        if (additionalCardsIndex % 2 == 0) System.out.println(options.get(0).toString());
        ++additionalCardsIndex;
        return (additionalCardsIndex % 2 == 0) ? options.get(0) : SortedBag.of();
    }

    private boolean couldClaimRoute(Route route){  // if there are some with one card away...
        return (ownState.carCount() >= route.length());
    }
    private Card oneMostMissingCard(Route route){
        List<SortedBag<Card>> cardList = new ArrayList<>(route.possibleClaimCards());
        List<Card> memory = new ArrayList<Card>();

        for(SortedBag<Card> s : cardList){
            for(int i = 0; i < s.size(); i++){
                List<Card> t = SortedBag.of(s).toList();    // nouvelle instance et pas une copie par référence
                Card temp = Card.of(t.get(i).color());
                t.remove(i);

                SortedBag<Card> cardBag = SortedBag.of(t);

                if(ownState.cards().contains(cardBag)){ // then we add this card to the counter !!!!
                    memory.add(temp);
                }
            }
        }

        Card toReturn = Card.LOCOMOTIVE;    // à préferer par défault
        int occurences = 0;
        for(Card c : Card.ALL){
            int curOc = Collections.frequency(memory, c);
            if(occurences < curOc){
                occurences = curOc;
                toReturn = c;
            }
        }

        // now we should return the card with the most counts for this route
        return toReturn;
    }


    /**
     *      -   -   -   -   -   -   - Méthodes principales utilisées dans nextTurn  -   -   -   -   -   -   -   -   -   -   -
     */

    /**
     * Le rôle est de retourner/choisir une route selon la difficulté voulue par le joueur
     * @param difficulty
     * @param claimableRoutes
     * @return
     */
     private Route getNextRouteToClaim(int difficulty, List<Route> claimableRoutes){
            Preconditions.checkArgument(DIFFICULTY.contains(difficulty));
            Route route = ChMap.routes().get(4);    // or null - here overground one yellow

            if(difficulty == 0){
                /**
                 *  here we just pick one random route in the claimable routes
                 */
                int routeIndex = rng.nextInt(claimableRoutes.size());
                route = claimableRoutes.get(routeIndex);

            }else if(difficulty == 1){
                /**
                 *  Içi on choisit la route qui rapporte le plus de points (en fonction des tickets détenus par le joueur) parmis les routes les plus longues qu'on peut saisir
                 */
                List<Route> longestRoutes = longestRoutesInList(claimableRoutes);
                route = longestRoutes.get(rng.nextInt(longestRoutes.size()));   // choisit une route random parmi les plus longues
                //TODO grosse diff entre (0) < rng.nextInt(longestRoutes.size())

                /**
                 *  Si une route pourrait rapporter plus de points (qui fait partie des tickets détenu par le joueur)
                 */
                int maxPoints = -1000;   // parce que parfois les points rapportés par les tickets sont négatif
                for(Route r : longestRoutes){
                    int curPoints = possibleTicketPoints(r);
                    maxPoints = (curPoints > maxPoints) ? curPoints : maxPoints;
                    route = (curPoints > maxPoints) ? r : route;
                }

            }else{
                //TODO à faire le niveau 2 - hard

                int routeIndex = rng.nextInt(claimableRoutes.size());
                route = claimableRoutes.get(routeIndex);
            }
            return route;
        }

    private TurnKind DrawTicketOrCard(int difficulty){
         if(difficulty == 0){
             if(gameState.canDrawTickets() && nextTurnKind == TurnKind.DRAW_TICKETS) {
                 System.out.println("    Le joueur : " + name + " va tirer des tickets");
                 nextTurnKind = TurnKind.DRAW_CARDS;
                 return TurnKind.DRAW_TICKETS;
             }else{
                 System.out.println("    Le joueur : " + name + " va tirer des cartes");
                 nextTurnKind = (ticketTurn <= MAX_TICKET_TURN) ? TurnKind.DRAW_TICKETS : TurnKind.DRAW_CARDS;   // une tirer un maximum de 4 fois par partie
                 return TurnKind.DRAW_CARDS;
             }
         }else if(difficulty == 1){
             if(gameState.canDrawTickets() && nextTurnKind == TurnKind.DRAW_TICKETS) {
                 System.out.println("    Le joueur : " + name + " va tirer des tickets");
                 nextTurnKind = TurnKind.DRAW_CARDS;
                 return TurnKind.DRAW_TICKETS;
             }else{
                 System.out.println("    Le joueur : " + name + " va tirer des cartes");
                 nextTurnKind = (ticketTurn <= MAX_TICKET_TURN) ? TurnKind.DRAW_TICKETS : TurnKind.DRAW_CARDS;   // une tirer un maximum de 4 fois par partie
                 return TurnKind.DRAW_CARDS;
             }
         }else{
             if(gameState.canDrawTickets() && nextTurnKind == TurnKind.DRAW_TICKETS) {
                 System.out.println("    Le joueur : " + name + " va tirer des tickets");
                 nextTurnKind = TurnKind.DRAW_CARDS;
                 return TurnKind.DRAW_TICKETS;
             }else{
                 System.out.println("    Le joueur : " + name + " va tirer des cartes");
                 nextTurnKind = (ticketTurn <= MAX_TICKET_TURN) ? TurnKind.DRAW_TICKETS : TurnKind.DRAW_CARDS;   // une tirer un maximum de 4 fois par partie
                 return TurnKind.DRAW_CARDS;
             }
         }
    }

    /**
     *      -   -   -   -   -   -   - Méthodes secondaires utilisées dans nextTurn  -   -   -   -   -   -   -   -   -   -
     */

    /**
     * @param route
     * @return Le nombre de points que pourrait rapporter la route, si le joueur s'en empare
     */
    private int possibleTicketPoints(Route route){
        List<Route> routes = new ArrayList<>(ownState.routes());
        routes.add(route);

        int idMax = -1;
        int ticketPoints = 0;
        for(Route r : routes){
            idMax = Math.max(Math.max(r.station1().id(), r.station2().id()), idMax);
        }

        StationPartition.Builder builder = new StationPartition.Builder(idMax+1);

        for (Route r : routes) {
            builder.connect(r.station1(), r.station2());
        }
        StationPartition partition = builder.build();

        for (Ticket ticket : ownState.tickets()) {
            ticketPoints += ticket.points(partition);
        }
        return ticketPoints;
    }


    /**
     *  ---------------------------------------
     */

    /**
     * @param t
     * @return Le nombre de points que rapporterait le ticket en fonction des routes détenus actuellement
     */
    private int chooseTicketPoints(Ticket t){
        int idMax = -1;
        int ticketPoints = 0;
        for(Route r : ownState.routes()){
            idMax = Math.max(Math.max(r.station1().id(), r.station2().id()), idMax);
        }
        StationPartition.Builder builder = new StationPartition.Builder(idMax+1);

        for (Route r : ownState.routes()) {
            builder.connect(r.station1(), r.station2());
        }
        StationPartition partition = builder.build();
        ticketPoints += t.points(partition);
        return ticketPoints;
    }

    /**
     *  -   -   -   -   -   -   -   -   -   -   -   -   -   -   - Quelques méthodes utiles, pour rendre le code plus lisible en haut -   -   -   -   -   -   -   -
     */

    /**
     * @param routeList
     * @return Les routes les plus longues de la liste donnée
     */
    private List<Route> longestRoutesInList(List<Route> routeList){
        List<Route> longestRoutes = new ArrayList<>(routeList);
        int maxLength = 0;
        for(Route r : routeList){ // to get the longest distance/length
            if(maxLength < r.length()){
                maxLength = r.length();
            }
        }
        for(Route r : routeList){
            if(maxLength == r.length()){
                longestRoutes.add(r);
            }
        }
        return longestRoutes;
    }

    // définir des getter pour voir à la fin de l'éxecution

    // ou en fonction des méthodes appelées, junit ! plus compliqué

    //override receive info - redéfinir

    // dans cette simulation le joueur ne tire jamais de ticket, comment vérifier ça ???
}
