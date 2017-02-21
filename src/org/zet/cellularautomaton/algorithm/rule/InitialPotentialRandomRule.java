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

import java.util.ArrayList;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.results.Action;
import org.zet.cellularautomaton.results.DieAction;
import org.zet.cellularautomaton.results.VoidAction;
import org.zetool.rndutils.RandomUtils;

/**
 * Sets a random exit to an individual.
 */
public class InitialPotentialRandomRule extends AbstractInitialRule {

    /**
     * @param cell the cell
     * @return 
     */
    @Override
    protected Action onExecute(EvacCellInterface cell) {
        ArrayList<Potential> exits = new ArrayList<>();
        es.getCellularAutomaton().getExits().stream().filter(
                exit -> (es.getCellularAutomaton().getPotentialFor(exit).hasValidPotential(cell) && es.getCellularAutomaton().getPotentialFor(exit).getPotential(cell) >= 0)).forEach(exit -> exits.add(es.getCellularAutomaton().getPotentialFor(exit)));
        
        if( exits.isEmpty() ) {
            return new DieAction(cell, DeathCause.EXIT_UNREACHABLE, cell.getState().getIndividual());
        } else {
            int numberOfExits = exits.size();
            RandomUtils random = RandomUtils.getInstance();
            int randomExitNumber = random.getRandomGenerator().nextInt(numberOfExits);
            es.propertyFor(cell.getState().getIndividual()).setStaticPotential(exits.get(randomExitNumber));
        }
        return VoidAction.VOID_ACTION;
    }
}
