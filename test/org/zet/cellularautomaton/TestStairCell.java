package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.util.function.Consumer;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TestStairCell {
    
    @Test
    public void initialization() {
        StairCell cell = new StairCell(0, 0);
        assertThat(cell.getSpeedFactorDown(), is(both(greaterThan(0.0)).and(lessThanOrEqualTo(1.0))));
        assertThat(cell.getSpeedFactorUp(), is(both(greaterThan(0.0)).and(lessThanOrEqualTo(1.0))));                
    }
    
    @Test
    public void testSpeedFactor() {
        StairCell cell = new StairCell(0, 0) {

            @Override
            public Level getLevel(Direction8 direction) {
                switch(direction) {
                    case Down:
                    case DownLeft:
                    case DownRight:
                        return Level.Higher;
                    case Top:
                    case TopLeft:
                    case TopRight:
                        return Level.Lower;
                    default:
                        return Level.Equal;
                }
            }
            
        };
        double FACTOR_UP = 0.3;
        double FACTOR_DOWN = 0.2;
        cell.setUpSpeedFactor(FACTOR_UP);
        cell.setDownSpeedFactor(FACTOR_DOWN);
        assertThat(cell.getStairSpeedFactor(Direction8.Down), is(closeTo(FACTOR_UP, 10e-8)));
        assertThat(cell.getStairSpeedFactor(Direction8.DownLeft), is(closeTo(FACTOR_UP, 10e-8)));
        assertThat(cell.getStairSpeedFactor(Direction8.DownRight), is(closeTo(FACTOR_UP, 10e-8)));
        assertThat(cell.getStairSpeedFactor(Direction8.Top), is(closeTo(FACTOR_DOWN, 10e-8)));
        assertThat(cell.getStairSpeedFactor(Direction8.TopLeft), is(closeTo(FACTOR_DOWN, 10e-8)));
        assertThat(cell.getStairSpeedFactor(Direction8.TopRight), is(closeTo(FACTOR_DOWN, 10e-8)));
        assertThat(cell.getStairSpeedFactor(Direction8.Right), is(closeTo(1.0, 10e-8)));
        assertThat(cell.getStairSpeedFactor(Direction8.Left), is(closeTo(1.0, 10e-8)));
    }
    
    @Test
    public void testFailure() {
        StairCell cell = new StairCell(0, 0);
        for( Double d : new double[] {0, -0.2, Math.nextDown(0.0), Math.nextUp(1)} ) {
            assertAssignment(cell::setDownSpeedFactor, d, false);
            assertAssignment(cell::setUpSpeedFactor, d, false);
        }
        assertAssignment(cell::setDownSpeedFactor, 1.0, true);
        assertThat(cell.getSpeedFactorDown(), is(closeTo(1, 10e-4)));
        assertAssignment(cell::setUpSpeedFactor, 1.0, true);
        assertThat(cell.getSpeedFactorDown(), is(closeTo(1, 10e-4)));
    }
    
    public static <T> void assertAssignment(Consumer<T> d , T value, boolean shouldAccept) {
        assertAssignment(d, value, shouldAccept, IllegalArgumentException.class);
    }
    
    
    public static <T, E extends Exception> void assertAssignment(Consumer<T> assignmentFunction,
            T value, boolean shouldAccept, Class<E> exceptionType) {
        try {
            assignmentFunction.accept(value);
        } catch(Exception ex) {
            if(shouldAccept) {
                throw new AssertionError("Should not fail for " + value);
            } else {
                assertThat(ex, is(instanceOf(exceptionType)));
                return;
            }
        }
        if(!shouldAccept) {
            throw new AssertionError("Should fail for " + value);
        }
    }

}
