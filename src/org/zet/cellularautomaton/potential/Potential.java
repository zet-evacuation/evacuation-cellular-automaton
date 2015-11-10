package org.zet.cellularautomaton.potential;

import org.zet.cellularautomaton.EvacCell;

/**
 * A {@code Potential} stores a distance value for each cell to an exit. The potential values are used to evaluate 
 * a route that the indiviuals take to the exit. Potential values are non-negative
 * 
 * @author Jan-Philipp Kappmeier
 */
public interface Potential {

    /**
     * Returns the potential of a specified {@link EvacCell}.
     *
     * @param cell the cell which potential should be returned
     * @return potential of the specified cell
     */
    public int getPotential(EvacCell cell);

    public double getPotentialDouble(EvacCell cell);

    public int getMaxPotential();

    /**
     * Checks whether a given cell has a valid potential. Especially a valid potential value is not
     * {@link #UNKNOWN_POTENTIAL_VALUE}.
     * @param cell the cell
     * @return {@code true} if the cell has a valid potential value, {@code false} otherwise
     */
    public boolean hasValidPotential(EvacCell cell);
}
