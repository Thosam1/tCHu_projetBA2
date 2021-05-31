package ch.epfl.tchu.gui;

import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public final class Stage11Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {


        System.out.println(StringsFr.DRAW);
        String draw = StringsFr.DRAW.replace("%s", "")
                .replace("\n", "");
        String[] arrayDraw = draw.split(" ");
        for(String s : arrayDraw){
            System.out.println(s);
        }

        System.out.println(StringsFr.WINS);
        String wins = StringsFr.WINS.replace("%s", "")
                .replace("\n", "");
        String[] arrayWins = wins.split(" ");
        for(String s : arrayWins){
//            if(s.isBlank()){continue;}
            System.out.println(s);
        }


      SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
      Map<PlayerId, String> names =
        Map.of(PlayerId.PLAYER_1, "Ada", PlayerId.PLAYER_2, "Charles");
      Map<PlayerId, Player> players =
        Map.of(PlayerId.PLAYER_1, new GraphicalPlayerAdapter(),
           PlayerId.PLAYER_2, new GraphicalPlayerAdapter());
      Random rng = new Random();
      new Thread(() -> Game.play(players, names, tickets, rng))
        .start();
    }

    public static final class CheckJavaFx {
      public static void main(String[] args) {
        Color c = Color.RED;
        System.out.println(c.getRed());
      }
    }
}