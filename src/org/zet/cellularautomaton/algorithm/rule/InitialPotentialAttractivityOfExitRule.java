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
import java.util.List;
import java.util.stream.Stream;
import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.results.Action;
import org.zet.cellularautomaton.results.DieAction;
import org.zet.cellularautomaton.results.VoidAction;

/**
 * This rule changes the Individuals StaticPotential. It chooses the admissible StaticPotential with the highest
 * attractivity value. A StaticPotential is admissible, if it leads the Individual to an ExitCell. Be careful using this
 * rule: Using this rule excessively might have the effect that all individuals run to the same exit with the highest
 * attractivity value!
 *
 * @author Jan-Philipp Kappmeier
 * @author Marcel Preuß
 */
public class InitialPotentialAttractivityOfExitRule extends AbstractInitialRule {

    /**
     * The concrete method changing the individuals StaticPotential. For a detailed description read the class
     * description above.
     *
     * @param cell
     * @return 
     */
    @Override
    protected Action onExecute(EvacCellInterface cell) {
        List<Exit> staticPotentials = new ArrayList<>(es.getCellularAutomaton().getExits());
        Exit initialPotential = initialPotential(staticPotentials.stream(), cell);
        if (initialPotential == null) {
            return new DieAction(cell, DeathCause.EXIT_UNREACHABLE, cell.getState().getIndividual());
        } else {
            assignMostAttractivePotential(staticPotentials, initialPotential, cell);
        }
        return VoidAction.VOID_ACTION;
    }

    /**
     * Find any admissible StaticPotential for this Individual
     *
     * @param staticPotentials list of static potentials
     * @param individual
     * @return
     */
    private Exit initialPotential(Stream<Exit> staticPotentials, EvacCellInterface cell) {
        return staticPotentials.filter(exit -> es.getCellularAutomaton().getPotentialFor(exit).hasValidPotential(cell)).findAny().orElse(null);
    }

    /**
     * Find the best admissible StaticPotential for the individual on a cell.
     *
     * @param exits a list of all static potentials
     * @param referencePotential some reference potential
     * @param cell the cell
     */
    private void assignMostAttractivePotential(List<Exit> exits, Exit referencePotential, EvacCellInterface cell) {
        Exit mostAttractivePotential = referencePotential;
        for (Exit exit : exits) {
            if ((exit.getAttractivity() > referencePotential.getAttractivity())
                    && (es.getCellularAutomaton().getPotentialFor(exit).hasValidPotential(cell))) {
                mostAttractivePotential = exit;
            }
        }
        es.propertyFor(cell.getState().getIndividual()).setStaticPotential(es.getCellularAutomaton().getPotentialFor(mostAttractivePotential));
    }
}
