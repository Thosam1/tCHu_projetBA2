package ch.epfl.tchu;

import ch.epfl.tchu.game.*;

import java.util.*;


/**
 * @author Thösam Norlha-Tsang (330163)
 * class SmartBot, représente un "joueur" joué par l'ordinateur suivant un algorithme
 */

public class SmartBot implements Player {

    private PlayerId id;
    private String name;
    private PlayerId otherPlayerId;
    private TurnKind nextTurnKind;
    private int drawSlot;
    private int additionalCardsIndex;
    private static final int TURN_LIMIT = 1000;

    private int drawCards10TimesOutOf20 = 0;

    private int ticketTurn = 0;
    private final int MAX_TICKET_TURN = 5;

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
        System.out.println(id.name() + " name is " + name + "; playerNames have been communicated to this player, " + "those names are : " + Collections.singletonList(playerNames));
        System.out.println(" -   -   -   -   -");
    }

    @Override
    public void receiveInfo(String info) {
        System.out.println(name + " has received the info : | " + info);
        System.out.println(" -   -   -   -   -");
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
        System.out.println("Pour " + name + ", voici les " + tickets.size() + " tickets reçu initialement : " + tickets.toList().toString());

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
    public TurnKind nextTurn() { // savoir quelle action le joueur courant désire effectuer parmi les trois possibles
        turnCounter += 1;
        if (turnCounter > TURN_LIMIT)
            throw new Error("Trop de tours joués !");

        // Détermine les routes dont ce joueur peut s'emparer

        List<Route> claimableRoutes = new ArrayList<>();

        for(Route r : allRoutes){
            if(ownState.canClaimRoute(r) && !ownState.routes().contains(r) && !gameState.playerState(otherPlayerId).routes().contains(r)){    // avec le "contient" ???
                claimableRoutes.add(r);
            }
        }
        if(drawCards10TimesOutOf20 % 20 <=9) { // prends des tickets 10 tours d'affilées avant de tirer des cartes 10 tours d'affilées ?
            ++drawCards10TimesOutOf20;
            return TurnKind.DRAW_CARDS;
        }
        ++drawCards10TimesOutOf20;
        if(!claimableRoutes.isEmpty()) {    // s'emparer d'une route
            System.out.println("    Le joueur : " + name + " va s'emparer d'une route");

//            // choisir une route au hasard
//            int routeIndex = rng.nextInt(claimableRoutes.size());
//
//            Route route = claimableRoutes.get(routeIndex);

                // choisir une route en fonction des tickets    !!!!!!!!!!!
            //version basique, juste regarder les gares et les tickets
            Route route = ChMap.routes().get(4);    // or null - here overground one yellow
            int maxPoints = -1000;   // because sometimes the ticket points is negative

            List<Route> longestRoutes = new ArrayList<>();
            int maxLength = 0;
            for(Route r : claimableRoutes){ // to get the longest distance
                if(maxLength < r.length()){
                    maxLength = r.length();
                }
            }
            for(Route r : claimableRoutes){
                if(maxLength == r.length()){
                    longestRoutes.add(r);
                }
            }
            route = longestRoutes.get(0);

            for(Route r : longestRoutes){
                int curPoints = possibleTicketPoints(r);
                maxPoints = (curPoints > maxPoints) ? curPoints : maxPoints;
                route = (curPoints > maxPoints) ? r : route;
            }

            List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

            if(cards.size() == 0){
                // choisir une route au hasard
                int routeIndex = rng.nextInt(claimableRoutes.size());
                route = claimableRoutes.get(routeIndex);
                cards = ownState.possibleClaimCards(route);
            }

            //TEST J aimerai des routes de longueurs différentes ce qui n est pas le cas avec rng = 1
            System.out.println("    routesize: " + route.length());
            routeToClaim = route;
//            System.out.println("Size : " + cards.size());
            initialClaimCards = cards.get(0);   // get an error and size = 0

            return TurnKind.CLAIM_ROUTE;
        } else {
//            nextTurnKind = ((turnCounter)%3 == 0) ? TurnKind.DRAW_TICKETS : TurnKind.DRAW_CARDS;  // lol sans piocher des tickets on perd :(

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

    // définir des getter pour voir à la fin de l'éxecution

    // ou en fonction des méthodes appelées, junit ! plus compliqué

    //override receive info - redéfinir

    // dans cette simulation le joueur ne tire jamais de ticket, comment vérifier ça ???
}
