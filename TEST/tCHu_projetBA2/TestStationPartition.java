package tCHu_projetBA2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.game.Station;
import ch.epfl.tchu.game.StationPartition;

class TestStationPartition {
    
    @Test
    public void TestThrowsExceptionWhenNegative(){
        assertThrows(IllegalArgumentException.class, () -> {
           new StationPartition.Builder(-1);
          });}
    
    @Test
    public void TestBuilder() {
        StationPartition.Builder builder = new StationPartition.Builder(16);
        builder.connect(new Station(0,"station0"), new Station(3,"station1"));
        builder.connect(new Station(1,"station1"), new Station(2, "station2"));
        builder.connect(new Station(2,"station1"), new Station(6, "station2"));
        builder.connect(new Station(6,"station1"), new Station(0, "station2"));
        builder.connect(new Station(0,"station1"), new Station(6, "station2"));
        builder.connect(new Station(7,"station1"), new Station(6, "station2"));
        builder.connect(new Station(9,"station1"), new Station(10, "station2"));
        builder.connect(new Station(9,"station1"), new Station(11, "station2"));
        builder.connect(new Station(13,"station1"), new Station(9, "station2"));
        builder.connect(new Station(12,"station1"), new Station(14, "station2"));
        builder.connect(new Station(1,"station1"), new Station(4, "station2"));
        StationPartition partition = builder.build();
        assertEquals(true, partition.connected(new Station (10,""), new Station (11,"")));
        assertEquals(false, partition.connected(new Station(0,""), new Station(14,"")));
        assertEquals(true, partition.connected(new Station(6,""), new Station(6,"")));
        assertEquals(true, partition.connected(new Station(20,"station20"), new Station(20, "station20")));
        assertEquals(false, partition.connected(new Station(20,"station20"), new Station(19, "station19")));
        assertEquals(false, partition.connected(new Station(20,"station20"), new Station(0, "station20")));

        
    }
}
