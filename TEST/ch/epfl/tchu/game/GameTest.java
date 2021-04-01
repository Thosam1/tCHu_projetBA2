package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import org.junit.jupiter.api.Test;

import java.util.*;

public class GameTest {
//    @Test
    @Test
    void testingIllegalExceptionFirstLessPlay(){

    }
    @Test
    void testingIllegalExceptionFirstMorePlay(){

    }
    @Test
    void testingIllegalExceptionSecondLessPlay(){

    }
    @Test
    void testingIllegalExceptionSecondMorePlay(){

    }
    @Test
    void testingIllegalExceptionBothLessPlay(){

    }
    @Test
    void testingIllegalExceptionBothMorePlay(){

    }


    @Test
    void testingPlay(){
        Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1, new TestPlayer(1, ChMap.routes(), PlayerId.PLAYER_1, "Thösam"), PlayerId.PLAYER_2, new TestPlayer(1, ChMap.routes(), PlayerId.PLAYER_2, "Aymeric"));
        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "Thösam", PlayerId.PLAYER_2, "Aymeric");
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Random rng = new Random(1);
        Game.play(players, playerNames, tickets, rng);
    }



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

    private final class TestPlayer implements Player {

        private PlayerId id;
        private String name;
        private PlayerId otherPlayerId;
        private TurnKind nextTurnKind;
        private int drawSlot;
        private int additionalCardsIndex;
        private static final int TURN_LIMIT = 1000;
        private int drawCards10TimesOutOf20 = 0;
        
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

        public TestPlayer(long randomSeed, List<Route> allRoutes, PlayerId id, String name) {
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
            System.out.println(name + " has received the updateState : | " + ownState.toString());
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
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer

            List<Route> claimableRoutes = new ArrayList<>();//    /* ... */;

            for(Route r : allRoutes){
                if(ownState.canClaimRoute(r) && !ownState.routes().contains(r) && !gameState.playerState(otherPlayerId).routes().contains(r)){    // avec le "contient" ???
                    claimableRoutes.add(r);
                }
            }
            
            if(drawCards10TimesOutOf20 % 20 <=10) {
                ++drawCards10TimesOutOf20;
                return TurnKind.DRAW_CARDS;
            }
            
            else {
                ++drawCards10TimesOutOf20;
                if (claimableRoutes.isEmpty()) {    // tire cartes si le joueur ne peut pas capturer de routes
                    
                    if(gameState.canDrawTickets() && nextTurnKind == TurnKind.DRAW_TICKETS) {
                        System.out.println("    Le joueur : " + name + " va tirer des tickets");
                        nextTurnKind = TurnKind.DRAW_CARDS;
                        return TurnKind.DRAW_TICKETS;
                  }
              
                  else {
                      System.out.println("    Le joueur : " + name + " va tirer des cartes");
                      nextTurnKind = TurnKind.DRAW_TICKETS;
                      return TurnKind.DRAW_CARDS;
                      }
                
                } else {
                    System.out.println("    Le joueur : " + name + " va s'emparer d'une route");

                    // choisir une route au hasard
                    int routeIndex = rng.nextInt(claimableRoutes.size());
                
                    Route route = claimableRoutes.get(routeIndex);
                
                    //TEST J aimerai des routes de longueurs différentes ce qui n est pas le cas avec rng = 1
                    System.out.println("routesize: " + route.length());
                
                    List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                    routeToClaim = route;
                
                    initialClaimCards = cards.get(0);
                    return TurnKind.CLAIM_ROUTE;
            }
            }
        }


        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            int randomInt = rng.nextInt(3) + 1;
            
            SortedBag.Builder<Ticket> chosenTickets = new SortedBag.Builder<>();
            for(int i = 0; i<randomInt; ++i) {
                chosenTickets.add(options.get(i));
            }
            
            return chosenTickets.build();
        }

        @Override
        public int drawSlot() {
            ++drawSlot;
            return (drawSlot)%2;
        }   //On cherche a alterner entre 0 et 1; //voir pourquoi les cartes sont toujours visible

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

        // définir des getter pour voir à la fin de l'éxecution

        // ou en fonction des méthodes appelées, junit ! plus compliqué

        //override receive info - redéfinir

        // dans cette simulation le joueur ne tire jamais de ticket, comment vérifier ça ???
    }
}
