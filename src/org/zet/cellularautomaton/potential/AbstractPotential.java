/* zet evacuation tool copyright (c) 2007-15 zet evacuation team
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.zet.cellularautomaton.EvacCell;

/**
 * For this a HashMap associates for each EvacCell a potential as int value. It is kept abstract, because there are two
 * special kinds of potentials, such as StaticPotential and DynamicPotential.
 */
public abstract class AbstractPotential implements Potential {
    /** The maximum potential value returned for an empty potential. */
    public static final int INALID = -1;
    /** A map from cells to their potential value. */
    protected Map<EvacCell, Double> potential;
    /** Stores the maximal value of this potential map. */
    private double maxPotential = INALID;

    /**
     * Create an empty potential.
     */
    public AbstractPotential() {
        potential = new HashMap<>();
    }

    /**
     * Associates the specified potential with the specified EvacCell in this AbstractPotential. If a EvacCell is
     * specified that exists already in this AbstractPotential the value will be overwritten. Otherwise a new mapping is
     * created.
     *
     * @param cell cell which has to be updated or mapped
     * @param i potential of the cell
     */
    public void setPotential(EvacCell cell, double i) {
        if (potential.containsKey(Objects.requireNonNull(cell))) {
            Double value = potential.get(cell);
            potential.remove(cell);
            if (getMaxPotential() == value.intValue()) {
                recomputeMaxPotential();
            }
        }
        potential.put(cell, i);
        maxPotential = Math.max(maxPotential, i);
    }
    
    private void recomputeMaxPotential() {
        maxPotential = INALID;
        for( Double d : potential.values()) {
            maxPotential = Math.max(maxPotential, d);
        }
    }

    @Override
    public int getPotential(EvacCell cell) {
        return (int) Math.round(getPotentialDouble(cell));
    }

    @Override
    public double getPotentialDouble(EvacCell cell) {
        if (hasValidPotential(cell)) {
            return potential.get(cell);
        }
        throw new IllegalArgumentException("Potential for " + cell + " not defined");
    }

    @Override
    public int getMaxPotential() {
        Double d = maxPotential;
        return d.intValue();
    }

    /**
     * Removes the mapping for the specified EvacCell. The method throws {@code IllegalArgumentExceptions} if you try to
     * remove the mapping of a EvacCell that does not exists.
     *
     * @param cell A EvacCell that mapping you want to remove.
     * @throws IllegalArgumentException if the cell is not contained in the map
     */
    public void deleteCell(EvacCell cell) {
        if (!potential.containsKey(Objects.requireNonNull(cell))) {
            throw new IllegalArgumentException("The Cell must be insert previously!");
        }
        Double value = potential.get(cell);
        potential.remove(cell);
        if( value.intValue() == getMaxPotential()) {
            recomputeMaxPotential();        
        }
    }

    /**
     * Returns a set of all cell which are mapped by this potential.
     * 
     * It is secured that the elements in the set have the same ordering using a {@code SortedSet}. This is needed due
     * to the fact that the keys can have different order even if the values are inserted using default hashcodes.
     *
     * @return set of mapped cells
     */
    public Set<EvacCell> getMappedCells() {
        SortedSet<EvacCell> cells = new TreeSet<>();
        potential.keySet().stream().forEach(cell -> cells.add(cell));
        return cells;
    }

    @Override
    public boolean hasValidPotential(EvacCell cell) {
        return potential.get(cell) != null;
    }
}
