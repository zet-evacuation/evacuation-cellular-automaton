package org.zet.cellularautomaton;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Level;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class StairCellTest {
    
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
}
