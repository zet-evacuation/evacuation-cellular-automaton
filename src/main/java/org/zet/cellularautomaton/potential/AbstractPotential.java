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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 * For this a HashMap associates for each EvacCell a potential as int value. It is kept abstract, because there are two
 * special kinds of potentials, such as StaticPotential and DynamicPotential.
 */
public abstract class AbstractPotential implements Potential {
    
    /** The maximum potential value returned for an empty potential. */
    public static final int INVALID = -1;
    /** A map from cells to their potential value. */
    protected Map<EvacCellInterface, Double> potential;
    /** Stores the maximal value of this potential map. */
    private double maxPotential = INVALID;

    /**
     * Create an empty potential.
     */
    public AbstractPotential() {
        potential = new HashMap<>();
    }

    /**
     * Associates the specified potential with the specified EvacCell in this AbstractPotential. If an EvacCell is
     * specified that exists already in this AbstractPotential the value will be overwritten. Otherwise a new mapping is
     * created.
     *
     * @param cell cell which has to be updated or mapped
     * @param i potential of the cell
     */
    public void setPotential(EvacCellInterface cell, double i) {
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
      maxPotential = potential.values().stream().mapToDouble(x -> x).max().orElse(INVALID);
    }

    @Override
    public int getPotential(EvacCellInterface cell) {
        return (int) Math.round(getPotentialDouble(cell));
    }

    @Override
    public double getPotentialDouble(EvacCellInterface cell) {
        if (hasValidPotential(cell)) {
            return potential.get(cell);
        }
        throw new IllegalArgumentException("Potential for " + cell + " not defined");
    }

    @Override
    public int getMaxPotential() {
        Double d = maxPotential;
        return (int) Math.round(d);
    }
    
    public double getMaxPotentialDouble() {
        return maxPotential;
    }

    /**
     * Removes the mapping for the specified EvacCell. The method throws {@code IllegalArgumentExceptions} if you try to
     * remove the mapping of a EvacCell that does not exists.
     *
     * @param cell A EvacCell that mapping you want to remove.
     * @throws IllegalArgumentException if the cell is not contained in the map
     */
    public void deleteCell(EvacCellInterface cell) {
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
     * Returns a set of all cells which are mapped by this potential.
     * 
     * It is secured that the elements in the set have the same ordering using a {@code SortedSet}. This is needed due
     * to the fact that the keys can have different order even if the values are inserted using default hashcodes.
     *
     * @return set of mapped cells
     */
    public Set<EvacCellInterface> getMappedCells() {
        SortedSet<EvacCellInterface> cells = new TreeSet<>(new EvacCellComparator());
        
        potential.keySet().stream().forEach(cell -> cells.add(cell));
        return cells;
    }

    @Override
    public boolean hasValidPotential(EvacCellInterface cell) {
        return potential.get(cell) != null;
    }
    
    private static class EvacCellComparator implements Comparator<EvacCellInterface> {

        @Override
        public int compare(EvacCellInterface c, EvacCellInterface o2) {            
            if (c.getX() == o2.getX()) {
                if (c.getY() == o2.getY()) {
                    return o2.hashCode() - c.hashCode();
                } else {
                    return o2.getY() - c.getY();
                }
            } else {
                return o2.getX() - c.getX();
            }
        }
    }
}
