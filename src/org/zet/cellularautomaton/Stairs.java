package org.zet.cellularautomaton;

import org.zetool.common.util.Direction8;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface Stairs {

    /**
     * Returns a speed factor if walking in the given direction.
     * 
     * @param direction the direction
     * @return the speed factor
     */
    public double getStairSpeedFactor(Direction8 direction);

}
