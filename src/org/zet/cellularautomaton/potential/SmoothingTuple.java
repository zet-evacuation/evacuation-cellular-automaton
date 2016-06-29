/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.potential;

import org.zet.cellularautomaton.EvacCellInterface;

/**
 * The data structure used by the algorithm for calculating the static potentials.
 *
 * @author Matthias Woste
 *
 */
public class SmoothingTuple {
    private static final int SMOOTHING_FACTOR = 3;
    /** Reference to the cell. */
    private final EvacCellInterface cell;
    /** Potential potential. */
    private double potential;
    /** Real distance. */
    private double distanceValue;
    /** Number of the cells that could affect this cell. */
    private int numberOfParents;
    /** Sum of the potentials of the parent cells. */
    private double sumOfPorentialsOfParents;

    /**
     * Constructs a new SmoothingTuple for a cell.
     *
     * @param c the cell
     * @param potentialOfParent the initial parent potential
     * @param potentialDifference the distance (diagonal: 14, horizontal or vertical: 10)
     * @param initialDistance the distance of the cell (using the parent as next)
     */
    public SmoothingTuple(EvacCellInterface c, double potentialOfParent, int potentialDifference, double initialDistance) {
        cell = c;
        potential = potentialOfParent + potentialDifference;
        distanceValue = initialDistance;
        numberOfParents = 1;
        sumOfPorentialsOfParents = potentialOfParent;
    }

    /**
     * Updates the values of this tuple.
     *
     * @param potentialOfParent potential of the parent
     * @param potentialDifference distance between this cell and its parent (diagonal: 14, horizontal or vertical: 10)
     */
    public void addParent(double potentialOfParent, int potentialDifference) {
        potential = Math.min(potential, potentialOfParent + potentialDifference);
        
        numberOfParents++;
        sumOfPorentialsOfParents += potentialOfParent;
    }

    public void addDistanceParent(double distanceOfParent, double distance) {
        distanceValue = Math.min(distanceValue, distanceOfParent + distance);
    }

    /**
     * Returns the EvacCell of the SmoothingTuple.
     *
     * @return the EvacCell
     */
    public EvacCellInterface getCell() {
        return cell;
    }

    /**
     * Returns the Potential of the EvacCell specified by this SmoothingTuple.
     *
     * @return the potential
     */
    public double getValue() {
        return potential;
    }

    /**
     * Returns the distance of the EvacCell specified by this SmoothingTuple.
     *
     * @return the distance
     */
    public double getDistanceValue() {
        return distanceValue;
    }

    /**
     * Applies the smoothing-algorithm to this tuple. It's based on the formula:
     * potential = Math.round((3*potential+sumOfValuesOfParents)/(3+numberOfParents))
     */
    public void applySmoothing() {
        potential = (SMOOTHING_FACTOR * potential + sumOfPorentialsOfParents) / (SMOOTHING_FACTOR + numberOfParents);
    }
}
