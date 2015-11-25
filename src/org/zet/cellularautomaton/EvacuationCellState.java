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
package org.zet.cellularautomaton;

import java.util.Objects;

/**
 * Stores the state of a cell in an {@link EvacuationCellularAutomaton}. The state is defined by the individual
 * standing on the cell (or not) and its properties.
 * 
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationCellState {

    Individual individual;

    public EvacuationCellState(Individual individual) {
        this.individual = individual;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        if (!isEmpty()) {
            throw new IllegalStateException("Already occupied with individual " + this.individual);
        }
        this.individual = Objects.requireNonNull(individual, "Individual is null.");
    }
    
    public boolean isEmpty() {
        return individual == null;
    }

    void removeIndividual() {
        if(isEmpty()) {
            throw new IllegalStateException("Cannot remove individual, is empty!");
        }
        this.individual = null;
    }
}
