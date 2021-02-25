// Rigel stage 1

package game.test.ch.epfl.tchu;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.Preconditions;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PreconditionsTest {
    @Test
    void checkArgumentSucceedsForTrue() {
        Preconditions.checkArgument(true);
    }

    @Test
    void checkArgumentFailsForFalse() {
        assertThrows(IllegalArgumentException.class, () -> {
            Preconditions.checkArgument(false);
        });
    }
}
