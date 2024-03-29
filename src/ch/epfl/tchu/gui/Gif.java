package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Color;

import java.util.List;
import java.util.Random;

public class Gif {

    // winners
    public enum Winners {
        LITTLEGIRLDANCING("WinnerGif/littleGirlDancing.gif"),   // works
        HAMSTER("WinnerGif/hamsterDance.gif"),  // works
        GECKO("WinnerGif/geckoThanos.gif");     // works


        /**
         *  Liste immuable contenant toutes les valeurs du type enum Winners, dans l'ordre
         */
        public static final List<Winners> ALL = List.of(Winners.values());

        /**
         *  Nombre total de valeurs dans la classe enum Winners
         */
        public static final int COUNT = Winners.ALL.size();

        private final String path;
        Winners(String path) { this.path = path;}  // constructeur privé

        /**
         * utilisée lors du pop up de fin du jeu sur l'écran du joueur s'il gagne
         * @return une string conetant le nom du ficher "ressources" correspondant
         */
        public static String pickRandomWinnerGif(){
            Random rdm = new Random();
            return ALL.get(rdm.nextInt(COUNT)).path();
        }

        /**
         * @return  retourne la path du type correspondant
         */
        public String path() {
            return path;
        }
    }

    //losers
    public enum Losers {
        LOSERTEXT("LoserGif/LoserText.gif"),
        DEALWITHIT("LoserGif/dealWithItLoser.gif"),
        PINGU("LoserGif/pinguLoser.gif");

        /**
         *  Liste immuable contenant toutes les valeurs du type enum Losers, dans l'ordre
         */
        public static final List<Losers> ALL = List.of(Losers.values());

        /**
         *  Nombre total de valeurs dans la classe enum Losers
         */
        public static final int COUNT = Losers.ALL.size();

        private final String path;
        Losers(String path) { this.path = path;}  // constructeur privé


        /**
         * utilisée lors du pop up de fin du jeu sur l'écran du joueur s'il perd
         * @return une string conetant le nom du ficher "ressources" correspondant
         */
        public static String pickRandomLoserGif(){
            Random rdm = new Random();
            return ALL.get(rdm.nextInt(COUNT)).path();
        }

        /**
         * @return  retourne la path du type correspondant
         */
        public String path() {
            return path;
        }
    }


}
