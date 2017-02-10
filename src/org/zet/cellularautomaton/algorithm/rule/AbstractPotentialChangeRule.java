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

package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.VoidAction;

/**
 * Abstract class for changing {@link Individual}s' static potentials.
 * @author Marcel Preuß
 *
 */
public abstract class AbstractPotentialChangeRule extends AbstractEvacuationRule<VoidAction> {
    /**
     *
     * @param cell
     * @return true, if the cange potential rule can be used
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        return !cell.getState().isEmpty() && !es.propertyFor(cell.getState().getIndividual()).isSafe()
                && wantsToChange(cell.getState().getIndividual());
    }
    
    protected abstract boolean wantsToChange(Individual i);

}
