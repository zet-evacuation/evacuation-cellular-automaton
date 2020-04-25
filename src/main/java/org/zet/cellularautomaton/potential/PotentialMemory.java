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

import java.util.Objects;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 * A tuple of {@link StaticPotential}s and the Individuals distance from that ExitCell, to which the
 * StaticPotentials refers. This class/tuple implements the interface Comparable in order to sort a collection of tuples
 * by their distance to the ExitCell, to which the StaticPotential refers.
 *
 * @param <P>
 * @author Marcel Preuß
 *
 */
public class PotentialMemory<P extends Potential> implements Comparable<PotentialMemory<P>> {

    /** The Individual's distance from the ExitCell the StaticPotential refers to. */
    private final int lengthOfWay;

    /** The StaticPotential of the tuple. */
    private final P potential;

    /** Initializes an potential memory that is empty. It is always smaller than all other potentials
     * 
     */
    public PotentialMemory() {
        lengthOfWay = -1;
        potential = null;
    }
    
    public PotentialMemory(EvacCellInterface c, P potential) {
        this.lengthOfWay = potential.getPotential(Objects.requireNonNull(c));
        this.potential = potential;
    }

    /**
     * Returns the lenthOfWay attribute.
     *
     * @return The lenthOfWay attribute
     */
    public int getLengthOfWay() {
        return this.lengthOfWay;
    }

    /**
     * Returns the StaticPotential attribute.
     *
     * @return The StaticPotential attribute
     */
    public P getStaticPotential() {
        return this.potential;
    }

    @Override
    public int hashCode() {
        return 31 * 3 + lengthOfWay;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PotentialMemory<?> other = (PotentialMemory<?>) obj;
        return this.lengthOfWay == other.lengthOfWay;
    }

    @Override
    public int compareTo(PotentialMemory<P> t) {
        if (t.getLengthOfWay() == this.getLengthOfWay()) {
            return 0;
        } else if (t.getLengthOfWay() < this.getLengthOfWay()) {
            return 1;
        } else {
            return -1;
        }
    }
}
