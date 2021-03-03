package ch.epfl.tchu.game;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TrailTest {

    @Test
    void longestWithEmptyList(){
        List<Route> routes = null;
        Trail a = new Trail(null, null, 0, null);
        Trail b = Trail.longest(routes);

        assertTrue((a.station1() == b.station1() && (a.station2() == b.station2()) && a.length() == b.length()));
//        assertTrue(a.equals(b));
    }



    @Test
    void longestWithListContainingOneLengthiestRoute(){
        List<Route> allRoutes = ChMap.routes();
        Route a = allRoutes.get(66);
        Route b = allRoutes.get(65);

        List<Route> testedRoutes = new ArrayList<>();
        testedRoutes.add(a);
        testedRoutes.add(b);

        Trail longestTrail = Trail.longest(testedRoutes);

        // a b
        int expectedLongestLength = a.length() + b.length();

        int lengthFound = longestTrail.length();
        Station st1Found = longestTrail.station1();
        Station st2Found = longestTrail.station2();

        assertTrue((expectedLongestLength == lengthFound) && ( (st1Found == ChMap.stations().get(31) && st2Found == ChMap.stations().get(26)) || (st1Found == ChMap.stations().get(26) && st2Found == ChMap.stations().get(31)) ) );

    }

    @Test
    void longestWithContainingMultipleLengthiestRoute(){
        List<Route> allRoutes = ChMap.routes();
        Route a = allRoutes.get(66);
        Route b = allRoutes.get(65);
        Route c = allRoutes.get(18);
        Route d = allRoutes.get(19);
        Route e = allRoutes.get(16);
        Route f = allRoutes.get(13);

        List<Route> testedRoutes = new ArrayList<>();
        testedRoutes.add(a);
        testedRoutes.add(b);
        testedRoutes.add(c);
        testedRoutes.add(d);
        testedRoutes.add(e);
        testedRoutes.add(f);

        Trail longestTrail = Trail.longest(testedRoutes);
        // e c b d f
        int expectedLongestLength = e.length() + c.length() + b.length() + d.length() + f.length();

        int lengthFound = longestTrail.length();
//        System.out.println("length expected : " + expectedLongestLength);
//        System.out.println("length calculated : " + expectedLongestLength);
//        System.out.println("actual longest trail found : " + longestTrail.toString());
//        System.out.println("expected station1 : " + longestTrail.station1() + " station2 : " + longestTrail.station2());

        assertTrue(expectedLongestLength == lengthFound);
    }

    @Test
    void testLength(){
        List<Route> allRoutes = ChMap.routes();
        Route a = allRoutes.get(66);
        Route b = allRoutes.get(65);
        Route d = allRoutes.get(19);
        Route e = allRoutes.get(16);
        Route f = allRoutes.get(13);

        List<Route> testedRoutes = new ArrayList<>();
        testedRoutes.add(a);
        testedRoutes.add(b);
        testedRoutes.add(d);
        testedRoutes.add(e);
        testedRoutes.add(f);

        Trail longestTrail = Trail.longest(testedRoutes);
//        System.out.println("-  -  -  Testing length()  -  -  -");
//        System.out.println("longest trail calculated : " + longestTrail.length());  // W T F !!!
//        System.out.println("a b d e : " + (a.length() + b.length() + d.length() + e.length()));
//        for(Route ate : testedRoutes){
//            System.out.println("station 1 : " + ate.station1() + " station 2 : " + ate.station2());
//        }
//
//        System.out.println("a b d f : " + (a.length() + b.length() + d.length() + f.length()));
//        System.out.println(longestTrail.toString());

        assertEquals(longestTrail.length(), 12);

    }

    @Test
    void testStation1(){
        List<Route> allRoutes = ChMap.routes();
        Route a = allRoutes.get(66);
        Route b = allRoutes.get(65);
        Route d = allRoutes.get(19);
        Route e = allRoutes.get(16);

        List<Route> testedRoutes = new ArrayList<>();
        testedRoutes.add(a);
        testedRoutes.add(b);
        testedRoutes.add(d);
        testedRoutes.add(e);

        Trail longestTrail = Trail.longest(testedRoutes);
        System.out.println(longestTrail.toString());
        System.out.println(longestTrail.station1());

        assertEquals(longestTrail.station1(), ChMap.stations().get(31));    // Yverdon ?

    }

    @Test
    void testStation2(){
        List<Route> allRoutes = ChMap.routes();
        Route a = allRoutes.get(66);
        Route b = allRoutes.get(65);
        Route d = allRoutes.get(19);
        Route e = allRoutes.get(16);

        List<Route> testedRoutes = new ArrayList<>();
        testedRoutes.add(a);
        testedRoutes.add(b);
        testedRoutes.add(d);
        testedRoutes.add(e);

        Trail longestTrail = Trail.longest(testedRoutes);
        assertEquals(longestTrail.station2(), ChMap.stations().get(16));    // Lucerne ?

    }

    @Test
    void testToStringWithNull(){
        Trail longestTrail = new Trail(null, null, 0, null);
        assertEquals(
                "(0)",
                longestTrail.toString()
        );
    }

    @Test
    void testToStringNormalTrail(){
        List<Route> allRoutes = ChMap.routes();
        Route a = allRoutes.get(66);
        Route b = allRoutes.get(65);
        Route d = allRoutes.get(19);
        Route e = allRoutes.get(16);

        List<Route> testedRoutes = new ArrayList<>();
        testedRoutes.add(a);
        testedRoutes.add(b);
        testedRoutes.add(d);
        testedRoutes.add(e);

        Trail longestTrail = Trail.longest(testedRoutes);

//        System.out.println(longestTrail.toString());
        assertTrue(longestTrail.toString().equals("Yverdon - Neuchâtel - Soleure - Berne - Lucerne (12)") || longestTrail.toString().equals("Lucerne - Berne - Soleure - Neuchâtel - Yverdon (12)"));
    }


}
