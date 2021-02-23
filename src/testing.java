import ch.epfl.tchu.*;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;

public class testing {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("- - - - - C O L O R S - - - - -");
        System.out.println("All colors list : " + Color.ALL);
        System.out.println("All colors count : " + Color.COUNT);
        System.out.println();

        System.out.println("- - - - - C A R D - - - - -");
        System.out.println("All cards list : " + Card.ALL);
        System.out.println("All cards count : " + Card.COUNT);
        System.out.println("All cards color : " + Card.CARS);

        System.out.println("This is a blue card/wagon : " + Card.of(Color.BLUE) + " and this is a locomotive : " + Card.of(null));
        System.out.println("The blue color of this card is : " + Card.BLUE.color());;
        System.out.println("The color of the locomotive card is : " + Card.LOCOMOTIVE.color());

    }
}
