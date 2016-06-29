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

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 * Abstract class rules only used during initialization.
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractInitialRule extends AbstractEvacuationRule {
    /**
     * Checks, whether the rule is executable or not. The rule is applicable if there is an individual standing on the
     * cell and if the individual has not yet a potential assigned.
     *
     * @param cell the cell on which the rule should be executed
     * @return Returns true, if an Individual is standing on this cell, and moreover this Individual does not already
     * have a StaticPotential.
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        return !cell.getState().isEmpty() && es.propertyFor(cell.getState().getIndividual()).getStaticPotential() == null;
    }

}
